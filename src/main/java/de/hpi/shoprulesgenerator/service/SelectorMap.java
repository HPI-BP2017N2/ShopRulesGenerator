package de.hpi.shoprulesgenerator.service;

import java.util.*;
import java.util.stream.Collectors;

public class SelectorMap extends EnumMap<OfferAttribute, Set<Selector>> {

    static SelectorMap buildSelectorMap(IdealoOffers idealoOffers, List<SelectorGenerator> generators) {
        SelectorMap selectorMap = new SelectorMap();
        idealoOffers.forEach(offer ->
                Arrays.stream(OfferAttribute.values()).forEach(offerAttribute ->
                        selectorMap.get(offerAttribute).addAll(buildSelectors(offer, offerAttribute, generators))));
        return selectorMap;
    }

    static SelectorMap buildEmptySelectorMap() {
        return new SelectorMap();
    }

    private SelectorMap() {
        super(OfferAttribute.class);
        Arrays.stream(OfferAttribute.values()).forEach(offerAttribute -> put(offerAttribute, new HashSet<>()));
    }

    void normalizeScore(int offerCount) {
        if (offerCount == 0) return;
        forEach((key, value) -> value.forEach(
                selector -> selector.setNormalizedScore(
                        (selector.getScore() + offerCount - 1.0) / (2.0 * offerCount - 1.0))));
    }

    void filter(double threshold) {
        values().forEach(selectors ->
                selectors.removeIf(selector -> selector.getNormalizedScore() < threshold));
    }

    private static Set<Selector> buildSelectors(IdealoOffer offer, OfferAttribute offerAttribute, List
            <SelectorGenerator> generators) {
        if (offer.get(offerAttribute) == null) return Collections.emptySet();
        return offer.get(offerAttribute).stream()
                .map(value -> buildSelectorForOfferAttributeValue(offer, value, generators))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static Set<Selector> buildSelectorForOfferAttributeValue(IdealoOffer offer, String offerAttributeValue,
                                                                     List<SelectorGenerator> generators) {
        return generators.stream()
                .map(generator -> generator.buildSelectors(offer.getFetchedPage(), offerAttributeValue))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
