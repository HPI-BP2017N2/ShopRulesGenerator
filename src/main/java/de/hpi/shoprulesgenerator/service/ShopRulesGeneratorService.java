package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final IShopRulesRepository shopRulesRepository;

    private final ShopRulesGenerator shopRulesGenerator;

    @Override
    @Cacheable(value = "rules")
    public ShopRules getRules(long shopID) throws ShopRulesDoNotExistException {
        ShopRules rules = getShopRulesRepository().findByShopID(shopID);
        if (rules == null) {
            new Thread(() -> getShopRulesGenerator().generateShopRules(shopID)).start();
            throw new ShopRulesDoNotExistException("There are no rules for the shop " + shopID + ". Started generating rules...");
        }
        return rules;
    }

}
