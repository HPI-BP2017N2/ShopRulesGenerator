package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
class AttributeNodeSelector extends Selector {

    private String attributeName;

    AttributeNodeSelector(String cssSelector, String attributeName) {
        super(NodeType.ATTRIBUTE_NODE, cssSelector);
        setAttributeName(attributeName);
    }

    AttributeNodeSelector(String cssSelector, String attributeName, String attribute, String textContainingAttribute) {
        super(NodeType.ATTRIBUTE_NODE, cssSelector, attribute, textContainingAttribute);
        setAttributeName(attributeName);
    }
}
