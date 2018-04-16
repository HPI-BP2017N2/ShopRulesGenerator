package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TextNodeSelectorGeneratorTest {

    private Document exampleHTML;

    @Before
    public void setup() throws IOException {
        setExampleHTML(Jsoup.parse(getClass().getClassLoader().getResourceAsStream("TextNodeSelectorGenerator1.html"),
                "UTF-8", "https://www.saturn.de"));
    }

    @Test
    public void buildSelectors() {
        List<Selector> selectors = new TextNodeSelectorGenerator().buildSelectors(getExampleHTML(), "771420-0010");
        Selector selector = new TextNodeSelector("#features section:nth-of-type(4) dl:nth-of-type(1) dd:nth-of-type" +
                "(3)");
        assertTrue(selectors.contains(selector));
    }

    // TODO: test for attribute that comes more then 1 time on the page (like "2305851")
    //for now<: contains -> goal: equals
}