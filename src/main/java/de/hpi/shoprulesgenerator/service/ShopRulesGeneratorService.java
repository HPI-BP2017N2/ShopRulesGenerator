package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final HTMLPageFetcher htmlPageFetcher;

    private final IdealoBridge idealoBridge;

    private final IShopRulesRepository shopRulesRepository;

    @Override
    public ShopRules getRules(long shopID) throws ShopRulesDoNotExistException {
        ShopRules rules = getShopRulesRepository().findByShopID(shopID);
        if (rules == null) {
            generateRule(shopID);
            throw new ShopRulesDoNotExistException("There are no rules for the shop " + shopID);
        }
        return rules;
    }

    //actions
    private void generateRule(long shopID) {
        IdealoOffers idealoOffers = getIdealoBridge().getSampleOffers(shopID);
        getHtmlPageFetcher().fetchHTMLPages(idealoOffers, shopID);

    }

}
