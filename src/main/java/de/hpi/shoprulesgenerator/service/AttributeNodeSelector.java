package de.hpi.shoprulesgenerator.service;

import lombok.*;
import org.springframework.data.annotation.PersistenceConstructor;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class AttributeNodeSelector extends Selector {

    private String attributeName;

    @PersistenceConstructor
    AttributeNodeSelector(String cssSelector, String attributeName){
        super(NodeType.ATTRIBUTE_NODE, cssSelector);
        setAttributeName(attributeName);
    }

    AttributeNodeSelector(String cssSelector, String attributeName, String attribute, String textContainingAttribute) {
        super(NodeType.ATTRIBUTE_NODE, cssSelector, attribute, textContainingAttribute);
        setAttributeName(attributeName);
    }

}
