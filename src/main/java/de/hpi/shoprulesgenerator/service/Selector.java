package de.hpi.shoprulesgenerator.service;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
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
