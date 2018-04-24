package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
class DataExtractor {

    private DataExtractor() {}

    static String extract(Document document, Selector selector) {
        if (selector.getNodeType() == Selector.NodeType.TEXT_NODE) {
            return extract(document, (TextNodeSelector) selector);
        } else if (selector.getNodeType() == Selector.NodeType.ATTRIBUTE_NODE) {
            return extract(document, (AttributeNodeSelector) selector);
        }
        return "";
    }

    private static String extract(Document document, AttributeNodeSelector selector) {
        Elements elements = document.select(selector.getCssSelector());
        return elements.isEmpty() ?  "" : elements.get(0).attr(selector.getAttributeName());
    }

    private static String extract(Document document, TextNodeSelector selector) {
        Elements elements = document.select(selector.getCssSelector());
        return (elements.isEmpty()) ? "" : elements.get(0).text();
    }
}
