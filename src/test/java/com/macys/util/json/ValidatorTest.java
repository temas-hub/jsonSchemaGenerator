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
import java.util.concurrent.atomic.AtomicInteger;

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

        final AtomicInteger errorCounter = new AtomicInteger(0);
        Validator validator = new Validator();
        validator.validate("", schema.getAsJsonObject(), instance, new ErrorInterceptor() {
            @Override
            public void error(final JsonElement schemaPointer, final JsonElement instancePointer, final ErrorEvent errorEvent) {
                System.out.println(errorEvent.getMessage());
                errorCounter.incrementAndGet();
            }
        });
        Assert.assertEquals(0, errorCounter.get());

    }
    @Test
    public void wrongTest() throws URISyntaxException, IOException {
        final URL instanceResource = getClass().getClassLoader().getResource("testData/wrongData.json");
        final JsonParser jsonParser = new JsonParser();
        JsonElement instance = jsonParser.parse(new FileReader(new File(instanceResource.toURI())));

        final URL schemaResource = getClass().getClassLoader().getResource("testData/expectedMerge.json");
        JsonElement schema = jsonParser.parse(new FileReader(new File(schemaResource.toURI())));

        final AtomicInteger errorCounter = new AtomicInteger(0);
        Validator validator = new Validator();
        validator.validate("", schema.getAsJsonObject(), instance, new ErrorInterceptor() {
            @Override
            public void error(final JsonElement schemaPointer, final JsonElement instancePointer, final ErrorEvent errorEvent) {
                System.out.println(errorEvent.getMessage());
                errorCounter.incrementAndGet();
            }
        });
        Assert.assertEquals(2, errorCounter.get());

    }
}
