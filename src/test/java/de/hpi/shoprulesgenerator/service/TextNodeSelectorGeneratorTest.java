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
public class TextNodeSelectorGeneratorTest {

    private Document exampleHTML;

    @Before
    public void setup() throws IOException {
        setExampleHTML(Jsoup.parse(getClass().getClassLoader().getResourceAsStream("TextNodeSelectorGenerator.html"),
                "UTF-8", "https://www.saturn.de"));
    }

    @Test
    public void buildSelectorsForAttributeWithSingleOccurrence() {
        List<Selector> selectors = new TextNodeSelectorGenerator().buildSelectors(getExampleHTML(), "771420-0010");
        Selector selector = new TextNodeSelector("#features > section:nth-of-type(4) > dl:nth-of-type(1) > " +
                "dd:nth-of-type(3)");
        assertTrue(selectors.contains(selector));
    }

    @Test
    public void buildSelectorsForAttributeWithMultipleOccurrence() {
        List<Selector> selectors = new TextNodeSelectorGenerator().buildSelectors(getExampleHTML(), "2305851");
        Selector selectorA = new TextNodeSelector("#product-details > div:nth-of-type(2) > div:nth-of-type(1) > dl:nth-of-type(1) > dd:nth-of-type(1) > span:nth-of-type(1)");
        Selector selectorB = new TextNodeSelector("#features > section:nth-of-type(1) > dl:nth-of-type(1) > dd:nth-of-type(2)");
        assertTrue(selectors.containsAll(Arrays.asList(selectorA, selectorB)));
    }

}