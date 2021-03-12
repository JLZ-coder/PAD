package es.ucm.fdi.calculator;

//Anotations
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// Basic JUnit4 test runner
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * JUnit4 unit tests for the calculator logic.
 * These are local unit tests; no device needed
 */
@RunWith(JUnit4.class) // Specify the test runner
public class CalculatorUnitTest {
/**
        * Test for simple addition.
        * Each test is identified by a @Test annotation.
*/
    @Test
    public void addTwoNumbers() {
        Calculator mCalculator = new Calculator();
        Integer resultAdd = mCalculator.add(2, 1);
        assertEquals(3, (long)resultAdd);
    }
}
