package de.hpi.shoprulesgenerator.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
abstract class Selector {

    public enum NodeType {
        TEXT_NODE,
        ATTRIBUTE_NODE
    }

    private final String cssSelector;
    private final NodeType nodeType;
}
