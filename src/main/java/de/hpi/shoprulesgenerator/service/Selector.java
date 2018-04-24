package de.hpi.shoprulesgenerator.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class Selector {

    public enum NodeType {
        TEXT_NODE,
        ATTRIBUTE_NODE
    }

    private double normalizedScore;

    private int score;

    private final NodeType nodeType;

    private final String cssSelector;

    void incrementScore() {
        setScore(getScore() + 1);
    }

    void decrementScore() {
        setScore(getScore() - 1);
    }
}
