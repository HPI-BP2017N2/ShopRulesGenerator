package de.hpi.shoprulesgenerator.service;

import com.eclipsesource.json.*;
import de.hpi.shoprulesgenerator.exception.CouldNotDetermineJsonPathException;

class JsonPathBuilder {

    private JsonPathBuilder() {}

    static String createJsonPath(Script snippet, String attribute) throws CouldNotDetermineJsonPathException {
        try {
            return "$" + createJsonPath(Json.parse(snippet.getContent()), attribute.toLowerCase());
        } catch (ParseException e) { throw new CouldNotDetermineJsonPathException("Could not parse Json!"); }
    }

    @SuppressWarnings("unchecked")
    private static String createJsonPath(JsonValue jsonValue, String attribute) throws
            CouldNotDetermineJsonPathException {
        if (jsonValue.isArray()) {
            return createJsonPath(jsonValue.asArray(), attribute);
        } else if (jsonValue.isObject()) {
            return createJsonPath(jsonValue.asObject(), attribute);
        } else if (jsonValue.toString().toLowerCase().contains(attribute)) {
            return "";
        }
        throw new CouldNotDetermineJsonPathException("Could not generate JsonPath!");
    }

    private static String createJsonPath(JsonArray array, String attribute) throws CouldNotDetermineJsonPathException {
        for (int iValue = 0; iValue < array.size(); iValue++)
            try {
                return "[" + iValue + "]" + createJsonPath(array.get(iValue), attribute);
            } catch (CouldNotDetermineJsonPathException ignored) { /* Ignore this, maybe attribute is in next entry */ }
        throw new CouldNotDetermineJsonPathException("Could not find attribute " + attribute + " within array!" + array);
    }

    private static String createJsonPath(JsonObject object, String attribute) throws CouldNotDetermineJsonPathException {
        for (String name : object.names()) {
            try {
                return "['" + name + "']" + createJsonPath(object.get(name), attribute);
            } catch (CouldNotDetermineJsonPathException ignored) { /* Ignore this, maybe attribute is in next name */ }
        }
        throw new CouldNotDetermineJsonPathException("Could not find attribute " + attribute + " within object!" + object);
    }
}
