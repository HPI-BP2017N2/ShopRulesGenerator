package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class AttributeNodeSelectorGenerator extends TextNodeSelectorGenerator {

    @SuppressWarnings("ConstantConditions") //null check not necessary, since we filter ocurrences out, where attr
    // does not occur
    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select("*")
                .stream()
                .filter(element -> hasAttributeContainingOfferAttribute(element, attribute))
                .map(occurrence -> {
                    Attribute selectorAttribute = getAttributeForOfferAttribute(occurrence, attribute);
                    List<AttributeNodeSelector> selectors = new LinkedList<>();
                    selectors.add(generateNumericSelector(selectorAttribute, occurrence, attribute));
                    AttributeNodeSelector nonNumericSelector = generateNonNumericSelector(html, selectorAttribute,
                            occurrence, attribute);
                    if (nonNumericSelector != null) selectors.add(nonNumericSelector);
                    return selectors;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private AttributeNodeSelector generateNonNumericSelector(Document html, Attribute selectorAttribute, Element occurrence, String attribute) {
        StringBuilder cssSelector = new StringBuilder(occurrence.tagName());
        for (Attribute attr : occurrence.attributes()) {
            if (attr.getKey().equals(selectorAttribute.getKey())) continue;
            cssSelector.append("[").append(attr.getKey());
            if (attr.getValue() != null && !attr.getValue().isEmpty()) cssSelector.append("=").append(attr.getValue());
            cssSelector.append("]");
        }
        cssSelector.append("[").append(selectorAttribute.getKey()).append("]");
        int index = getIndex(cssSelector.toString(), selectorAttribute, html);
        if (index == -1) return null;
        cssSelector.append(":nth-of-type(").append(index).append(")");
        return new AttributeNodeSelector(
                cssSelector.toString(),
                selectorAttribute.getKey(),
                attribute,
                selectorAttribute.getValue());
    }

    private int getIndex(String tmpSelector, Attribute selectorAttribute, Document html) {
        tmpSelector = tmpSelector.replace("\"", "\\\"");
        Elements elements = html.select(tmpSelector);
        for (int iElement = 0; iElement < elements.size(); iElement++) {
            Element element = elements.get(iElement);
            if (element.hasAttr(selectorAttribute.getKey()) && element.attr(selectorAttribute.getKey()).equals(selectorAttribute.getValue()))
                return iElement;
        }
        return -1;
    }

    private AttributeNodeSelector generateNumericSelector(Attribute selectorAttribute, Element occurrence, String attribute) {
        return new AttributeNodeSelector(
                buildCssSelectorForOccurrence(occurrence, selectorAttribute.getKey()),
                selectorAttribute.getKey(),
                attribute,
                selectorAttribute.getValue());
    }

    private String buildCssSelectorForOccurrence(Element occurrence, String attributeKey) {
        return buildCssSelectorForOccurrence(occurrence) + "[" + attributeKey + "]";
    }

    private boolean hasAttributeContainingOfferAttribute(Element element, String offerAttribute) {
        return getAttributeForOfferAttribute(element, offerAttribute) != null;
    }

    private Attribute getAttributeForOfferAttribute(Element element, String offerAttribute) {
        offerAttribute = offerAttribute.toLowerCase();
        for (Attribute nodeAttribute : element.attributes()) {
            if (nodeAttribute.getValue() == null) continue;
            if (nodeAttribute.getValue().toLowerCase().contains(offerAttribute)) return nodeAttribute;
        }
        return null;
    }

}
