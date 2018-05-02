package de.hpi.shoprulesgenerator.service;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
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
        calculateCutIndices(attribute, textContainingAttribute);
    }

    private void calculateCutIndices(String attribute, String textContainingAttribute) {
        setLeftCutIndex(textContainingAttribute.indexOf(attribute));
        setRightCutIndex(textContainingAttribute.length() - (getLeftCutIndex() + attribute.length()));
    }

    void incrementScore() {
        setScore(getScore() + 1);
    }

    void decrementScore() {
        setScore(getScore() - 1);
    }
}
