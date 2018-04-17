package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
class AttributeNodeSelector extends Selector {

    private String attributeName;

    AttributeNodeSelector(String cssSelector, String attributeName) {
        super(cssSelector, NodeType.ATTRIBUTE_NODE);
        setAttributeName(attributeName);
    }
}
