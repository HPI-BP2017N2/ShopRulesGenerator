package de.hpi.shoprulesgenerator.persistence;

import de.hpi.shoprulesgenerator.service.OfferAttribute;
import de.hpi.shoprulesgenerator.service.Selector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ShopRules {

    private final EnumMap<OfferAttribute, Set<Selector>> selectors;
    private final long shopID;
}
