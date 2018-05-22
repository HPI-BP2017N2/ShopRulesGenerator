package de.hpi.shoprulesgenerator.service;

import com.jayway.jsonpath.JsonPath;
import de.hpi.shoprulesgenerator.exception.BlockNotFoundException;
import de.hpi.shoprulesgenerator.exception.CouldNotDetermineJsonPathException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

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
                .map(occurrence -> {
                    List<Selector> selectors = new LinkedList<>();
                    try {
                        buildDataNodeSelectorDFS(
                                buildCssSelectorForOccurrence(occurrence),
                                new Script(occurrence.html()),
                                new Path(),
                                attribute,
                                selectors);
                    } catch (BlockNotFoundException e) { log.error("Could not generate selectors!", e); }
                    return selectors;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void buildDataNodeSelectorDFS(String cssSelector, Script script, Path path, String attribute,
                                          List<Selector> selectors) {
        if (script.isJSONLeaf() && script.containsAttribute(attribute)) {
            try {
                selectors.add(buildSelector(cssSelector, script.toValidJson(), path, attribute));
            } catch (IOException | CouldNotDetermineJsonPathException e) {
                log.error("Failed to create DataNodeSelector! Script: " + script + " Path: " + path + " Attribute: " +
                        attribute, e);
            }
        } else {
            script = removeOuterBrackets(script);
            try {
                while (hasBlockContainingAttribute(script, attribute)) {
                    Script block = script.getFirstBlock();
                    buildDataNodeSelectorDFS(cssSelector, block, path.cloneAndAddPathID(), attribute, selectors);
                    path.getLast().increment();
                    script = removeBlockFromScript(script, block);
                }
            } catch(BlockNotFoundException e) { log.error("Invalid JSON - skipping script tag."); }

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
