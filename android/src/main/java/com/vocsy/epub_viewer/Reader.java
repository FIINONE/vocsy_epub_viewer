package com.vocsy.epub_viewer;

import android.content.Context;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class Reader implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener,
            FolioReader.OnAddWordListener, FolioReader.TranslateAndCheckWordListener,
            FolioReader.TextToSpeechListener, FolioReader.OnDismissPopupListener {

    private ReaderConfig readerConfig;
    public FolioReader folioReader;
    private Context context;
    public MethodChannel.Result result;
    private EventChannel eventChannel;
    private EventChannel.EventSink pageEventSink;
    private BinaryMessenger messenger;
    private ReadLocator read_locator;
    private static final String PAGE_CHANNEL = "sage";

    private EventChannel.EventSink epubClosedSink;
    private EventChannel.EventSink addWordSink;
    private EventChannel.EventSink translateAndCheckSink;
    private EventChannel.EventSink textToSpeechSink;
    private EventChannel.EventSink onDismissPopupSink;

    Reader(Context context, BinaryMessenger messenger, ReaderConfig config, 
        EventChannel.EventSink sink, EventChannel.EventSink closeSink,
        EventChannel.EventSink addSink, EventChannel.EventSink sendWordSink,
        EventChannel.EventSink textSpeechSing, EventChannel.EventSink dismissSink) {
        this.context = context;
        readerConfig = config;

        getHighlightsAndSave();
        //setPageHandler(messenger);

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this)
                .setOnAddWordListener(this)
                .setTranslateAndCheckListener(this)
                .setTextToSpeechListener(this)
                .setOnDismissPopupListener(this);

        pageEventSink = sink;
        epubClosedSink = closeSink;
        addWordSink = addSink;
        translateAndCheckSink = sendWordSink;
        textToSpeechSink = textSpeechSing;
        onDismissPopupSink = dismissSink;
    }

    public void open(String bookPath, String lastLocation) {
        final String path = bookPath;
        final String location = lastLocation;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("SavedLocation", "-> savedLocation -> " + location);
                    if (location != null && !location.isEmpty()) {
                        ReadLocator readLocator = ReadLocator.Companion.fromJson(location);
                        folioReader.setReadLocator(readLocator);
                    }
                    folioReader.setConfig(readerConfig.config, true)
                            .openBook(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void close() {
        folioReader.close();
    }

    private void setPageHandler(BinaryMessenger messenger) {
//        final MethodChannel channel = new MethodChannel(registrar.messenger(), "page");
//        channel.setMethodCallHandler(new EpubKittyPlugin());
        Log.i("event sink is", "in set page handler:");
        eventChannel = new EventChannel(messenger, PAGE_CHANNEL);

        try {

            eventChannel.setStreamHandler(new EventChannel.StreamHandler() {

                @Override
                public void onListen(Object o, EventChannel.EventSink eventSink) {

                    Log.i("event sink is", "this is eveent sink:");

                    pageEventSink = eventSink;
                    if (pageEventSink == null) {
                        Log.i("empty", "Sink is empty");
                    }
                }

                @Override
                public void onCancel(Object o) {

                }
            });
        } catch (Error err) {
            Log.i("and error", "error is " + err.toString());
        }
    }

    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighLight>>() {}
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            // Do something on success
                        }
                    });
                }
            }
        }).start();
    }


    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("Reader", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("Reader", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    public void onFolioReaderClosed(int currentPage, int totalPage, String locator) {
        Log.i("readLocator", "-> saveReadLocator -> " + locator);
        Log.i("readLocator", "-> saveReadLocator -> " + currentPage + totalPage);
        final Map<String, Object> data = new HashMap<String, Object>();

        data.put("currentPage", currentPage);
        data.put("totalPage", totalPage);
        data.put("readLocator", locator);

        
        if (epubClosedSink != null) {
            epubClosedSink.success(data);
        }
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        read_locator = readLocator;
    }

    @Override
    public void onAddWordListener(String word) {
        Log.i("reader", "-> onAddWordListener -> " + word);

        if (addWordSink != null) {
            addWordSink.success(word);
        } else {
            Log.i("reader", "addWordSink -> Sink is Empty -> " + word);
        }
    }

    @Override
    public void translateAndCheckWordListener(String word) {
        Log.i("reader", "-> translateAndCheckWordListener ->" + word);

        if (translateAndCheckSink != null) {
            translateAndCheckSink.success(word);
        } else {
            Log.i("reader", "translateAndCheckSink -> Sink is Empty -> " + word);

        }
    }

    @Override
    public void textToSpeechListener(String word) {
        Log.v("reader", "-> textToSpeechListener ->" + word);
        
        if (textToSpeechSink != null) {
            textToSpeechSink.success(word);
        } else {
            Log.i("reader", "textToSpeechSink -> Sink is Empty -> " + word);

        }
    }

    @Override
    public void onDismissPopupListener() {
        Log.v("reader", "-> onDismissPopupListener");

        if (onDismissPopupSink != null) {
            onDismissPopupSink.success("dismiss");
        } else {
            Log.i("reader", "onDismissPopupSink -> Sink is Empty");

        }
    }

    public void sendTranslateAndCheckWord(String translate, boolean wordExist) {
        Log.i("send word", "-> sendTranslateAndCheckWord -> " + translate + wordExist);
        folioReader.sendTranslateAndCheckWord(translate, wordExist);
    }
}
