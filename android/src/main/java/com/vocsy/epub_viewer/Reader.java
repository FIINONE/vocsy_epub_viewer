package com.vocsy.epub_viewer;

import android.content.Context;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class Reader implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener,
            FolioReader.OnAddWordListener, FolioReader.TranslateAndCheckWordListener,
            FolioReader.TextToSpeechListener, FolioReader.OnDismissPopupListener,
            FolioReader.LikeHandler {

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
    // The plugin's method channel; used to ask the Dart like handler for the new state.
    private MethodChannel methodChannel;

    Reader(Context context, BinaryMessenger messenger, ReaderConfig config,
        EventChannel.EventSink sink, EventChannel.EventSink closeSink,
        EventChannel.EventSink addSink, EventChannel.EventSink sendWordSink,
        EventChannel.EventSink textSpeechSing, EventChannel.EventSink dismissSink,
        MethodChannel channel) {
        this.context = context;
        readerConfig = config;

        //setPageHandler(messenger);

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this)
                .setOnAddWordListener(this)
                .setTranslateAndCheckListener(this)
                .setTextToSpeechListener(this)
                .setOnDismissPopupListener(this)
                .setLikeHandler(this);

        pageEventSink = sink;
        epubClosedSink = closeSink;
        addWordSink = addSink;
        translateAndCheckSink = sendWordSink;
        textToSpeechSink = textSpeechSing;
        onDismissPopupSink = dismissSink;
        methodChannel = channel;
    }

    public void open(String bookPath, String lastLocation, boolean liked) {
        final String path = bookPath;
        final String location = lastLocation;
        // Set synchronously so the toolbar already shows the right icon when the reader starts.
        folioReader.setLiked(liked);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("SavedLocation", "-> savedLocation -> " + location);
                    // FolioReader is a singleton: always overwrite the opening position, otherwise a
                    // book opened without lastLocation starts at the previous book's position.
                    ReadLocator readLocator = null;
                    if (location != null && !location.isEmpty()) {
                        readLocator = ReadLocator.Companion.fromJson(location);
                    }
                    folioReader.setReadLocator(readLocator);
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

    // Calls the Dart handler set with VocsyEpub.setLikeHandler(); its return value is the new state.
    // Any failure (no handler, exception in it, unexpected value) keeps the current state.
    @Override
    public void onLikeTapped(final boolean currentlyLiked, final FolioReader.LikeResultCallback callback) {
        Log.v("reader", "-> onLikeTapped -> currentlyLiked = " + currentlyLiked);

        methodChannel.invokeMethod("onLike", currentlyLiked, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                callback.onResult(result instanceof Boolean ? (Boolean) result : currentlyLiked);
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Log.e("reader", "-> onLikeTapped -> error: " + errorCode + " " + errorMessage);
                callback.onResult(currentlyLiked);
            }

            @Override
            public void notImplemented() {
                Log.i("reader", "-> onLikeTapped -> no like handler on the Dart side");
                callback.onResult(currentlyLiked);
            }
        });
    }

    public void sendTranslateAndCheckWord(String translate, boolean wordExist) {
        Log.i("send word", "-> sendTranslateAndCheckWord -> " + translate + wordExist);
        folioReader.sendTranslateAndCheckWord(translate, wordExist);
    }
}
