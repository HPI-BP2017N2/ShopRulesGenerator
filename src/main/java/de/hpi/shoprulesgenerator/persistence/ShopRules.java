package de.hpi.shoprulesgenerator.persistence;

import de.hpi.shoprulesgenerator.service.SelectorMap;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@RequiredArgsConstructor
@ToString
public class ShopRules {

    @ApiModelProperty(notes = "Map containing shop specific rules", required = true)
    private final SelectorMap selectorMap;

    @ApiModelProperty(notes = "Unique shop identifier used by idealo", required = true)
    @Id
    private final long shopID;
}
