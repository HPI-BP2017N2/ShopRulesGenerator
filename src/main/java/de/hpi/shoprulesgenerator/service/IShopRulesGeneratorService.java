package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;

public interface IShopRulesGeneratorService {

    ShopRules getRules(long shopID) throws ShopRulesDoNotExistException;

}
