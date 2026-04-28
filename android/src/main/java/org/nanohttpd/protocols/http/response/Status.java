package org.nanohttpd.protocols.http.response;

public enum Status implements IStatus {
    OK(200, "OK"),
    PARTIAL_CONTENT(206, "Partial Content"),
    RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    NOT_MODIFIED(304, "Not Modified"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int requestStatus;
    private final String description;

    Status(int requestStatus, String description) {
        this.requestStatus = requestStatus;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return requestStatus + " " + description;
    }

    @Override
    public int getRequestStatus() {
        return requestStatus;
    }
}
