package de.hpi.shoprulesgenerator.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
class Script {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String content;

    Script(String content) {
        allowAllObjectMapperFeatures();
        setContent(content);
    }

    private void allowAllObjectMapperFeatures() {
        Arrays.stream(JsonParser.Feature.values()).forEach(feature -> getObjectMapper().configure(feature, true));
    }

    Script getBlock(int blockIndex) {
        int currentBlockID = 0;
        int bracketCount = 0;
        int startIndex = getContent().indexOf('{');
        if (startIndex == -1) throw new BlockNotFoundException("There is no block within this script");

        for (int iChar = startIndex; iChar < getContent().length(); iChar++) {
            char c = getContent().charAt(iChar);
            if (c == '{') bracketCount++;
            else if (c == '}') bracketCount--;
            if (bracketCount == 0) {
                if (currentBlockID == blockIndex) return new Script(getContent().substring(startIndex, iChar + 1));
                startIndex = getContent().indexOf('{', iChar + 1);
                if (startIndex == -1) break;
                iChar = startIndex - 1;
                currentBlockID++;
            }
        }
        throw new BlockNotFoundException("Could not find block with id " + blockIndex);
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
