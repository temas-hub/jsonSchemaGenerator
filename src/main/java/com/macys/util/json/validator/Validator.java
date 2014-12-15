package com.macys.util.json.validator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.macys.util.json.Type;
import com.macys.util.json.generator.SchemaGenerator;

import java.util.Map;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 11/12/2014
 */
public class Validator {
    public void validate(final String path, final JsonObject schema, final JsonElement instance, final ErrorInterceptor errorInterceptor) {

        String type = schema.get(SchemaGenerator.TYPE).getAsString();
        final boolean isRequired = schema.get(SchemaGenerator.REQUIRED).getAsBoolean();
        if (instance == null) {
            if (isRequired) {
                errorInterceptor.error(path, instance,
                        new ErrorEvent(ErrorType.PROPERTY_REQUIRED,
                                String.format("'%s' element is required but not found", path)));
            }
            return;
        }
        final String instanceType = Type.getSchemaTypeFor(instance);
        if (!type.equals(instanceType)) {
            errorInterceptor.error(path, instance,
                    new ErrorEvent(ErrorType.TYPE_DOESNOT_MATCH,
                            String.format("'%s' element has wrong type. Schema has type '%s' but '%s' was found", path, type, instanceType)));
        }

        if (type.equals(Type.OBJECT)) {
            final JsonObject instanceObj = instance.getAsJsonObject();
            final JsonElement propertiesElement = schema.get(SchemaGenerator.PROPERTIES);
            if (propertiesElement != null) {
                final JsonObject properties = propertiesElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
                    final String childKey = property.getKey();
                    final JsonObject childSchema = property.getValue().getAsJsonObject();
                    final JsonElement instChild = instanceObj.get(childKey);
                    this.validate(path + "/" + childKey, childSchema, instChild, errorInterceptor);
                }
            }
        } else if (type.equals(Type.ARRAY)) {
            final JsonArray instanceArray = instance.getAsJsonArray();
            final JsonObject arrayElementObject = schema.get(SchemaGenerator.ITEMS).getAsJsonObject();
            if (arrayElementObject.get(SchemaGenerator.REQUIRED).getAsBoolean() && instanceArray.size() == 0) {
                final String elementPath = path + "/0";
                errorInterceptor.error(elementPath, instance,
                        new ErrorEvent(ErrorType.PROPERTY_REQUIRED,
                                String.format("'%s' element is required but not found", elementPath)));
            }
            for (int i = 0; i < instanceArray.size(); i++) {
                this.validate(path + "/" + i, arrayElementObject, instanceArray.get(i), errorInterceptor);
            }
        }
    }
}
