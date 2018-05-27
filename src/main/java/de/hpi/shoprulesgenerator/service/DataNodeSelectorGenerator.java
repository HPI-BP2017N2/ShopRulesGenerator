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

import static de.hpi.shoprulesgenerator.service.JsonPathBuilder.getJsonPath;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class DataNodeSelectorGenerator extends TextNodeSelectorGenerator {

    @Override
    public List<Selector> buildSelectors(Document html, String attribute) {
        return html.select("script")
                .stream()
                .filter(occurrence -> occurrence.html().toLowerCase().contains(attribute))
                .map(occurrence -> buildDataNodeSelectorDFS(occurrence, attribute))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Selector> buildDataNodeSelectorDFS(Element occurrence, String attribute) {
        List<Selector> selectors = new LinkedList<>();
        scriptBlockDFS(
                buildCssSelectorForOccurrence(occurrence),
                new Script(occurrence.data()),
                new Path(),
                attribute,
                selectors);
        return selectors;
    }


    private void scriptBlockDFS(String cssSelector, Script script, Path path, String attribute, List<Selector> selectors) {
        if (script.isJSONLeaf() && script.containsAttribute(attribute)) {
            addDataNodeSelector(selectors, cssSelector, script, path, attribute);
        } else {
            script = removeOuterBrackets(script);
            goOneLevelDeeperInBlockTree(script, attribute, selectors, path, cssSelector);
        }
    }

    private void goOneLevelDeeperInBlockTree(Script script, String attribute, List<Selector> selectors, Path path, String cssSelector) {
        try {
            while (hasBlockContainingAttribute(script, attribute)) {
                Script block = script.getFirstBlock();
                scriptBlockDFS(cssSelector, block, path.cloneAndAddPathID(), attribute, selectors);
                path.getLast().increment();
                script = removeBlockFromScript(script, block);
            }
        } catch (BlockNotFoundException e) { log.warn("Invalid Javascript - skipping script: " + script.getContent()); }
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
        String jsonPath = getJsonPath(snippet, attribute);
        String textContainingAttribute = JsonPath.parse(snippet.getContent()).read(jsonPath);
        return new DataNodeSelector(cssSelector, attribute, textContainingAttribute, path, jsonPath);
    }

}
