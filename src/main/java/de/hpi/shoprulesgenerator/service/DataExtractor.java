package de.hpi.shoprulesgenerator.service;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Slf4j
class DataExtractor {

    private DataExtractor() {}

    static String extract(Document document, Selector selector) {
        try {
            String extractedData = "";
            switch (selector.getNodeType()) {
                case ATTRIBUTE_NODE:
                    extractedData = extract(document, (AttributeNodeSelector) selector);
                    break;
                case DATA_NODE:
                    extractedData = extract(document, (DataNodeSelector) selector);
                    break;
                case TEXT_NODE:
                    extractedData = extract(document, (TextNodeSelector) selector);
                    break;
            }
            extractedData = cutAdditionalText(selector, extractedData);
            return extractedData;
        } catch (SelectorParseException e) {
            log.warn("Could not extract using selector: " + selector, e);
            return "";
        }
    }

    private static String cutAdditionalText(Selector selector, String extractedData) {
        if (selector.getLeftCutIndex() + selector.getRightCutIndex() >= extractedData.length()) return "";
        return extractedData.substring(selector.getLeftCutIndex(), extractedData.length() - selector.getRightCutIndex());
    }

    private static String extract(Document document, AttributeNodeSelector selector) {
        Elements elements = document.select(selector.getCssSelector());
        return elements.isEmpty() ?  "" : elements.get(0).attr(selector.getAttributeName());
    }

    private static String extract(Document document, TextNodeSelector selector) {
        Elements elements = document.select(selector.getCssSelector());
        return (elements.isEmpty()) ? "" : elements.get(0).text();
    }

    private static String extract(Document document, DataNodeSelector selector) {
        Elements elements = document.select(selector.getCssSelector());
        if (elements.isEmpty()) return "";
        Script block = new Script(elements.get(0).html());
        try {
            for (PathID id : selector.getPathToBlock()) {
                if (id != selector.getPathToBlock().getFirst()) block = removeOuterBrackets(block);
                block = block.getBlock(id.getId());
            }
            return extract(block, selector);
        } catch (BlockNotFoundException ignored) {
            return "";
        }
    }

    private static String extract(Script block, DataNodeSelector selector) {
        try {
            return JsonPath.parse(block.getContent()).read(selector.getJsonPath());
        } catch (InvalidJsonException | PathNotFoundException ignored) {
            return "";
        }
    }

    private static Script removeOuterBrackets(Script block) {
        String content = block.getContent();
        int firstBracketIndex = content.indexOf('{') + 1;
        int lastBracketIndex = content.lastIndexOf('}');
        return (firstBracketIndex > 0 && lastBracketIndex != -1) ? new Script(content.substring(firstBracketIndex,
                lastBracketIndex)) : block;
    }
}
