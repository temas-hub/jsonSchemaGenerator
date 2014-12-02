package com.macys.util.json.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 01/12/2014
 */
public class GenerateTest {
    @Test
    public void simpleTest1() throws FileNotFoundException, URISyntaxException {
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        List<JsonObject> schemas = new LinkedList<JsonObject>();
        final URL resource1 = getClass().getClassLoader().getResource("testData/res1.json");
        SchemaGenerator generator = SchemaGenerator.fromTxt(new FileReader(new File(resource1.toURI())));
        final JsonObject schema1 = generator.getSchemaElement();
        schemas.add(schema1);
        System.out.println(gs.toJson(schema1));
        System.out.println("==================================");
    }
}
