package org.nanohttpd.protocols.http.response;

import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;

public class Response {
    private final NanoHTTPD.Response delegate;

    public Response(NanoHTTPD.Response delegate) {
        this.delegate = delegate;
    }

    public static Response newChunkedResponse(IStatus status, String mimeType, InputStream data) {
        return new Response(NanoHTTPD.newChunkedResponse(toFiStatus(status), mimeType, data));
    }

    public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
        return new Response(NanoHTTPD.newFixedLengthResponse(toFiStatus(status), mimeType, txt));
    }

    public void addHeader(String name, String value) {
        delegate.addHeader(name, value);
    }

    public NanoHTTPD.Response unwrap() {
        return delegate;
    }

    static NanoHTTPD.Response.IStatus toFiStatus(IStatus status) {
        if (status == null) {
            return NanoHTTPD.Response.Status.INTERNAL_ERROR;
        }

        if (status instanceof Status) {
            try {
                return NanoHTTPD.Response.Status.valueOf(((Status) status).name());
            } catch (IllegalArgumentException ignored) {
                return NanoHTTPD.Response.Status.INTERNAL_ERROR;
            }
        }

        return new NanoHTTPD.Response.IStatus() {
            @Override
            public String getDescription() {
                return status.getDescription();
            }

            @Override
            public int getRequestStatus() {
                return status.getRequestStatus();
            }
        };
    }
}
