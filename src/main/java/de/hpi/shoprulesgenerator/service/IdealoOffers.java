package de.hpi.shoprulesgenerator.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class IdealoOffers extends LinkedList<IdealoOffer> {

    void removeRootUrlFromImages(String shopRootUrl) {
        forEach(idealoOffer -> {
            List<String> shortendImageUrls = idealoOffer.get(OfferAttribute.IMAGE_URLS).stream().map(
                    imageUrl -> imageUrl.replace(shopRootUrl, ""))
                    .collect(Collectors.toList());
            idealoOffer.get(OfferAttribute.IMAGE_URLS).clear();
            idealoOffer.get(OfferAttribute.IMAGE_URLS).addAll(shortendImageUrls);
        });
    }
}
