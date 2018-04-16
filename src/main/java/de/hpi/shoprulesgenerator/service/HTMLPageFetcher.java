package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Getter(AccessLevel.PRIVATE)
@Component
@RequiredArgsConstructor
@Slf4j
public class HTMLPageFetcher {

    private final ShopRulesGeneratorConfig config;

    private final URLCleaner urlCleaner;

    void fetchHTMLPages(IdealoOffers offers, long shopID) {
        for (Iterator<IdealoOffer> iterator = offers.iterator(); iterator.hasNext();) {
            IdealoOffer offer = iterator.next();
            fetchHtmlPage(offer, shopID);
            if (iterator.hasNext()) sleep(getConfig().getFetchDelay());
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.warn("Error while waiting between two fetches.", e);
            Thread.currentThread().interrupt();
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
        return urls.isEmpty() ? null : getUrlCleaner().cleanURL(urls.get(0), shopID);
    }
}
