import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

part 'model/enum/epub_scroll_direction.dart';
part 'model/epub_locator.dart';
part 'utils/util.dart';

class VocsyEpub {
  static const MethodChannel _channel =
      const MethodChannel('vocsy_epub_viewer');
  static const EventChannel _epubClosedChannel =
      const EventChannel('epub_closed');
  static const EventChannel _pageChannel = const EventChannel('page');
  static const EventChannel _addWordChannel = const EventChannel('add_word');
  static const EventChannel _transAndCheckWordChannel =
      const EventChannel('translate_and_check_word');
  static const EventChannel _textToSpeechChannel =
      const EventChannel('text_to_speech');
  static const EventChannel _onDismissPopupChannel =
      const EventChannel('on_dismiss_popup');

  /// Configure Viewer's with available values
  ///
  /// themeColor is the color of the reader
  /// scrollDirection uses the [EpubScrollDirection] enum
  /// allowSharing
  /// enableTts is an option to enable the inbuilt Text-to-Speech
  static void setConfig(
      {Color themeColor = Colors.blue,
      String identifier = 'book',
      bool nightMode = false,
      EpubScrollDirection scrollDirection = EpubScrollDirection.ALLDIRECTIONS,
      bool allowSharing = false,
      bool enableTts = false}) async {
    Map<String, dynamic> agrs = {
      "identifier": identifier,
      "themeColor": Util.getHexFromColor(themeColor),
      "scrollDirection": Util.getDirection(scrollDirection),
      "allowSharing": allowSharing,
      'enableTts': enableTts,
      'nightMode': nightMode
    };
    await _channel.invokeMethod('setConfig', agrs);
  }

  /// bookPath should be a local file.
  /// Last location is only available for android.
  static void open(String bookPath, {EpubLocator? lastLocation}) async {
    Map<String, dynamic> agrs = {
      "bookPath": bookPath,
      'lastLocation':
          lastLocation == null ? '' : jsonEncode(lastLocation.toJson()),
    };
    _channel.invokeMethod('setChannel');
    await _channel.invokeMethod('open', agrs);
  }

  static void closeReader() async {
    _channel.invokeMethod('setChannel');
    await _channel.invokeMethod('close');
  }

  /// bookPath should be an asset file path.
  /// Last location is only available for android.
  static Future openAsset(String bookPath, {EpubLocator? lastLocation}) async {
    if (extension(bookPath) == '.epub') {
      Map<String, dynamic> agrs = {
        "bookPath": (await Util.getFileFromAsset(bookPath)).path,
        'lastLocation':
            lastLocation == null ? '' : jsonEncode(lastLocation.toJson()),
      };
      _channel.invokeMethod('setChannel');
      await _channel.invokeMethod('open', agrs);
    } else {
      throw ('${extension(bookPath)} cannot be opened, use an EPUB File');
    }
  }

  static Future setChannel() async {
    await _channel.invokeMethod('setChannel');
  }

  ///Epub closed event
  static Stream get epubClosed {
    Stream epubClosedStream =
        _epubClosedChannel.receiveBroadcastStream().map((value) => value);

    return epubClosedStream;
  }

  /// Stream to get EpubLocator for android and pageNumber for iOS
  static Stream get locatorStream {
    print("In stream");
    Stream pageStream =
        _pageChannel.receiveBroadcastStream().map((value) => value);

    return pageStream;
  }

  static Stream<String> get addWordStream {
    Stream<String> addWordStream = _addWordChannel.receiveBroadcastStream().map(
          (event) => event.toString(),
        );
    return addWordStream;
  }

  static Stream<String> get transAndCheckWordStream {
    Stream<String> addWordStream =
        _transAndCheckWordChannel.receiveBroadcastStream().map(
              (event) => event.toString(),
            );
    return addWordStream;
  }

  static Stream<String> get textToSpeechStream {
    Stream<String> textStream = _textToSpeechChannel
        .receiveBroadcastStream()
        .map((event) => event.toString());

    return textStream;
  }

  static Stream<String> get onDismissPopupStream {
    Stream<String> onDismiss = _onDismissPopupChannel
        .receiveBroadcastStream()
        .map((event) => event.toString());

    return onDismiss;
  }

  static Future<void> sendTransAndCheckWord(
      String translate, bool wordExist) async {
    Map<String, dynamic> args = {
      "translate": translate,
      "wordExist": wordExist,
    };

    await _channel.invokeMethod('send_word', args);
  }
}
