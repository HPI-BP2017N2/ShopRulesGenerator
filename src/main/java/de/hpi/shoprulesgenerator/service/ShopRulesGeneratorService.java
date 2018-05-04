package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final HTMLPageFetcher htmlPageFetcher;

    private final IdealoBridge idealoBridge;

    private List<SelectorGenerator> generators =
            Arrays.asList(new AttributeNodeSelectorGenerator(), new TextNodeSelectorGenerator());

    private final IShopRulesRepository shopRulesRepository;

    private Set<Long> generateProcesses = new HashSet<>();

    private final ShopRulesGeneratorConfig config;

    @Override
    public ShopRules getRules(long shopID) throws ShopRulesDoNotExistException {
        ShopRules rules = getShopRulesRepository().findByShopID(shopID);
        if (rules == null) {
            new Thread(() -> generateShopRules(shopID)).start();
            throw new ShopRulesDoNotExistException("There are no rules for the shop " + shopID);
        }
        return rules;
    }

    //actions
    private void generateShopRules(long shopID) {
        if (getGenerateProcesses().contains(shopID)) return;
        getGenerateProcesses().add(shopID);

        IdealoOffers idealoOffers = getIdealoBridge().getSampleOffers(shopID);
        getHtmlPageFetcher().fetchHTMLPages(idealoOffers, shopID);
        EnumMap<OfferAttribute, Set<Selector>> selectorMap = buildSelectorMap(idealoOffers);
        calculateScoreForSelectors(idealoOffers, selectorMap);
        normalizeScore(selectorMap, idealoOffers.size());
        filterSelectors(selectorMap);
        ShopRules rules = new ShopRules(selectorMap, shopID);
        getShopRulesRepository().save(rules);
        log.info("Created rules for shop " + shopID);

        getGenerateProcesses().remove(shopID);
    }

    private void filterSelectors(EnumMap<OfferAttribute, Set<Selector>> selectorMap) {
        selectorMap.values().forEach(selectors ->
                selectors.removeIf(selector -> selector.getNormalizedScore() < getConfig().getScoreThreshold()));
    }

    private void normalizeScore(EnumMap<OfferAttribute, Set<Selector>> selectorMap, int offerCount) {
        if (offerCount == 0) return;
        selectorMap.forEach((key, value) -> value.forEach(
                selector -> selector.setNormalizedScore(
                        (selector.getScore() + offerCount - 1.0) / (2.0 * offerCount - 1.0))));
    }

    private void calculateScoreForSelectors(IdealoOffers idealoOffers, EnumMap<OfferAttribute, Set<Selector>> selectorMap) {
        idealoOffers.forEach(idealoOffer ->
                selectorMap.forEach((offerAttribute, selectors) -> selectors.forEach(
                        selector -> updateScoreForSelector(idealoOffer, offerAttribute, selector))));
    }

    private void updateScoreForSelector(IdealoOffer idealoOffer, OfferAttribute attribute, Selector selector) {
        if (!idealoOffer.has(attribute)) return;
        String extractedData = DataExtractor.extract(idealoOffer.getFetchedPage(), selector);
        if (doesMatch(extractedData, idealoOffer.get(attribute))){
            selector.incrementScore();
        } else if (!extractedData.isEmpty()) {
            selector.decrementScore();
        }
    }

    private boolean doesMatch(String extractedData, List<String> offerAttributes) {
        return offerAttributes.stream().anyMatch(extractedData::equalsIgnoreCase);
    }

    private EnumMap<OfferAttribute, Set<Selector>> buildSelectorMap(IdealoOffers idealoOffers) {
        EnumMap<OfferAttribute, Set<Selector>> selectorMap = createEmptySelectorMap();
        idealoOffers.forEach(offer ->
                Arrays.stream(OfferAttribute.values()).forEach(offerAttribute ->
                        selectorMap.get(offerAttribute).addAll(buildSelectors(offer, offerAttribute))));
        return selectorMap;
    }

    private EnumMap<OfferAttribute, Set<Selector>> createEmptySelectorMap() {
        EnumMap<OfferAttribute, Set<Selector>> selectorMap = new EnumMap<>(OfferAttribute.class);
        Arrays.stream(OfferAttribute.values()).forEach(offerAttribute ->
                selectorMap.put(offerAttribute, new LinkedHashSet<>()));
        return selectorMap;
    }

    private Set<Selector> buildSelectors(IdealoOffer offer, OfferAttribute offerAttribute) {
        if (offer.get(offerAttribute) == null) return Collections.emptySet();
        return offer.get(offerAttribute).stream()
                .map(value -> buildSelectorForOfferAttributeValue(offer, value))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Selector> buildSelectorForOfferAttributeValue(IdealoOffer offer, String offerAttributeValue) {
        return getGenerators().stream()
                .map(generator -> generator.buildSelectors(offer.getFetchedPage(), offerAttributeValue))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
