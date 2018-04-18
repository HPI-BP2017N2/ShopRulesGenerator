package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class ShopRulesGeneratorServiceTest {

    @Getter(AccessLevel.PRIVATE) private static final long EXAMPLE_SHOP_ID = 1234L;

    @Mock
    private IShopRulesRepository shopRulesRepository;

    @Mock
    private IdealoBridge idealoBridge;

    @InjectMocks
    private ShopRulesGeneratorService shopRulesGeneratorService;

    @Test(expected = ShopRulesDoNotExistException.class)
    public void getUnExistingRules() throws ShopRulesDoNotExistException {
        doReturn(new IdealoOffers()).when(getIdealoBridge()).getSampleOffers(anyLong());
        doAnswer(returnsFirstArg()).when(getShopRulesRepository()).save(any());
        getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID());
    }

    @Test
    public void getExistingRules() throws ShopRulesDoNotExistException {
        doReturn(new ShopRules(null, getEXAMPLE_SHOP_ID())).when(getShopRulesRepository()).findByShopID
                (getEXAMPLE_SHOP_ID());
        getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID());
    }
}