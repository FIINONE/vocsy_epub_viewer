package org.nanohttpd.router;

import java.util.List;
import java.util.Map;

public class RouterNanoHTTPD extends fi.iki.elonen.router.RouterNanoHTTPD {

    public RouterNanoHTTPD(int port) {
        super(port);
    }

    public RouterNanoHTTPD(String hostname, int port) {
        super(hostname, port);
    }

    public static class UriResource {
        private final fi.iki.elonen.router.RouterNanoHTTPD.UriResource delegate;

        public UriResource(fi.iki.elonen.router.RouterNanoHTTPD.UriResource delegate) {
            this.delegate = delegate;
        }

        public <T> T initParameter(Class<T> clazz) {
            return delegate.initParameter(clazz);
        }
    }

    public interface UriResponder {
        org.nanohttpd.protocols.http.response.Response get(UriResource uriResource,
                                                           Map<String, String> urlParams,
                                                           org.nanohttpd.protocols.http.IHTTPSession session);

        org.nanohttpd.protocols.http.response.Response put(UriResource uriResource,
                                                           Map<String, String> urlParams,
                                                           org.nanohttpd.protocols.http.IHTTPSession session);

        org.nanohttpd.protocols.http.response.Response post(UriResource uriResource,
                                                            Map<String, String> urlParams,
                                                            org.nanohttpd.protocols.http.IHTTPSession session);

        org.nanohttpd.protocols.http.response.Response delete(UriResource uriResource,
                                                              Map<String, String> urlParams,
                                                              org.nanohttpd.protocols.http.IHTTPSession session);

        org.nanohttpd.protocols.http.response.Response other(String method,
                                                             UriResource uriResource,
                                                             Map<String, String> urlParams,
                                                             org.nanohttpd.protocols.http.IHTTPSession session);
    }

    public static abstract class DefaultHandler implements UriResponder, fi.iki.elonen.router.RouterNanoHTTPD.UriResponder {
        public abstract String getText();

        public abstract org.nanohttpd.protocols.http.response.IStatus getStatus();

        public String getMimeType() {
            return fi.iki.elonen.NanoHTTPD.MIME_HTML;
        }

        @Override
        public org.nanohttpd.protocols.http.response.Response get(UriResource uriResource,
                                                                  Map<String, String> urlParams,
                                                                  org.nanohttpd.protocols.http.IHTTPSession session) {
            return org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse(getStatus(), getMimeType(), getText());
        }

        @Override
        public org.nanohttpd.protocols.http.response.Response put(UriResource uriResource,
                                                                  Map<String, String> urlParams,
                                                                  org.nanohttpd.protocols.http.IHTTPSession session) {
            return methodNotAllowed();
        }

        @Override
        public org.nanohttpd.protocols.http.response.Response post(UriResource uriResource,
                                                                   Map<String, String> urlParams,
                                                                   org.nanohttpd.protocols.http.IHTTPSession session) {
            return methodNotAllowed();
        }

        @Override
        public org.nanohttpd.protocols.http.response.Response delete(UriResource uriResource,
                                                                     Map<String, String> urlParams,
                                                                     org.nanohttpd.protocols.http.IHTTPSession session) {
            return methodNotAllowed();
        }

        @Override
        public org.nanohttpd.protocols.http.response.Response other(String method,
                                                                    UriResource uriResource,
                                                                    Map<String, String> urlParams,
                                                                    org.nanohttpd.protocols.http.IHTTPSession session) {
            return methodNotAllowed();
        }

        @Override
        public fi.iki.elonen.NanoHTTPD.Response get(fi.iki.elonen.router.RouterNanoHTTPD.UriResource uriResource,
                                                     Map<String, String> urlParams,
                                                     fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
            return toFiResponse(get(new UriResource(uriResource), urlParams, new SessionAdapter(session)));
        }

        @Override
        public fi.iki.elonen.NanoHTTPD.Response put(fi.iki.elonen.router.RouterNanoHTTPD.UriResource uriResource,
                                                     Map<String, String> urlParams,
                                                     fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
            return toFiResponse(put(new UriResource(uriResource), urlParams, new SessionAdapter(session)));
        }

        @Override
        public fi.iki.elonen.NanoHTTPD.Response post(fi.iki.elonen.router.RouterNanoHTTPD.UriResource uriResource,
                                                      Map<String, String> urlParams,
                                                      fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
            return toFiResponse(post(new UriResource(uriResource), urlParams, new SessionAdapter(session)));
        }

        @Override
        public fi.iki.elonen.NanoHTTPD.Response delete(fi.iki.elonen.router.RouterNanoHTTPD.UriResource uriResource,
                                                        Map<String, String> urlParams,
                                                        fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
            return toFiResponse(delete(new UriResource(uriResource), urlParams, new SessionAdapter(session)));
        }

        @Override
        public fi.iki.elonen.NanoHTTPD.Response other(String method,
                                                       fi.iki.elonen.router.RouterNanoHTTPD.UriResource uriResource,
                                                       Map<String, String> urlParams,
                                                       fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
            return toFiResponse(other(method, new UriResource(uriResource), urlParams, new SessionAdapter(session)));
        }

        private org.nanohttpd.protocols.http.response.Response methodNotAllowed() {
            return org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse(
                    org.nanohttpd.protocols.http.response.Status.INTERNAL_ERROR,
                    fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT,
                    "Method not supported"
            );
        }
    }

    private static fi.iki.elonen.NanoHTTPD.Response toFiResponse(org.nanohttpd.protocols.http.response.Response response) {
        if (response != null) {
            return response.unwrap();
        }
        return fi.iki.elonen.NanoHTTPD.newFixedLengthResponse(
                fi.iki.elonen.NanoHTTPD.Response.Status.INTERNAL_ERROR,
                fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT,
                "Empty response"
        );
    }

    private static final class SessionAdapter implements org.nanohttpd.protocols.http.IHTTPSession {
        private final fi.iki.elonen.NanoHTTPD.IHTTPSession delegate;

        private SessionAdapter(fi.iki.elonen.NanoHTTPD.IHTTPSession delegate) {
            this.delegate = delegate;
        }

        @Override
        public Map<String, String> getHeaders() {
            return delegate.getHeaders();
        }

        @Override
        public org.nanohttpd.protocols.http.request.Method getMethod() {
            return org.nanohttpd.protocols.http.request.Method.fromFi(delegate.getMethod());
        }

        @Override
        public Map<String, List<String>> getParameters() {
            return delegate.getParameters();
        }

        @Override
        public String getUri() {
            return delegate.getUri();
        }
    }
}
