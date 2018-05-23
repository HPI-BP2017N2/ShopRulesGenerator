package de.hpi.shoprulesgenerator.service;

import com.eclipsesource.json.*;
import de.hpi.shoprulesgenerator.exception.CouldNotDetermineJsonPathException;

class JsonPathBuilder {

    private JsonPathBuilder() {}

    static String getJsonPath(Script snippet, String attribute) throws CouldNotDetermineJsonPathException {
        try {
            return "$" + getJsonPath(Json.parse(snippet.getContent()), attribute.toLowerCase());
        } catch (ParseException e) { throw new CouldNotDetermineJsonPathException("Could not parse Json!"); }
    }

    @SuppressWarnings("unchecked")
    private static String getJsonPath(JsonValue jsonValue, String attribute) throws
            CouldNotDetermineJsonPathException {
        if (jsonValue.isArray()) {
            return getJsonPath(jsonValue.asArray(), attribute);
        } else if (jsonValue.isObject()) {
            return getJsonPath(jsonValue.asObject(), attribute);
        } else if (jsonValue.isString() && jsonValue.asString().toLowerCase().contains(attribute)) {
            return "";
        }
        throw new CouldNotDetermineJsonPathException("Could not generate JsonPath!");
    }

    private static String getJsonPath(JsonArray array, String attribute) throws CouldNotDetermineJsonPathException {
        for (int iValue = 0; iValue < array.size(); iValue++)
            try {
                return "[" + iValue + "]" + getJsonPath(array.get(iValue), attribute);
            } catch (CouldNotDetermineJsonPathException ignored) { /* Ignore this, maybe attribute is in next entry */ }
        throw new CouldNotDetermineJsonPathException("Could not find attribute " + attribute + " within array!" + array);
    }

    private static String getJsonPath(JsonObject object, String attribute) throws CouldNotDetermineJsonPathException {
        for (String name : object.names()) {
            try {
                return "['" + name + "']" + getJsonPath(object.get(name), attribute);
            } catch (CouldNotDetermineJsonPathException ignored) { /* Ignore this, maybe attribute is in next name */ }
        }
        throw new CouldNotDetermineJsonPathException("Could not find attribute " + attribute + " within object!" + object);
    }
}
