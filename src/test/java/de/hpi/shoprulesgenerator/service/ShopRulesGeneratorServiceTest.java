package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class ShopRulesGeneratorServiceTest {

    @Getter(AccessLevel.PRIVATE) private static final long EXAMPLE_SHOP_ID = 1234L;

    @Mock
    private HTMLPageFetcher fetcher;

    @Mock
    private IdealoBridge idealoBridge;

    private IdealoOffers saturnOffers;

    @Mock
    private IShopRulesRepository shopRulesRepository;

    @InjectMocks
    private ShopRulesGeneratorService shopRulesGeneratorService;

    @Before
    public void setup() throws IOException {
        loadSaturnOffers();
    }

    private void loadSaturnOffers() throws IOException {
        setSaturnOffers(new ObjectMapper().readValue(getClass().getClassLoader().getResource
                ("saturn-sample/sampleOffers.json"), IdealoOffers.class));
        getSaturnOffers().forEach(this::loadHTMLFile);
    }

    private void loadHTMLFile(IdealoOffer offer) {
        try {
            offer.setFetchedPage(Jsoup.parse(
                    getClass().getClassLoader().getResourceAsStream("saturn-sample/" + offer.get(OfferAttribute.URL)),
                    "UTF-8",
                    "https://www.saturn.de"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ShopRulesDoNotExistException.class)
    public void getUnExistingRules(){
        doReturn(new IdealoOffers()).when(getIdealoBridge()).getSampleOffers(anyLong());
        doNothing().when(getFetcher()).fetchHTMLPages(any(), anyLong());
        doAnswer(returnsFirstArg()).when(getShopRulesRepository()).save(any());
        await().until(() -> getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID()) == null);
    }

    @Test
    public void getExistingRules() throws ShopRulesDoNotExistException {
        doReturn(new ShopRules(null, getEXAMPLE_SHOP_ID())).when(getShopRulesRepository()).findByShopID
                (getEXAMPLE_SHOP_ID());
        getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID());
    }

    @Test
    public void selectorsScoring() {

    }
}