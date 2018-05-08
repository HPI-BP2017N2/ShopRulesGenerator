package de.hpi.shoprulesgenerator.service;

import com.eclipsesource.json.ParseException;
import com.jayway.jsonpath.JsonPath;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
class Script {

    private final String content;

    Script getBlock(int blockIndex) throws BlockNotFoundException {
        int bracketCount = blockIndex;
        int startIndex = getContent().indexOf('{');
        if (startIndex == -1) throw new BlockNotFoundException("There is no with the given block index " + blockIndex);
        for (int iChar = startIndex; iChar < getContent().length(); iChar++) {
            if (getContent().charAt(iChar) == '{') bracketCount++;
            else if (getContent().charAt(iChar) == '}') bracketCount--;
            if (bracketCount == 0) return new Script(getContent().substring(startIndex, iChar));
        }
        throw new BlockNotFoundException("Malformed JSON! Could not find block");
    }

    Script getFirstBlock() throws BlockNotFoundException {
        return getBlock(0);
    }

    boolean containsBlock() {
        int indexOfFirstBracket = getContent().indexOf('{');
        return indexOfFirstBracket != -1 && getContent().substring(indexOfFirstBracket).contains("}");
    }

    @SuppressWarnings("squid:S3516")
    boolean isJSONLeaf() {
        try {
            JsonPath.parse(getContent());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}
