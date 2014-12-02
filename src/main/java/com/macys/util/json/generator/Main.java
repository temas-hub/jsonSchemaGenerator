package com.macys.util.json.generator;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static final String EXPECTED_JSON = "D:\\Download\\1\\act3.json";

    public static void main(String[] args) throws IOException{
        final SchemaGenerator schemaGenerator = SchemaGenerator.fromTxt(new FileReader(EXPECTED_JSON));
        System.out.println(schemaGenerator.generateString());
    }

}

