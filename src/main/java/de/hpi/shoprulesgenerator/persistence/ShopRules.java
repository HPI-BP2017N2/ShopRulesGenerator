package de.hpi.shoprulesgenerator.persistence;

import de.hpi.shoprulesgenerator.service.SelectorMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ShopRules {

    private final SelectorMap selectorMap;
    private final long shopID;
}
