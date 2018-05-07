package de.hpi.shoprulesgenerator.api;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.service.ShopRulesGeneratorService;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(secure = false)
@Getter(AccessLevel.PRIVATE)
public class ShopRulesGeneratorControllerTest {

    @Getter(AccessLevel.PRIVATE) private static final long EXAMPLE_SHOP_ID = 1234L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopRulesGeneratorService shopRulesGeneratorService;

    @Test
    public void getRulesHappyPath() throws Exception {
        doReturn(new ShopRules(null, getEXAMPLE_SHOP_ID())).when(getShopRulesGeneratorService()).getRules
                (getEXAMPLE_SHOP_ID());
        getMockMvc()
                .perform(get("/getRules/" + getEXAMPLE_SHOP_ID()))
                .andExpect(jsonPath("data.shopID").value(getEXAMPLE_SHOP_ID()))
                .andExpect(jsonPath("data.selectorMap").isEmpty())
                .andExpect(status().isOk());
    }


    @Test
    public void getRules404() throws Exception {
        doThrow(ShopRulesDoNotExistException.class).when(getShopRulesGeneratorService()).getRules(getEXAMPLE_SHOP_ID());
        getMockMvc()
                .perform(get("/getRules/" + getEXAMPLE_SHOP_ID()))
                .andExpect(status().isNotFound());
    }
}