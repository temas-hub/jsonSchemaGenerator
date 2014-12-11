package com.macys.util.json.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.macys.util.json.Type;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SchemaGenerator {

    public static final String SCHEMA = "$schema";
    public static final String ID = "id";
    public static final String REQUIRED = "required";
    public static final String TYPE = "type";
    public static final String PROPERTIES = "properties";
    public static final String ITEMS = "items";

    private JsonElement rootElement;

    public SchemaGenerator(final JsonElement rootElement) {
        this.rootElement = rootElement;
    }

    public static SchemaGenerator fromTxt(Reader jsonReader) {
        final JsonParser jsonParser = new JsonParser();
        JsonElement jelement = jsonParser.parse(jsonReader);
        return new SchemaGenerator(jelement);
    }

    private JsonObject makeSchemaElement(JsonElement jsonElement, String elementName, boolean isFirstLevel){

        JsonObject schema_dict = new JsonObject();

        if (isFirstLevel) {
            schema_dict.addProperty(SCHEMA, Type.SCHEMA_VERSION);
            schema_dict.addProperty(ID, "#");
        }

        if (elementName != null) {
            schema_dict.addProperty(ID, elementName);
        }

        String schema_type = Type.getSchemaTypeFor(jsonElement);

        schema_dict.addProperty(REQUIRED, Boolean.TRUE);
        schema_dict.addProperty(TYPE, schema_type);

        if (jsonElement.isJsonObject() && !jsonElement.getAsJsonObject().entrySet().isEmpty()) {
            final JsonObject propObject = new JsonObject();
            schema_dict.add(PROPERTIES, propObject);

            for (Map.Entry<String, JsonElement> propElemement : jsonElement.getAsJsonObject().entrySet()) {
                propObject.add(propElemement.getKey(), makeSchemaElement(propElemement.getValue(), propElemement.getKey(), false));
            }

        }
        else if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
            // TODO now support arrays with the same type only
            final JsonArray asJsonArray = jsonElement.getAsJsonArray();
            SchemaMerger merger = new SchemaMerger();
            JsonElement mergedItemsSchema = null;
            if (asJsonArray.size() > 1) {
                final List<JsonObject> schemas = new ArrayList<JsonObject>();
                for (int i = 0; i < asJsonArray.size(); i++) {
                    schemas.add(makeSchemaElement(asJsonArray.get(i), "0", false));
                }
                mergedItemsSchema = merger.mergeSchemas(schemas);
            } else {
                mergedItemsSchema = makeSchemaElement(asJsonArray.get(0), "0", false);
            }

            schema_dict.add(ITEMS, mergedItemsSchema);
        }
        return schema_dict;
    }


    public JsonElement getOrigElement() {
        return rootElement;
    }

    public JsonObject getSchemaElement() {
        return this.makeSchemaElement(rootElement, null, true);
    }


    public String generateString() {
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        return gs.toJson(getSchemaElement());
    }
}

