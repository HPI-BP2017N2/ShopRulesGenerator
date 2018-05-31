package de.hpi.shoprulesgenerator.service;

import com.jayway.jsonpath.JsonPath;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import de.hpi.shoprulesgenerator.exception.CouldNotDetermineJsonPathException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static de.hpi.shoprulesgenerator.service.JsonPathBuilder.createJsonPath;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class DataNodeSelectorGenerator extends TextNodeSelectorGenerator {

    @Getter(AccessLevel.PRIVATE) private static final String CSS_QUERY_TEMPLATE = "script";

    /**
     * This method generates selectors for occurrences of the wanted attribute within valid JSON in script tags.
     * @param html The document where the script tags are located.
     * @param attribute The value, for which the selectors should get build for.
     * @return A list of selectors, referencing the occurrences of the value in the specified HTML document.
     */
    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select(getCSS_QUERY_TEMPLATE())
                .stream()
                .filter(occurrence -> occurrence.html().toLowerCase().contains(attribute))
                .map(occurrence -> buildDataNodeSelectorDFS(occurrence, attribute))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Selector> buildDataNodeSelectorDFS(Element occurrence, String attribute) {
        List<Selector> selectors = new LinkedList<>();
        scriptBlockDFS(
                new Script(occurrence.data()),
                attribute,
                selectors,
                new Path(),
                buildCssSelectorForOccurrence(occurrence));
        return selectors;
    }

    private void scriptBlockDFS(Script script, String attribute, List<Selector> selectors, Path path, String cssSelector) {
        path.add(new PathID());
        try {
            while (hasBlockContainingAttribute(script, attribute)) {
                Script block = script.getFirstBlock();
                checkIfTargetJsonReached(cssSelector, block, path.copy(), attribute, selectors);
                path.getLast().increment();
                script = removeBlockFromScript(script, block);
            }
        } catch (BlockNotFoundException e) { log.warn("Invalid Javascript - skipping script: " + script.getContent()); }
    }

    private void checkIfTargetJsonReached(String cssSelector, Script script, Path path, String attribute, List<Selector> selectors) {
        if (script.isJSONLeaf()) {
            if (script.containsAttribute(attribute))
                addDataNodeSelector(selectors, cssSelector, script, path, attribute);
        } else {
            scriptBlockDFS(removeOuterBrackets(script), attribute, selectors, path, cssSelector);
        }
    }

    private void addDataNodeSelector(List<Selector> selectors, String cssSelector, Script script, Path path, String attribute) {
        try {
            selectors.add(buildSelector(cssSelector, script.toValidJson(), path, attribute));
        } catch (IOException | CouldNotDetermineJsonPathException e) {
            log.error("Failed to create DataNodeSelector! Script: " + script + " Path: " + path + " Attribute: " +
                    attribute, e);
        }
    }

    private Script removeOuterBrackets(Script block) {
        String content = block.getContent();
        int firstBracketIndex = content.indexOf('{') + 1;
        int lastBracketIndex = content.lastIndexOf('}');
        return (firstBracketIndex > 0 && lastBracketIndex != -1) ? new Script(content.substring(firstBracketIndex,
                lastBracketIndex)) : block;
    }

    private Script removeBlockFromScript(Script script, Script block) {
        int index = script.getContent().indexOf(block.getContent());
        return new Script(
                script.getContent().substring(0, index) +
                        script.getContent().substring(index + block.getContent().length()));
    }

    private boolean hasBlockContainingAttribute(Script script, String attribute) {
        return script.getContent().contains(attribute) && script.containsBlock();
    }

    private DataNodeSelector buildSelector(String cssSelector, Script snippet, Path path, String attribute) throws
            CouldNotDetermineJsonPathException {
        String jsonPath = createJsonPath(snippet, attribute);
        Object data = JsonPath.parse(snippet.getContent()).read(jsonPath);
        String textContainingAttribute;
        try {
            textContainingAttribute = (String) data;
        } catch (ClassCastException e) {
            textContainingAttribute = String.valueOf(data);
        }
        return new DataNodeSelector(cssSelector, attribute, textContainingAttribute, path, jsonPath);
    }

}
