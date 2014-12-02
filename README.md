Json Schema Generator
===================

Generates json schema from list of json data files

1. Current generated attributes are: "id" - name, "type" - (object, boolean, string, number, array) are supported, "required" - boolean, "items" - (only for arrays) - merged items schema object, "properties" - (only for objects) list attributes 
2. Parent anonymous object is required for all data files. It will be generated to "id" : "#", "type": "object" 
3. All items of each array must contain elements with the same type (object, boolean, string, number, array). If it is not true - the IllegalArgumentException would be thrown

####Usage
https://github.com/temas-hub/jsonSchemaGenerator/blob/master/src/test/java/com/macys/util/json/generator/MergeTest.java 

