package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.dto.Property;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.stream.Collectors;

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

    public void setEan(Property<String> ean) {
        getOfferAttributes().put(OfferAttribute.EAN, toList(ean.getValue()));
    }

    public void setSku(Property<String> sku) {
        getOfferAttributes().put(OfferAttribute.SKU, toList(sku.getValue()));
    }

    public void setHan(Property<String> han) {
        getOfferAttributes().put(OfferAttribute.HAN, toList(han.getValue()));
    }

    public void setTitles(Property<Map<String, String>> titles) {
        getOfferAttributes().put(OfferAttribute.TITLE, toList(titles.getValue()));
    }

    public void setCategoryPaths(Property<String[]> categoryPaths) {
        getOfferAttributes().put(OfferAttribute.CATEGORY, toList(categoryPaths.getValue()));
    }

    public void setBrandName(Property<String> brandName) {
        getOfferAttributes().put(OfferAttribute.BRAND, toList(brandName.getValue()));
    }

    public void setPrice(Property<Map<String, Integer>> price) {
        getOfferAttributes().put(OfferAttribute.PRICE, toList(price.getValue()));
    }

    public void setDescription(Property<Map<String, String>> description) {
        getOfferAttributes().put(OfferAttribute.DESCRIPTION, toList(description.getValue()));
    }

    public void setUrls(Property<Map<String, String>> urls) {
        getOfferAttributes().put(OfferAttribute.URL, toList(urls.getValue()));
    }

    List<String> get(OfferAttribute attribute) {
        return getOfferAttributes().get(attribute);
    }

    boolean has(OfferAttribute attribute) { return getOfferAttributes().containsKey(attribute); }


    //convert
    private List<String> toList(Object object) {
        return Collections.singletonList(String.valueOf(object));
    }

    private List<String> toList(Map<String, ?> map) {
        return map.values().stream().map(String::valueOf).collect(Collectors.toList());
    }

    private List<String> toList(String[] array) {
        return Arrays.asList(array);
    }
}
