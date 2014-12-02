package com.macys.util.json.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 19/11/2014
 */
public class SchemaMerger {
    public JsonElement mergeSchemas(List<JsonObject> inputSchemaObjects) {
        Map<String, JsonElement> allProperties = new HashMap<String, JsonElement>();
        boolean firstElement = true;
        String name = null;
        String type = null;
        boolean isRequired = true;
        List<JsonObject> childInputSchemaObjects = new ArrayList<JsonObject>();
        for (JsonObject element : inputSchemaObjects) {
            if (element == null) {
                isRequired = false;
                childInputSchemaObjects.add(null); // for non required arrays only, for object child objects are populated below
                continue;
            }
            if (firstElement) {
                name = element.get(SchemaGenerator.ID).getAsString();
                type = element.get(SchemaGenerator.TYPE).getAsString();
                isRequired &= element.get(SchemaGenerator.REQUIRED).getAsBoolean();
            } else {
                if (!name.equals(element.get(SchemaGenerator.ID).getAsString())) {
                    throw new IllegalStateException("Must be equal because of propertiesInputSchemaObj.get(reqPropEntry.getKey())");
                }
                // for all prop check that type is equal
                final String foundType = element.get(SchemaGenerator.TYPE).getAsString();
                if (!type.equals(foundType)) {
                    throw new IllegalArgumentException("Types of object with name " + name + " are different. Expected:" + type + " but found:" + foundType );
                }
            }
            if (element.get(SchemaGenerator.TYPE).getAsString().equals(Type.ARRAY)) {
                final JsonObject arrayElementObject = element.get(SchemaGenerator.ITEMS).getAsJsonObject();
                childInputSchemaObjects.add(arrayElementObject);
            } else {
                final JsonElement propertiesElement = element.get(SchemaGenerator.PROPERTIES);
                if (propertiesElement != null) {
                    final JsonObject properties = propertiesElement.getAsJsonObject();
                    union(allProperties, properties);
                }
            }
            firstElement = false;
        }
        JsonObject resultSchemaObj = new JsonObject();
        resultSchemaObj.addProperty(SchemaGenerator.ID, name);
        resultSchemaObj.addProperty(SchemaGenerator.TYPE, type);
        resultSchemaObj.addProperty(SchemaGenerator.REQUIRED, Boolean.valueOf(isRequired));

        if (type.equals(Type.ARRAY)) {
            // array type schema type always has a single item (object) -> recursion
            resultSchemaObj.add(SchemaGenerator.ITEMS, mergeSchemas(childInputSchemaObjects));
        } else {
            if (allProperties.isEmpty()) {
                return resultSchemaObj;
            }
            final JsonObject resPropObject = new JsonObject();
            resultSchemaObj.add(SchemaGenerator.PROPERTIES, resPropObject);
            // for all requiredProperties
            for (Map.Entry<String, JsonElement> reqPropEntry : allProperties.entrySet()) {
                childInputSchemaObjects = new ArrayList<JsonObject>(); //clean
                // collect all child input objects for required property
                for (JsonObject schemaObject : inputSchemaObjects) {
                    if (schemaObject == null) {
                        childInputSchemaObjects.add(null); // means that the attribute is not required
                    } else {
                        final JsonObject propertiesInputSchemaObj = schemaObject.get(SchemaGenerator.PROPERTIES).getAsJsonObject();
                        final JsonElement inputProperty = propertiesInputSchemaObj.get(reqPropEntry.getKey());
                        childInputSchemaObjects.add(inputProperty == null ? null : inputProperty.getAsJsonObject());
                    }
                }
                // for objects and primitives -> recursion
                resPropObject.add(reqPropEntry.getKey(), mergeSchemas(childInputSchemaObjects));
            }
        }
        return resultSchemaObj;
    }

    private void union(final Map<String, JsonElement> allProperties, final JsonObject properties) {
        for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
            allProperties.put(property.getKey(), property.getValue());
        }
    }
}
