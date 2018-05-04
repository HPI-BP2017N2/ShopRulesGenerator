package de.hpi.shoprulesgenerator.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public abstract class Selector {

    public enum NodeType {
        TEXT_NODE,
        ATTRIBUTE_NODE
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
    @SuppressWarnings("squid:S1206")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof Selector)) return false;
        Selector other = (Selector) o;
        return Double.compare(getNormalizedScore(), other.getNormalizedScore()) == 0 &&
                getScore() == other.getScore() &&
                getLeftCutIndex() == other.getLeftCutIndex() &&
                getRightCutIndex() == other.getRightCutIndex() &&
                getNodeType().equals(other.getNodeType()) &&
                getCssSelector().equals(other.getCssSelector());
    }
}
