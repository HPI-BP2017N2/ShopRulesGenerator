package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import de.hpi.shoprulesgenerator.persistence.ShopRules;
import de.hpi.shoprulesgenerator.persistence.repository.IShopRulesRepository;
import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.given;
import static org.junit.Assert.assertEquals;
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

    private IdealoOffers sampleOffers;

    @Mock
    private IShopRulesRepository shopRulesRepository;

    @Spy
    private ShopRulesGeneratorConfig config;

    @InjectMocks
    private ShopRulesGenerator shopRulesGenerator;

    private ShopRulesGeneratorService shopRulesGeneratorService;

    @Before
    public void setup() throws IOException {
        setShopRulesGeneratorService(new ShopRulesGeneratorService(getShopRulesRepository(), getShopRulesGenerator()));
        loadSampleOffers();
    }

    private void loadSampleOffers() throws IOException {
        setSampleOffers(new ObjectMapper().readValue(getClass().getClassLoader().getResource
                ("scoring-samples/scoringOffers.json"), IdealoOffers.class));
        getSampleOffers().forEach(this::loadHTMLFile);
    }

    private void loadHTMLFile(IdealoOffer offer) {
        try {
            offer.setFetchedPage(Jsoup.parse(
                    getClass().getClassLoader().getResourceAsStream("scoring-samples/" + offer.get(OfferAttribute.URL)
                            .get(0)),
                    "UTF-8",
                    "https://www.sample.de"));
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
        doReturn(getSampleOffers()).when(getIdealoBridge()).getSampleOffers(getEXAMPLE_SHOP_ID());
        doNothing().when(getFetcher()).fetchHTMLPages(getSampleOffers(), getEXAMPLE_SHOP_ID());
        doAnswer(invocationOnMock -> {
            ShopRules rules = invocationOnMock.getArgument(0);
            doReturn(rules).when(getShopRulesRepository()).findByShopID(getEXAMPLE_SHOP_ID());
            selectorsScoringCorrect(rules);
            return rules;
        }).when(getShopRulesRepository()).save(any());
        given().ignoreException(ShopRulesDoNotExistException.class)
                .await().atMost(30, SECONDS)
                .until(() -> getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID()) != null);
    }

    private void selectorsScoringCorrect(ShopRules rules) {
        assertEquals(1,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector -> selector.getScore() == 5)
                        .count());
        assertEquals(1,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector -> selector.getScore() == 4)
                        .count());
        assertEquals(2,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector -> selector.getScore() == 1).count());
        assertEquals(1,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector -> selector.getScore() == -1).count());
        assertEquals(1,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector -> selector.getScore() == -3).count());
    }

    @Test
    public void changedNormalizedScoreCalculation() {
        doReturn(getSampleOffers()).when(getIdealoBridge()).getSampleOffers(getEXAMPLE_SHOP_ID());
        doNothing().when(getFetcher()).fetchHTMLPages(getSampleOffers(), getEXAMPLE_SHOP_ID());
        doAnswer(invocationOnMock -> {
            ShopRules rules = invocationOnMock.getArgument(0);
            doReturn(rules).when(getShopRulesRepository()).findByShopID(getEXAMPLE_SHOP_ID());
            selectorsNormalizedScoringCorrect(rules);
            return rules;
        }).when(getShopRulesRepository()).save(any());
        given().ignoreException(ShopRulesDoNotExistException.class)
                .await().atMost(30, SECONDS)
                .until(() -> getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID()) != null);
    }

    private void selectorsNormalizedScoringCorrect(ShopRules rules) {
        assertEquals(1,
                rules.getSelectorMap().get(OfferAttribute.EAN).stream().filter(selector ->
                        selector.getNormalizedScore() == 1).count());
    }

    @Test
    public void priceSpecificRules() {
        doReturn(getSampleOffers()).when(getIdealoBridge()).getSampleOffers(getEXAMPLE_SHOP_ID());
        doNothing().when(getFetcher()).fetchHTMLPages(getSampleOffers(), getEXAMPLE_SHOP_ID());
        doAnswer(invocationOnMock -> {
            ShopRules rules = invocationOnMock.getArgument(0);
            doReturn(rules).when(getShopRulesRepository()).findByShopID(getEXAMPLE_SHOP_ID());
            generateRulesForPrice(rules);
            return rules;
        }).when(getShopRulesRepository()).save(any());
        given().ignoreException(ShopRulesDoNotExistException.class)
                .await().atMost(30, SECONDS)
                .until(() -> getShopRulesGeneratorService().getRules(getEXAMPLE_SHOP_ID()) != null);
    }

    private void generateRulesForPrice(ShopRules rules) {
        assertEquals(1, rules.getSelectorMap().get(OfferAttribute.PRICE).size());
    }

}