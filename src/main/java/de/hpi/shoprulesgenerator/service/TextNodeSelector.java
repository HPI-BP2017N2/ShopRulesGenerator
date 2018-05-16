package de.hpi.shoprulesgenerator.service;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
class TextNodeSelector extends Selector {

    @PersistenceConstructor
    TextNodeSelector(String cssSelector) {
        super(NodeType.TEXT_NODE, cssSelector);
    }

    TextNodeSelector(String cssSelector, String attribute, String textContainingAttribute) {
        super(NodeType.TEXT_NODE, cssSelector, attribute, textContainingAttribute);
    }

}
