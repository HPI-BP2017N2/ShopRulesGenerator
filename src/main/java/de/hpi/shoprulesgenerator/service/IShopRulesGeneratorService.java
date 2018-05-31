package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;

public interface IShopRulesGeneratorService {


    /**
     * Check whether the rules for the shop with the specified id already exist.<br />
     * If the rules do not exist, they get generated asynchronously.<br />
     * Multiple calls will not trigger multiple generation processes.
     * @param shopID The id of the shop
     * @return The rules for the shop with the id
     * @throws ShopRulesDoNotExistException Indicates, that rules are not existing yet.
     */
    ShopRules getRules(long shopID) throws ShopRulesDoNotExistException;

}
