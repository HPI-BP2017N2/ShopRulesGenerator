package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private HTMLPageFetcher htmlPageFetcher;

    private IdealoBridge idealoBridge;

    private List<SelectorGenerator> generators;

    private IShopRulesRepository shopRulesRepository;

    private Set<Long> generateProcesses;


    @Autowired
    private ShopRulesGeneratorService(HTMLPageFetcher htmlPageFetcher, IdealoBridge idealoBridge,
                                      IShopRulesRepository shopRulesRepository) {
        setHtmlPageFetcher(htmlPageFetcher);
        setIdealoBridge(idealoBridge);
        setShopRulesRepository(shopRulesRepository);
        setGenerators(Arrays.asList(new AttributeNodeSelectorGenerator(), new TextNodeSelectorGenerator()));
        setGenerateProcesses(new HashSet<>());
    }

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
        ShopRules rules = new ShopRules(selectorMap, shopID);
        getShopRulesRepository().save(rules);
        log.info("Created rules for shop " + shopID);

        getGenerateProcesses().remove(shopID);
    }

    private EnumMap<OfferAttribute, Set<Selector>> buildSelectorMap(IdealoOffers idealoOffers) {
        EnumMap<OfferAttribute, Set<Selector>> selectorMap = new EnumMap<>(OfferAttribute.class);

        idealoOffers.forEach(offer ->
                Arrays.stream(OfferAttribute.values()).forEach(offerAttribute ->
                        selectorMap.put(offerAttribute, buildSelectors(offer, offerAttribute))));
        return selectorMap;
    }

    private Set<Selector> buildSelectors(IdealoOffer offer, OfferAttribute offerAttribute) {
        if (offer.get(offerAttribute) == null) return new HashSet<>();
        return offer.get(offerAttribute).stream()
                .map(value -> getGenerators().stream()
                        .map(generator -> generator.buildSelectors(offer.getFetchedPage(), value))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
