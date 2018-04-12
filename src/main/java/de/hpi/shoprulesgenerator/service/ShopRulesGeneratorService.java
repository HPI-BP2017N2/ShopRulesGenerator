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
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorService implements IShopRulesGeneratorService {

    private final IdealoBridge idealoBridge;

    private final IShopRulesRepository shopRulesRepository;

    private final ShopRulesGeneratorConfig config;

    private final URLCleaner urlCleaner;

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
        List<IdealoOffer> idealoOffers = getIdealoBridge().getSampleOffers(shopID);
        fetchHtmlPages(idealoOffers, shopID);
    }

    private void fetchHtmlPages(List<IdealoOffer> idealoOffers, long shopID) {
        for (Iterator<IdealoOffer> iterator = idealoOffers.iterator(); iterator.hasNext();) {
            IdealoOffer offer = iterator.next();
            fetchHtmlPage(offer, shopID);
            if (iterator.hasNext()) {
                try {
                    Thread.sleep(getConfig().getFetchDelay());
                } catch (InterruptedException e) {
                    log.warn("Error while waiting between two fetches.", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void fetchHtmlPage(IdealoOffer offer, long shopID) {
        String cleanUrl = cleanUrl(offer.getOfferAttribute(OfferAttribute.URL), shopID);
        try {
            Document fetchedPage = Jsoup.connect(cleanUrl).userAgent(getConfig().getUserAgent()).get();
            offer.setFetchedPage(fetchedPage);
        } catch (IOException e) {
            log.error("Could not fetch page for: " + cleanUrl, e);
        }
    }

    private String cleanUrl(List<String> urls, long shopID) {
        if (urls.isEmpty()) {
            return null;
        }
        return getUrlCleaner().cleanURL(urls.get(0), shopID);
    }
}
