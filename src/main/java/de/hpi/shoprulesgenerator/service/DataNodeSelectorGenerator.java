package de.hpi.shoprulesgenerator.service;

import com.jayway.jsonpath.JsonPath;
import de.hpi.shoprulesgenerator.exception.CouldNotDetermineJsonPathException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

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
                    buildDataNodeSelectorDFS(
                            buildCssSelectorForOccurrence(occurrence),
                            new Script(occurrence.html()),
                            new Path(),
                            attribute,
                            selectors);
                        return selectors;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void buildDataNodeSelectorDFS(String cssSelector, Script script, Path path, String attribute,
                                          List<Selector> selectors) {
        if (script.isJSONLeaf()) {
            try {
                selectors.add(buildSelector(cssSelector, script, path, attribute));
            } catch (CouldNotDetermineJsonPathException e) {
                log.error("Failed to create DataNodeSelector! " + script + " " + path + " " + attribute);
            }
        } else {
            while (hasBlockContainingAttribute(script, attribute)) {
                Script block = script.getFirstBlock();
                path.getLast().increment();
                buildDataNodeSelectorDFS(cssSelector, block, path.cloneAndAddPathID(), attribute, selectors);
                script = removeBlockFromScript(script, block);
            }
        }
    }

    private Script removeBlockFromScript(Script script, Script block) {
        return new Script(script.getContent().substring(
                script.getContent().indexOf(block.getContent()) + block.getContent().length()));
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
