package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
class PathID{

    private int id;

    void increment() {
        setId(getId() + 1);
    }

}
