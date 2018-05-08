package de.hpi.shoprulesgenerator.service;

import java.util.LinkedList;

class Path extends LinkedList<PathID> {

    Path() {
        add(new PathID());
    }

    Path cloneAndAddPathID() {
        Path clone = (Path) clone();
        clone.add(new PathID());
        return clone;
    }
}
