package de.hpi.shoprulesgenerator.service;

import lombok.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class DataNodeSelector extends Selector {

    private Path pathToBlock;

    private String jsonPath;

    DataNodeSelector(String cssSelector, Path path, String jsonPath) {
        super(NodeType.DATA_NODE, cssSelector);
        setPathToBlock(path);
        setJsonPath(jsonPath);
    }

    DataNodeSelector(String cssSelector, String attribute, String textContainingAttribute, Path path, String jsonPath) {
        super(NodeType.DATA_NODE, cssSelector, attribute, textContainingAttribute);
        setPathToBlock(path);
        setJsonPath(jsonPath);
    }
}
