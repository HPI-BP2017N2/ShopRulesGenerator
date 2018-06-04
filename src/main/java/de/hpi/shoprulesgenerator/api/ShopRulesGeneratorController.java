package de.hpi.shoprulesgenerator.api;

import de.hpi.shoprulesgenerator.dto.SuccessResponse;
import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.service.IShopRulesGeneratorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorController {

    private final IShopRulesGeneratorService service;

    @ApiOperation(value = "Get rules for specific shop", response = ShopRules.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved shop rules"),
            @ApiResponse(code = 404, message = "The rules are not existing yet. Try again later.")})

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @RequestMapping(value = "/getRules/{shopID}", method = GET, produces = "application/json")
    public HttpEntity<Object> getRules(@PathVariable long shopID,
                                       @RequestParam(required = false) Optional<Boolean> forceUpdate)
            throws ShopRulesDoNotExistException {
        return new SuccessResponse<>(getService().getRules(shopID, forceUpdate.orElse(false)))
                .withMessage("Shop rules found.")
                .send();
    }
}
