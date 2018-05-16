package de.hpi.shoprulesgenerator.service;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
//@EqualsAndHashCode
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttributeNodeSelector.class, name = "attributeNodeSelector"),
        @JsonSubTypes.Type(value = DataNodeSelector.class, name = "dataNodeSelector"),
        @JsonSubTypes.Type(value = TextNodeSelector.class, name = "textNodeSelector")
})
public abstract class Selector {

    public enum NodeType {
        ATTRIBUTE_NODE,
        DATA_NODE,
        TEXT_NODE,
    }

    private double normalizedScore;

    private int score;

    private int leftCutIndex;

    private int rightCutIndex;

    private final NodeType nodeType;

    private final String cssSelector;

    public Selector(NodeType nodeType, String cssSelector, String attribute, String textContainingAttribute) {
        this(nodeType, cssSelector);
        if (!textContainingAttribute.toLowerCase().contains(attribute.toLowerCase())) {
            throw new IllegalArgumentException("Attribute has to be contained within given text!");
        }
        calculateCutIndices(attribute, textContainingAttribute);
    }

    private void calculateCutIndices(String attribute, String textContainingAttribute) {
        setLeftCutIndex(textContainingAttribute.toLowerCase().indexOf(attribute.toLowerCase()));
        setRightCutIndex(textContainingAttribute.length() - (getLeftCutIndex() + attribute.length()));
    }

    void incrementScore() {
        setScore(getScore() + 1);
    }

    void decrementScore() {
        setScore(getScore() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Selector selector = (Selector) o;
        return Double.compare(selector.normalizedScore, normalizedScore) == 0 &&
                score == selector.score &&
                leftCutIndex == selector.leftCutIndex &&
                rightCutIndex == selector.rightCutIndex &&
                nodeType == selector.nodeType &&
                Objects.equals(cssSelector, selector.cssSelector);
    }

    @Override
    public int hashCode() {

        return Objects.hash(normalizedScore, score, leftCutIndex, rightCutIndex, nodeType, cssSelector);
    }
}
