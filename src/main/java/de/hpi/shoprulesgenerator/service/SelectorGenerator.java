package de.hpi.shoprulesgenerator.service;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.regex.Pattern;

public interface SelectorGenerator {

    List<Selector> buildSelectors(Document html, String attribute);

    default String buildCSSQuery(String template, String attribute) {
        String escapedAttribute = Pattern.quote(attribute);
        escapedAttribute = escapedAttribute
                .replace("\"", "\\\"")
                .replace("\'", "\\\'");
        return template.replace("#attr#", escapedAttribute);
    }
}
