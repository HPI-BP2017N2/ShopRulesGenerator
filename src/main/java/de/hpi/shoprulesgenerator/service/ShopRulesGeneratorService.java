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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.hpi.shoprulesgenerator.service.SelectorMap.buildSelectorMap;

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
        SelectorMap selectorMap = buildSelectorMap(idealoOffers, getGenerators());
        calculateScoreForSelectors(idealoOffers, selectorMap);
        selectorMap.normalizeScore(idealoOffers.size());
        selectorMap.filter(getConfig().getScoreThreshold());
        ShopRules rules = new ShopRules(selectorMap, shopID);
        getShopRulesRepository().save(rules);
        log.info("Created rules for shop " + shopID);

        getGenerateProcesses().remove(shopID);
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

}
