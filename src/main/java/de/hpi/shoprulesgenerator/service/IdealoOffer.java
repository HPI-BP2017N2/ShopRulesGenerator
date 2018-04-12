package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

@Setter(AccessLevel.PRIVATE)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdealoOffer {

    @JsonIgnore
    private EnumMap<ProductAttribute, String[]> offerAttributes;

    public IdealoOffer() {
        setOfferAttributes(new EnumMap<>(ProductAttribute.class));
    }

    private String[] toArray(Object value) {
        return new String[] { String.valueOf(value) };
    }

    private String[] toArray(Map map) {
        Collection values = map.values();
        String[] mapValues = new String[values.size()];
        int index = 0;
        for (Object value : values) {
            mapValues[index++] = String.valueOf(value);
        }
        return mapValues;
    }

    public void setEans(String[] eans) {
        getOfferAttributes().put(ProductAttribute.EAN, eans);
    }

    public void setSku(String sku) {
        getOfferAttributes().put(ProductAttribute.SKU, toArray(sku));
    }

    public void setHans(String[] hans) {
        getOfferAttributes().put(ProductAttribute.HAN, hans);
    }

    public void setTitles(Map<String, String> titles) {
        getOfferAttributes().put(ProductAttribute.TITLE, toArray(titles));
    }

    public void setCategoryPaths(String[] categoryPaths) {
        getOfferAttributes().put(ProductAttribute.CATEGORY, categoryPaths);
    }

    public void setBrandName(String brandName) {
        getOfferAttributes().put(ProductAttribute.BRAND, toArray(brandName));
    }

    public void setPrice(Map<String, Integer> price) {
        getOfferAttributes().put(ProductAttribute.PRICE, toArray(price));
    }

    public void setDescription(Map<String, String> description) {
        getOfferAttributes().put(ProductAttribute.DESCRIPTION, toArray(description));
    }
}
