package de.hpi.shoprulesgenerator.service;

import org.jsoup.nodes.Document;

import java.util.List;

public interface SelectorGenerator {

    List<Selector> buildSelectors(Document html, String attribute);

    default String escapeQuotes(String attribute) {
        return attribute
                .replace("\"", "\\\"")
                .replace("\'", "\\\'");
    }
}
