package de.hpi.shoprulesgenerator.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import lombok.*;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
class Script {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String content;

    Script(String content) {
        getObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        setContent(content);
    }

    Script getBlock(int blockIndex) {
        int bracketCount = blockIndex;
        int startIndex = getContent().indexOf('{');
        if (startIndex == -1) throw new BlockNotFoundException("There is no with the given block index " + blockIndex);
        for (int iChar = startIndex; iChar < getContent().length(); iChar++) {
            char c = getContent().charAt(iChar);
            if (c == '{') bracketCount++;
            else if (c == '}') bracketCount--;
            if (bracketCount == 0) return new Script(getContent().substring(startIndex, iChar + 1));
        }
        throw new BlockNotFoundException("Malformed JSON! Could not find block");
    }

    Script getFirstBlock() {
        return getBlock(0);
    }

    boolean containsBlock() {
        int indexOfFirstBracket = getContent().indexOf('{');
        return indexOfFirstBracket != -1 && getContent().substring(indexOfFirstBracket).contains("}");
    }

    @SuppressWarnings("squid:S3516")
    boolean isJSONLeaf() {
        try {
            getObjectMapper().readValue(getContent(), new TypeReference<Map<String, Object>>(){});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    Script toValidJson() throws IOException {
        //use jackson to convert to valid json and then convert back to string/ script object
        return new Script(getObjectMapper().writeValueAsString(getObjectMapper().readValue(getContent(), new
                TypeReference<Map<String, Object>>(){})));
    }

    boolean containsAttribute(String attribute) {
        return getContent().toLowerCase().contains(attribute.toLowerCase());
    }
}
