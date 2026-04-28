package org.nanohttpd.protocols.http;

import org.nanohttpd.protocols.http.request.Method;

import java.util.List;
import java.util.Map;

public interface IHTTPSession {
    Map<String, String> getHeaders();

    Method getMethod();

    Map<String, List<String>> getParameters();

    String getUri();
}
