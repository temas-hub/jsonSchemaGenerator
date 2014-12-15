package com.macys.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.macys.util.json.validator.ErrorEvent;
import com.macys.util.json.validator.ErrorInterceptor;
import com.macys.util.json.validator.Validator;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 11/12/2014
 */
public class ValidatorTest {
    @Test
    public void simpleTest() throws URISyntaxException, IOException {
        final URL instanceResource = getClass().getClassLoader().getResource("testData/res1.json");
        final JsonParser jsonParser = new JsonParser();
        JsonElement instance = jsonParser.parse(new FileReader(new File(instanceResource.toURI())));

        final URL schemaResource = getClass().getClassLoader().getResource("testData/expectedMerge.json");
        JsonElement schema = jsonParser.parse(new FileReader(new File(schemaResource.toURI())));

        final List<String> errorPointers = validateAndGetErrors(instance, schema);
        Assert.assertEquals(0, errorPointers.size());
    }

    private List<String> validateAndGetErrors(final JsonElement instance, final JsonElement schema) {
        final List<String> errorPath = new ArrayList<String>();
        Validator validator = new Validator();
        validator.validate("", schema.getAsJsonObject(), instance, new ErrorInterceptor() {
            @Override
            public void error(final String errorElementPath, final JsonElement instancePointer, final ErrorEvent errorEvent) {
                System.out.println(errorEvent.getMessage());
                errorPath.add(errorElementPath);
            }
        });
        return errorPath;
    }

    @Test
    public void requiredElementsTest() throws URISyntaxException, IOException {
        final URL instanceResource = getClass().getClassLoader().getResource("testData/requiredElementDataWothError.json");
        final JsonParser jsonParser = new JsonParser();
        JsonElement instance = jsonParser.parse(new FileReader(new File(instanceResource.toURI())));

        final URL schemaResource = getClass().getClassLoader().getResource("testData/expectedMerge.json");
        JsonElement schema = jsonParser.parse(new FileReader(new File(schemaResource.toURI())));

        final List<String> errorPointers = validateAndGetErrors(instance, schema);
        Assert.assertEquals(2, errorPointers.size());
        Assert.assertEquals("/category/0/product/product/0/summary/producttype", errorPointers.get(0));
        Assert.assertEquals("/category/0/product/product/0/badges/promotionbadge/0", errorPointers.get(1));
    }


    @Test
    public void wrongTypeTest() throws URISyntaxException, IOException {
        final URL instanceResource = getClass().getClassLoader().getResource("testData/wrongTypes.json");
        final JsonParser jsonParser = new JsonParser();
        JsonElement instance = jsonParser.parse(new FileReader(new File(instanceResource.toURI())));

        final URL schemaResource = getClass().getClassLoader().getResource("testData/expectedMerge.json");
        JsonElement schema = jsonParser.parse(new FileReader(new File(schemaResource.toURI())));

        final List<String> errorPointers = validateAndGetErrors(instance, schema);
        Assert.assertEquals(1, errorPointers.size());
        Assert.assertEquals("/category/0/product/product/0/category/0/id", errorPointers.get(0));
    }
}
