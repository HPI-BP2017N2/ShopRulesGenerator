package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class AttributeNodeSelectorGeneratorTest {

    private Document exampleHTML;

    @Before
    public void setup() throws IOException {
        setExampleHTML(Jsoup.parse(getClass().getClassLoader().getResourceAsStream("AttributeNodeSelectorGenerator" +
                        ".html"),
                "UTF-8", "https://www.saturn.de"));
    }

    @Test
    public void buildSelectorsForAttribute() {
        List<Selector> selectors = new AttributeNodeSelectorGenerator().buildSelectors(getExampleHTML(), "2305851");
        Selector selectorA = new AttributeNodeSelector("#product-details > div:nth-of-type(2) > div:nth-of-type(1) > dl:nth-of-type(1) > dd:nth-of-type(1) > span:nth-of-type(1)[content]", "content");
        Selector selectorB = new AttributeNodeSelector("#product-sidebar > div:nth-of-type(5) > form:nth-of-type(1) > div:nth-of-type(2) > a:nth-of-type(1)[data-gtm-event-ext]", "data-gtm-event-ext");
        Selector selectorC = new AttributeNodeSelector("span[itemprop=sku][content]:nth-of-type(0)", "content");
        assertTrue(selectors.containsAll(Arrays.asList(selectorA, selectorB, selectorC)));
    }

}