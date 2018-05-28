package de.hpi.shoprulesgenerator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

class Path extends LinkedList<PathID> {

    Path() {}

    Path(Collection<? extends PathID> c) {
        super(c);
    }

    private Path(Path pathToClone) {
        addAll(pathToClone.stream()
                .map(pathID -> new PathID(pathID.getId()))
                .collect(Collectors.toList()));
    }

    Path copy() {
        return new Path(this);
    }
}
