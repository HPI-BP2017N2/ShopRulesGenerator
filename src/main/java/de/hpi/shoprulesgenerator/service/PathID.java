package de.hpi.shoprulesgenerator.service;

import lombok.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
@NoArgsConstructor
@EqualsAndHashCode
class PathID{

    private int id;

    PathID(int id) {
        setId(id);
    }

    void increment() {
        setId(getId() + 1);
    }

}
