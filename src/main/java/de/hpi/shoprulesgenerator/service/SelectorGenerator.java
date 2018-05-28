package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import org.jsoup.nodes.Document;

import java.util.List;

abstract class SelectorGenerator {

    @Getter(AccessLevel.PRIVATE) private static final String[] REGEX_CHARACTERS = new String[] { "\\", ".", "[", "]", "{", "}", "(", ")",
            "*", "+", "-", "?", "^", "$", "|" };

    abstract List<Selector> buildSelectors(Document html, String attribute);

    String escapeQuotes(String attribute) {
        return attribute
                .replace("\"", "\\\"")
                .replace("\'", "\\\'");
    }

    String escapeRegex(String attribute) {
        for (String specialChar : getREGEX_CHARACTERS()) {
            attribute = attribute.replace(specialChar, "\\" + specialChar);
        }
        return attribute;
    }
}
