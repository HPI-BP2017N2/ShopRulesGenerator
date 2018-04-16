package de.hpi.shoprulesgenerator.service;

import java.util.List;

public abstract class SelectorGenerator {

    public abstract List<Selector> buildSelectors(IdealoOffer offer, String attribute);

    String escapeQuotes(String attribute) {
        return attribute.replace("\"", "\\\"");
    }
}
