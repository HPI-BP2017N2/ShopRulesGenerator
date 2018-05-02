package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class AttributeNodeSelectorGenerator extends TextNodeSelectorGenerator {

    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select("*")
                .stream()
                .filter(element -> hasAttributeContainingOfferAttribute(element, attribute))
                .map(occurrence -> {
                    String attributeKey = getAttributeKeyForOfferAttribute(occurrence, attribute);
                    return new AttributeNodeSelector(buildCssSelectorForOccurrence(occurrence, attributeKey), attributeKey);
                })
                .collect(Collectors.toList());
    }

    private String buildCssSelectorForOccurrence(Element occurrence, String attributeKey) {
        return buildCssSelectorForOccurrence(occurrence) + "[" + attributeKey + "]";
    }

    private boolean hasAttributeContainingOfferAttribute(Element element, String offerAttribute) {
        return getAttributeKeyForOfferAttribute(element, offerAttribute) != null;
    }

    private String getAttributeKeyForOfferAttribute(Element element, String offerAttribute) {
        offerAttribute = offerAttribute.toLowerCase();
        for (Attribute nodeAttribute : element.attributes()) {
            if (nodeAttribute.getValue() == null) continue;
            if (nodeAttribute.getValue().toLowerCase().contains(offerAttribute)) return nodeAttribute.getKey();
        }
        return null;
    }

}
