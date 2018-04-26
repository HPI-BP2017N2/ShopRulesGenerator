package de.hpi.shoprulesgenerator.api;

import de.hpi.shoprulesgenerator.dto.SuccessResponse;
import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.service.IShopRulesGeneratorService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShopRulesGeneratorController {

    private final IShopRulesGeneratorService service;

    @RequestMapping(value = "/getRules/{shopID}", method = GET, produces = "application/json")
    public HttpEntity<Object> getRules(@PathVariable long shopID) throws ShopRulesDoNotExistException {
        return new SuccessResponse<>(getService().getRules(shopID)).withMessage("Shop rules found.").send();
    }
}
