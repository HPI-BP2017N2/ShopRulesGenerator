package de.hpi.shoprulesgenerator.service;

import org.jsoup.nodes.Document;

import java.util.List;

public abstract class SelectorGenerator {

    public abstract List<Selector> buildSelectors(Document html, String attribute);

    String escapeQuotes(String attribute) {
        return attribute.replace("\"", "\\\"");
    }
}
