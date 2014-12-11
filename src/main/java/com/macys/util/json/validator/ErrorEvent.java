package com.macys.util.json.validator;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 11/12/2014
 */
public class ErrorEvent {
    private final ErrorType type;
    private final String message;

    public ErrorEvent(final ErrorType type, final String message) {
        this.type = type;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
