package org.nanohttpd.protocols.http.request;

public enum Method {
    GET,
    PUT,
    POST,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PATCH;

    public static Method fromFi(fi.iki.elonen.NanoHTTPD.Method method) {
        if (method == null) {
            return null;
        }
        return Method.valueOf(method.name());
    }

    public fi.iki.elonen.NanoHTTPD.Method toFi() {
        return fi.iki.elonen.NanoHTTPD.Method.valueOf(name());
    }
}
