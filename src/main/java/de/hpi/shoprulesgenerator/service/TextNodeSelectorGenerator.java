package de.hpi.shoprulesgenerator.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TextNodeSelectorGenerator implements SelectorGenerator {

    @Getter(AccessLevel.PRIVATE) private static final String CSS_QUERY_TEMPLATE = "*:containsOwn(#attr#)";

    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select(buildCSSQuery(attribute))
                .stream()
                .filter(occurrence -> doesTextContainOfferAttribute(occurrence.text(), attribute))
                .map(occurrence ->
                        new TextNodeSelector(buildCssSelectorForOccurrence(occurrence), attribute, occurrence.text()))
                .collect(Collectors.toList());
    }

    /**
     * @param occurrence - The target DOM-Element
     * @return A minimal selector with highest possible identity match to select the element out of html.
     */
    String buildCssSelectorForOccurrence(Element occurrence) {
        return buildCssSelectorForOccurrence(occurrence, new StringBuilder());
    }

    private boolean doesTextContainOfferAttribute(String text, String attribute) {
        return text.toLowerCase().contains(attribute.toLowerCase());
    }

    private String buildCssSelectorForOccurrence(Element element, StringBuilder selectorBuilder) {
        if (selectorBuilder.length() > 0) selectorBuilder.insert(0, " > ");
        if (isElementIDSet(element)) return selectorBuilder.insert(0,"#" + element.id()).toString();
        if (hasNoParentNode(element)) return element.tagName() + selectorBuilder.toString();
        selectorBuilder.insert(0, element.tagName() + ":nth-of-type(" + getTagIndexForChild(element) + ")");
        return buildCssSelectorForOccurrence(element.parent(), selectorBuilder);
    }

    /**
     * @param child The child element, that's tag index should get returned for
     * @return A 1-based index indicating umpteenth child this is of its own parent with the same tag as the child.
     */
    private int getTagIndexForChild(Element child){
        if (hasNoParentNode(child)) return -1;
        return child.parent().select("> " + child.tagName()).indexOf(child) + 1;
    }

    private String buildCSSQuery(String attribute) {
        String attrWithEscapedQuotes = escapeQuotes(attribute);
        return getCSS_QUERY_TEMPLATE().replace("#attr#", attrWithEscapedQuotes);
    }

    //conditionals
    private boolean isElementIDSet(Element element) {
        return !element.id().isEmpty();
    }

    private boolean hasNoParentNode(Element element) { return element.parent() == null || element.tagName()
            .equalsIgnoreCase("html"); }
}
