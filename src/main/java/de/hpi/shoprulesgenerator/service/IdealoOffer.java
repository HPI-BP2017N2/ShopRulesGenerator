package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.jsoup.nodes.Document;

import java.util.*;

@Setter(AccessLevel.PRIVATE)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdealoOffer {

    @JsonIgnore
    private EnumMap<OfferAttribute, List<String>> offerAttributes;

    @JsonIgnore
    @Setter private Document fetchedPage;

    public IdealoOffer() {
        setOfferAttributes(new EnumMap<>(OfferAttribute.class));
    }

    private List<String> toList(Object object) {
        return Collections.singletonList(String.valueOf(object));
    }

    private List<String> toList(Map map) {
        Collection values = map.values();
        List<String> stringValues = new LinkedList<>();
        for (Object value : values) {
            stringValues.add(String.valueOf(value));
        }
        return stringValues;
    }

    private List<String> toList(String[] array) {
        return Arrays.asList(array);
    }

    public void setEans(String[] eans) {
        getOfferAttributes().put(OfferAttribute.EAN, toList(eans));
    }

    public void setSku(String sku) {
        getOfferAttributes().put(OfferAttribute.SKU, toList(sku));
    }

    public void setHans(String[] hans) {
        getOfferAttributes().put(OfferAttribute.HAN, toList(hans));
    }

    public void setTitles(Map<String, String> titles) {
        getOfferAttributes().put(OfferAttribute.TITLE, toList(titles));
    }

    public void setCategoryPaths(String[] categoryPaths) {
        getOfferAttributes().put(OfferAttribute.CATEGORY, toList(categoryPaths));
    }

    public void setBrandName(String brandName) {
        getOfferAttributes().put(OfferAttribute.BRAND, toList(brandName));
    }

    public void setPrice(Map<String, Integer> price) {
        getOfferAttributes().put(OfferAttribute.PRICE, toList(price));
    }

    public void setDescription(Map<String, String> description) {
        getOfferAttributes().put(OfferAttribute.DESCRIPTION, toList(description));
    }

    public void setUrls(Map<String, String> urls) {
        getOfferAttributes().put(OfferAttribute.URL, toList(urls));
    }

    List<String> getOfferAttribute(OfferAttribute attribute) {
        return getOfferAttributes().get(attribute);
    }
}
