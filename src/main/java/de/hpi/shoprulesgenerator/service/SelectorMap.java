package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class SelectorMap extends EnumMap<OfferAttribute, Set<Selector>> {

    @Getter(AccessLevel.PRIVATE) private static final String[] PRICE_SEPARATORS = new String[] {".", ","};

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

    void normalizeScore(EnumMap<OfferAttribute, Integer> countMap) {
        forEach((offerAttribute, selectors) -> selectors.stream()
                .filter(selector -> countMap.get(offerAttribute) > 0)
                .forEach(selector -> selector.setNormalizedScore(
                        (selector.getScore() + countMap.get(offerAttribute) - 1.0) /
                                (2.0 * countMap.get(offerAttribute) - 1.0))));
        updateSelectorHashes();
    }

    void filter(double threshold) {
        values().forEach(selectors ->
                selectors.removeIf(selector -> selector.getNormalizedScore() < threshold));
    }

    void updateSelectorHashes() { //needs to get called, after a selector got mutated
        forEach((key, value) -> put(key, new HashSet<>(value)));
    }

    private static Set<Selector> buildSelectors(IdealoOffer offer, OfferAttribute offerAttribute, List
            <SelectorGenerator> generators) {
        if (offer.get(offerAttribute) == null) return Collections.emptySet();
        return offer.get(offerAttribute).stream()
                .map(value -> {
                    if (OfferAttribute.PRICE.equals(offerAttribute)) return buildPriceSpecificSelectors(offer, value, generators);
                    return buildSelectorForOfferAttributeValue(offer, value, generators);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static Set<Selector> buildPriceSpecificSelectors(IdealoOffer offer, String price, List<SelectorGenerator> generators) {

        return Arrays.stream(getPRICE_SEPARATORS())
                .map(separator -> buildSelectorForOfferAttributeValue(offer, new StringBuilder(price).insert(price
                        .length() - 2, separator).toString(), generators))
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
