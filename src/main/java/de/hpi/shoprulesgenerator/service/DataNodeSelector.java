package de.hpi.shoprulesgenerator.service;

import lombok.*;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Objects;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DataNodeSelector that = (DataNodeSelector) o;
        return Objects.equals(pathToBlock, that.pathToBlock) &&
                Objects.equals(jsonPath, that.jsonPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pathToBlock, jsonPath);
    }
}
