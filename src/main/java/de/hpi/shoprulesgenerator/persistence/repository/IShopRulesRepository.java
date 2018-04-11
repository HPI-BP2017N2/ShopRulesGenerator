package de.hpi.shoprulesgenerator.persistence.repository;

import de.hpi.shoprulesgenerator.persistence.ShopRules;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IShopRulesRepository extends MongoRepository<ShopRules, Long> {

    ShopRules findByShopID(long shopID);
}
