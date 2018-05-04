package de.hpi.shoprulesgenerator.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelectorTest {

    @Test
    public void testCutIndicesCalculation() {
        String attr = "123";
        String textContainingAttr = "EAN: 123";
        Selector testSelector = new TextNodeSelector(null, attr,textContainingAttr);
        assertEquals(5, testSelector.getLeftCutIndex());
        assertEquals(0, testSelector.getRightCutIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void attemptToCreateSelectorWithInvalidParameter() {
        new TextNodeSelector(null, "1234", "textWithoutOneTwoThreeFour");
    }

}