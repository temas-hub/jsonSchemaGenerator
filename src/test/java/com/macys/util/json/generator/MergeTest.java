package com.macys.util.json.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.macys.mobile.gateway.sdpstub.fitnesse.json.ComparisonResult;
import com.macys.mobile.gateway.sdpstub.fitnesse.json.JsonComparator;
import com.macys.mobile.gateway.sdpstub.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 27/11/2014
 */
public class MergeTest {
    @Test
    public void simpleTest1() throws URISyntaxException, IOException {
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        List<JsonObject> schemas = new LinkedList<JsonObject>();
        final URL resource1 = getClass().getClassLoader().getResource("testData/res1.json");
        SchemaGenerator generator = SchemaGenerator.fromTxt(new FileReader(new File(resource1.toURI())));
        final JsonObject schema1 = generator.getSchemaElement();
        schemas.add(schema1);

        final URL resource2 = getClass().getClassLoader().getResource("testData/res2.json");
        generator = SchemaGenerator.fromTxt(new FileReader(new File(resource2.toURI())));
        final JsonObject schema2 = generator.getSchemaElement();
        schemas.add(schema2);

        SchemaMerger merger = new SchemaMerger();
        final JsonElement result = merger.mergeSchemas(schemas);

        final String mergeResultActual = gs.toJson(result);


        final URL expectedResource = getClass().getClassLoader().getResource("testData/expectedMerge.json");
        String expectedResponse = Utils.readFile(new File(expectedResource.toURI()));
        ComparisonResult checkResult = JsonComparator.compare(expectedResponse, mergeResultActual);
        checkResult.logFailed();
        Assert.assertTrue(checkResult.equal());
    }
}
