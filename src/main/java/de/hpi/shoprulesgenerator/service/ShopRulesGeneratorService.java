package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final IdealoBridge idealoBridge;

    private final IShopRulesRepository shopRulesRepository;

    private final ShopRulesGeneratorConfig config;

    @Override
    public ShopRules getRules(long shopID) throws ShopRulesDoNotExistException {
        ShopRules rules = getShopRulesRepository().findByShopID(shopID);
        if (rules == null) { throw new ShopRulesDoNotExistException("There are no rules for the shop " + shopID); }
        return rules;
    }

    //actions
    private void generateRule(long shopID) {
        List<IdealoOffer> idealoOffers = getIdealoBridge().getSampleOffers(shopID);
        fetchHtmlPages(idealoOffers);
    }

    private void fetchHtmlPages(List<IdealoOffer> idealoOffers) {
        for (IdealoOffer offer : idealoOffers) {
            String cleanUrl = cleanUrl(offer.getOfferAttribute(OfferAttribute.URL));
            Document fetchedPage = null;
            try {
                fetchedPage = Jsoup.connect(cleanUrl)
                        .userAgent(getConfig().getUserAgent()).get();
            } catch (IOException e) {
                log.error("Could not fetch page for: " + cleanUrl, e);
            }
            offer.setFetchedPage(fetchedPage);
        }
    }

    private String cleanUrl(List<String> urls) {
        if (urls.isEmpty()) {
            return null;
        }
        return urls.get(0);
    }
}
