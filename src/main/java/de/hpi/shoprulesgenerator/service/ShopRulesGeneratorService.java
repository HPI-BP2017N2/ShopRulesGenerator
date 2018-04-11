package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final IShopRulesRepository shopRulesRepository;

    @Override
    public ShopRules getRules(long shopID) throws ShopRulesDoNotExistException {
        ShopRules rules = getShopRulesRepository().findByShopID(shopID);
        if (rules == null) { throw new ShopRulesDoNotExistException("There are no rules for the shop " + shopID); }
        return rules;
    }
}
