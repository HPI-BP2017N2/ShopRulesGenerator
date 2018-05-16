package de.hpi.shoprulesgenerator.service;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.jsoup.nodes.TextNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

public class SelectorMapTest {

    @SuppressWarnings("unchecked")
    @Test
    public void filter() {
        SelectorMap map = SelectorMap.buildEmptySelectorMap();
        TextNodeSelector selectorA = new TextNodeSelector("a");
        selectorA.setNormalizedScore(0.5);
        TextNodeSelector selectorB = new TextNodeSelector("b");
        selectorB.setNormalizedScore(0.4);
        TextNodeSelector selectorC = new TextNodeSelector("c");
        selectorC.setNormalizedScore(0.6);

        map.put(OfferAttribute.EAN, new HashSet<>(Arrays.asList(selectorA, selectorB, selectorC, selectorC)));
        map.filter(0.5);

        assertThat(map.get(OfferAttribute.EAN), containsInAnyOrder(
                hasProperty("cssSelector", is("a")),
                hasProperty("cssSelector", is("c"))
        ));
        assertEquals(2, map.get(OfferAttribute.EAN).size());
    }
}