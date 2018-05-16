package de.hpi.shoprulesgenerator.service;

import lombok.*;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Objects;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AttributeNodeSelector selector = (AttributeNodeSelector) o;
        return Objects.equals(attributeName, selector.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attributeName);
    }
}
