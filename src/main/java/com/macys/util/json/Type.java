package com.macys.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Type {
    public static final String SCHEMA_VERSION =  "http://json-schema.org/draft-03/schema";
    public static final String OBJECT = "object";
    public static final String ARRAY = "array";
    public static final String BOOLEAN = "boolean";
    public static final String NUMBER = "number";
    public static final String STRING = "string";
    public static final String NULL = "null";

    public static String getSchemaTypeFor(JsonElement t) {
        if (t.isJsonObject()) {
            return OBJECT;
        }
        if (t.isJsonArray()) {
            return ARRAY;
        }
        if (t.isJsonPrimitive()) {
            final JsonPrimitive asJsonPrimitive = t.getAsJsonPrimitive();
            if (asJsonPrimitive.isBoolean()) {
                return BOOLEAN;
            }
            if (asJsonPrimitive.isNumber()) {
                return NUMBER;
            }
            return STRING;
        }
        return NULL;
    }
}

