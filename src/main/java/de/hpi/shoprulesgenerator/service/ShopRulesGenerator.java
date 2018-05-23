package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static de.hpi.shoprulesgenerator.service.SelectorMap.buildSelectorMap;

@Component
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class ShopRulesGenerator {

    private final HTMLPageFetcher htmlPageFetcher;

    private final IdealoBridge idealoBridge;

    private final IShopRulesRepository shopRulesRepository;

    private final ShopRulesGeneratorConfig config;

    private List<SelectorGenerator> generators =
            Arrays.asList(
                    new AttributeNodeSelectorGenerator(),
                    new TextNodeSelectorGenerator(),
                    new DataNodeSelectorGenerator());

    private Set<Long> generateProcesses = new CopyOnWriteArraySet<>();

    @Async
    public void generateShopRules(long shopID) {
        if (getGenerateProcesses().contains(shopID)) return;
        getGenerateProcesses().add(shopID);

        IdealoOffers idealoOffers = getIdealoBridge().getSampleOffers(shopID);
        getHtmlPageFetcher().fetchHTMLPages(idealoOffers, shopID);
        SelectorMap selectorMap = buildSelectorMap(idealoOffers, getGenerators());
        calculateScoreForSelectors(idealoOffers, selectorMap);
        selectorMap.normalizeScore(calculateCountMap(idealoOffers));
        selectorMap.updateSelectorHashes();
        selectorMap.filter(getConfig().getScoreThreshold());
        ShopRules rules = new ShopRules(selectorMap, shopID);
        logRuleStatus(rules);
        getShopRulesRepository().save(rules);
        log.info("Created rules for shop " + shopID);

        getGenerateProcesses().remove(shopID);
    }

    private void logRuleStatus(ShopRules rules) {
        if (shouldDropRule(rules.getSelectorMap())) {
            log.error("Failed to fetch any qualified rule for shop " + rules.getShopID() + ". Storing empty rules " +
                    "anyway.");
        }
    }

    private EnumMap<OfferAttribute,Integer> calculateCountMap(IdealoOffers idealoOffers) {
        EnumMap<OfferAttribute, Integer> countMap = new EnumMap<>(OfferAttribute.class);
        Arrays.stream(OfferAttribute.values())
                .forEach(offerAttribute ->
                        idealoOffers.forEach(idealoOffer ->
                                countMap.put(offerAttribute, countMap.getOrDefault(offerAttribute, 0) +
                                        (idealoOffer.has(offerAttribute) ? 1 : 0))));
        return countMap;
    }

    private void calculateScoreForSelectors(IdealoOffers idealoOffers, SelectorMap selectorMap) {
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

    private boolean shouldDropRule(SelectorMap selectorMap) {
        return selectorMap.values().stream().allMatch(Set::isEmpty);
    }
}
