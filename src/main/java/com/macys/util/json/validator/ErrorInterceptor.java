package com.macys.util.json.validator;

import com.google.gson.JsonElement;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 11/12/2014
 */
public interface ErrorInterceptor {
    public void error(final String errorElementPath, final JsonElement instancePointer, final ErrorEvent errorEvent);
}
