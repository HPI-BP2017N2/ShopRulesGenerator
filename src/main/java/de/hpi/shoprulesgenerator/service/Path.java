package de.hpi.shoprulesgenerator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

class Path extends LinkedList<PathID> {

    Path() {
        add(new PathID());
    }

    Path(Collection<? extends PathID> c) {
        super(c);
    }

    private Path(Path pathToClone) {
        addAll(pathToClone.stream()
                .map(pathID -> new PathID(pathID.getId()))
                .collect(Collectors.toList()));
    }

    Path cloneAndAddPathID() {
        Path clone = new Path(this);
        clone.add(new PathID());
        return clone;
    }
}
