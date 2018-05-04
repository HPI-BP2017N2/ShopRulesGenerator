package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceConstructor;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
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
    @SuppressWarnings("squid:S1206")
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (!(o instanceof AttributeNodeSelector)) return false;
        AttributeNodeSelector other = (AttributeNodeSelector) o;
        return getAttributeName().equals(other.getAttributeName());
    }
}
