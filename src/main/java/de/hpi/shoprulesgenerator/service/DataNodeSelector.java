package de.hpi.shoprulesgenerator.service;

import lombok.*;
import org.springframework.data.annotation.PersistenceConstructor;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class DataNodeSelector extends Selector {

    private Path pathToBlock;

    private String jsonPath;

    @PersistenceConstructor
    DataNodeSelector(String cssSelector, Path pathToBlock, String jsonPath) {
        super(NodeType.DATA_NODE, cssSelector);
        setPathToBlock(pathToBlock);
        setJsonPath(jsonPath);
    }

    DataNodeSelector(String cssSelector, String attribute, String textContainingAttribute, Path path, String jsonPath) {
        super(NodeType.DATA_NODE, cssSelector, attribute, textContainingAttribute);
        setPathToBlock(path);
        setJsonPath(jsonPath);
    }

}
