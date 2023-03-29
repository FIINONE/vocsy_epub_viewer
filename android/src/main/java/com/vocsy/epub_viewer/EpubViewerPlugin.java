package com.vocsy.epub_viewer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Map;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.FlutterPlugin;

import androidx.annotation.NonNull;

import com.folioreader.model.locators.ReadLocator;

/**
 * EpubReaderPlugin
 */
public class EpubViewerPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

    private Reader reader;
    private ReaderConfig config;
    private MethodChannel channel;
    static private Activity activity;
    static private Context context;
    static BinaryMessenger messenger;
    static private EventChannel eventChannel;
    static private EventChannel.EventSink sink;
    private static final String channelName = "vocsy_epub_viewer";

    static private EventChannel epubClosedChannel;
    static private EventChannel.EventSink epubClosedSink;
    private static final String epubClosedChannelName = "epub_closed";

    static private EventChannel addWordChannel;
    static private EventChannel.EventSink addWordSink;
    private static final String addWordChannelName = "add_word";

    static private EventChannel transAndCheckChannel;
    static private EventChannel.EventSink transAndCheckSink;
    private static final String transAndCheckChannelName = "translate_and_check_word";

    static private EventChannel textToSpeechChannel;
    static private EventChannel.EventSink textToSpeechSink;
    private static final String textToSpeechChannelName = "text_to_speech";

    static private EventChannel onDismissPopupChannel;
    static private EventChannel.EventSink onDismissPopupSink;
    private static final String onDismissPopupChannelName = "on_dismiss_popup";

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        context = registrar.context();
        activity = registrar.activity();
        messenger = registrar.messenger();
        new EventChannel(messenger, "page").setStreamHandler(new EventChannel.StreamHandler() {

            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                sink = eventSink;
                if (sink == null) {
                    Log.i("empty", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });

        setEpubClosedEvent();
        setAddWordEvent();
        setTranslateAndCheckEvent();
        setTextToSpeechEvent();
        setOnDismissPopupEvent();

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "vocsy_epub_viewer");
        channel.setMethodCallHandler(new EpubViewerPlugin());
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        messenger = binding.getBinaryMessenger();
        context = binding.getApplicationContext();
        new EventChannel(messenger, "page").setStreamHandler(new EventChannel.StreamHandler() {

            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                sink = eventSink;
                if (sink == null) {
                    Log.i("empty", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });


        setEpubClosedEvent();
        setAddWordEvent();
        setTranslateAndCheckEvent();
        setTextToSpeechEvent();
        setOnDismissPopupEvent();

        channel = new MethodChannel(binding.getFlutterEngine().getDartExecutor(), channelName);
        channel.setMethodCallHandler(this);
    }

    private static void setEpubClosedEvent () {
        epubClosedChannel = new EventChannel(messenger, epubClosedChannelName);
        epubClosedChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                epubClosedSink = eventSink;
                if (epubClosedSink == null) {
                    Log.i("epubClosedSink", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private static void setAddWordEvent () {
        addWordChannel = new EventChannel(messenger, addWordChannelName);
        addWordChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                addWordSink = eventSink;
                if (addWordSink == null) {
                    Log.i("addWordSink", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private static void setTranslateAndCheckEvent () {
        transAndCheckChannel = new EventChannel(messenger, transAndCheckChannelName);
        transAndCheckChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                transAndCheckSink = eventSink;
                if (transAndCheckSink == null) {
                    Log.i("transAndCheckSink", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private static void setTextToSpeechEvent () {
        textToSpeechChannel = new EventChannel(messenger, textToSpeechChannelName);
        textToSpeechChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                textToSpeechSink = eventSink;
                if (textToSpeechSink == null) {
                    Log.i("textToSpeechSink", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private static void setOnDismissPopupEvent () {
        onDismissPopupChannel = new EventChannel(messenger, onDismissPopupChannelName);
        onDismissPopupChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {

                onDismissPopupSink = eventSink;
                if (onDismissPopupSink == null) {
                    Log.i("onDismissPopupSink", "Sink is empty");
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        // TODO: your plugin is no longer attached to a Flutter experience.
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        if (call.method.equals("setConfig")) {
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            String identifier = arguments.get("identifier").toString();
            String themeColor = arguments.get("themeColor").toString();
            String scrollDirection = arguments.get("scrollDirection").toString();
            Boolean nightMode = Boolean.parseBoolean(arguments.get("nightMode").toString());
            Boolean allowSharing = Boolean.parseBoolean(arguments.get("allowSharing").toString());
            Boolean enableTts = Boolean.parseBoolean(arguments.get("enableTts").toString());
            config = new ReaderConfig(context, identifier, themeColor,
                    scrollDirection, allowSharing, enableTts, nightMode);

        } else if (call.method.equals("open")) {

            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            String bookPath = arguments.get("bookPath").toString();
            String lastLocation = arguments.get("lastLocation").toString();

            Log.i("opening", "In open function");

            if (sink == null) {
                Log.i("sink status", "sink is empty");
            }
            if (epubClosedSink == null) {
                Log.i("epubClosedSink status", "sink is empty");
            }
            if (addWordSink == null) {
                Log.i("addWordSink status", "sink is empty");
            }
            if (transAndCheckSink == null) {
                Log.i("transAndCheckSink status", "sink is empty");
            }
            if (textToSpeechSink == null) {
                Log.i("textToSpeechSink status", "sink is empty");
            }
            if (onDismissPopupSink == null) {
                Log.i("onDismissPopupSink status", "sink is empty");
            }
            reader = new Reader(context, messenger, config, sink, epubClosedSink, addWordSink, transAndCheckSink, textToSpeechSink, onDismissPopupSink);
            reader.open(bookPath, lastLocation);

        } else if (call.method.equals("close")) {
            reader.close();
        } else if (call.method.equals("setChannel")) {
            eventChannel = new EventChannel(messenger, "page");
            eventChannel.setStreamHandler(new EventChannel.StreamHandler() {

                @Override
                public void onListen(Object o, EventChannel.EventSink eventSink) {

                    sink = eventSink;
                }

                @Override
                public void onCancel(Object o) {

                }
            });
        } else if (call.method.equals("send_word")) {
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            String translate = arguments.get("translate").toString();
            Boolean wordExist = Boolean.parseBoolean(arguments.get("wordExist").toString());

            reader.sendTranslateAndCheckWord(translate, wordExist);
        } else {
            result.notImplemented();
        }
    }
}
