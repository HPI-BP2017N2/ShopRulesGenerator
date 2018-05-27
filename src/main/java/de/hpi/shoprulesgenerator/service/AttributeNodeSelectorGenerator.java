package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class AttributeNodeSelectorGenerator extends TextNodeSelectorGenerator {

    @SuppressWarnings("ConstantConditions") //null check not necessary, since we filter occurrences out, where attr
    // does not occur
    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select("*")
                .stream()
                .filter(element -> hasAttributeContainingOfferAttribute(element, attribute))
                .map(occurrence -> {
                    Attribute selectorAttribute = getAttributeForOfferAttribute(occurrence, attribute);
                    return Arrays.asList(
                            generateNumericSelector(selectorAttribute, occurrence, attribute),
                            generateNonNumericSelector(selectorAttribute, occurrence, attribute));})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private AttributeNodeSelector generateNonNumericSelector(Attribute selectorAttribute, Element occurrence, String attribute) {
        StringBuilder cssSelector = new StringBuilder(occurrence.tagName());
        for (Attribute attr : occurrence.attributes()) {
            if (attr.getKey().equals(selectorAttribute.getKey())) continue;
            cssSelector.append("[").append(attr.getKey());
            if (attr.getValue() != null && !attr.getValue().replace(" ", "").isEmpty())
                cssSelector.append("=").append(attr.getValue());
            cssSelector.append("]");
        }
        cssSelector.append("[").append(selectorAttribute.getKey()).append("]");
        return new AttributeNodeSelector(
                cssSelector.toString(),
                selectorAttribute.getKey(),
                attribute,
                selectorAttribute.getValue());
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
