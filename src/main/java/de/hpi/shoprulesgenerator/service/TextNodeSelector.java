package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
class TextNodeSelector extends Selector {

    TextNodeSelector(String cssSelector) {
        super(cssSelector, NodeType.TEXT_NODE);
    }
}
