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
public class DataNodeSelectorGeneratorTest {

    private Document exampleHTML;

    @Before
    public void setup() throws IOException {
        setExampleHTML(Jsoup.parse(getClass().getClassLoader().getResourceAsStream("DataNodeSelectorGenerator.html"),
                "UTF-8", "https://www.saturn.de"));
    }

    @Test
    public void buildDataNodeSelectorsForAttribute() {
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(getExampleHTML(), "45678");
        System.out.println(selectors);
        // Selector selectorA = new AttributeNodeSelector("#product-details > div:nth-of-type(2) > div:nth-of-type(1)
        // > dl:nth-of-type(1) > dd:nth-of-type(1) > span:nth-of-type(1)[content]", "content");
      //  assertTrue(selectors.containsAll(Arrays.asList(selectorA, selectorB)));
    }
}