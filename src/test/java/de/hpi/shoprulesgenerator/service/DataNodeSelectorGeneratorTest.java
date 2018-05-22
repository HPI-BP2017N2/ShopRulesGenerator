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

    @Test
    public void buildDataNodeSelectorsForAttribute() throws IOException {
        Document exampleHTML = Jsoup.parse(getClass().getClassLoader().getResourceAsStream("DataNodeSelectorGenerator.html"),
                "UTF-8", "https://www.saturn.de");
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(exampleHTML, "45678");
        Path expectedPath = new Path(Arrays.asList(new PathID(0), new PathID(0)));
        assertTrue(selectors.stream().anyMatch(selector -> {
            DataNodeSelector dataNodeSelector = (DataNodeSelector) selector;
            if (dataNodeSelector.getPathToBlock().size() != expectedPath.size()) return false;
            for (int iPathID = 0; iPathID < dataNodeSelector.getPathToBlock().size(); iPathID++) {
                if (!expectedPath.get(iPathID).equals(dataNodeSelector.getPathToBlock().get(iPathID))) return false;
            }
            return true;
        }));

        assertEquals(1, selectors.size());

        String expectedJsonPath = "$['products'][1]['ean']";
        assertTrue(selectors.stream().anyMatch(selector -> expectedJsonPath.equals(((DataNodeSelector) selector)
                .getJsonPath())));
    }

    @Test
    public void buildNoDataSelectorsNoJson() throws IOException {
        Document exampleHTML = Jsoup.parse(getClass().getClassLoader().getResourceAsStream
                        ("DataNodeSelectorGenerator2.html"),"UTF-8", "https://www.saturn.de");
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(exampleHTML, "45678");
        assertTrue(selectors.isEmpty());
    }

    @Test
    public void buildNoDataSelectorsInvalidJson() throws IOException {
        Document exampleHTML = Jsoup.parse(getClass().getClassLoader().getResourceAsStream
                ("DataNodeSelectorGenerator3.html"),"UTF-8", "https://www.saturn.de");
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(exampleHTML, "45678");
        assertTrue(selectors.isEmpty());
    }

    @Test
    public void buildDataSelectorsMultipleOccurrenceInOneJson() throws IOException {
        Document exampleHTML = Jsoup.parse(getClass().getClassLoader().getResourceAsStream
                ("DataNodeSelectorGenerator4.html"),"UTF-8", "https://www.saturn.de");
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(exampleHTML, "45678");
        assertEquals(1, selectors.size());
    }

    @Test
    public void buildDataSelectorsForJS() throws IOException {
        Document exampleHTML = Jsoup.parse(getClass().getClassLoader().getResourceAsStream
                ("DataNodeSelectorGenerator5.html"),"UTF-8", "https://www.saturn.de");
        List<Selector> selectors = new DataNodeSelectorGenerator().buildSelectors(exampleHTML, "45678");
        assertEquals(0, selectors.size());
    }
}

