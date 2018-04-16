package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE) class TextNodeSelector extends Selector {

    TextNodeSelector(String cssSelector) {
        super(cssSelector, NodeType.TEXT_NODE);
    }
}
