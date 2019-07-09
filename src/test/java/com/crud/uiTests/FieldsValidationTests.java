package com.crud.uiTests;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.SourceTree;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.crud.pages.ComputerPage.Field.*;

public class FieldsValidationTests extends BaseValidationTest {

    @Test(dataProvider = "dataProviderForDateValidation")
    public void checkValidationByIntroducedDateTest(String value, boolean expectedResult, String comment) {
        checkValidationForFieldWithValue(INTRODUCED_DATE, value, expectedResult, comment);
    }

    @Test(dataProvider = "dataProviderForDateValidation")
    public void checkValidationByDiscontinuedDateTest(String value, boolean expectedResult, String comment) {
        checkValidationForFieldWithValue(DISCONTINUED_DATE, value, expectedResult, comment);
    }

    @Test(dataProvider = "dataProviderForCompanyValidation")
    public void checkValidationByCompanyTest(String value, boolean expectedResult, String comment) {
        checkValidationForFieldWithValue(COMPANY, value, expectedResult, comment);
    }

    @Test(dataProvider = "dataProviderForNameValidation", groups = "nameValidation")
    public void checkValidationByNameTest(String value, boolean expectedResult, String comment) {
        checkValidationForFieldWithValue(COMPUTER_NAME, value, expectedResult, comment);
    }

    @BeforeGroups("nameValidation")
    public void makeFormInvalidNotToSubmitNewObjectsIfValidationPassed(){
        computerPage.fillField(INTRODUCED_DATE, "invalid text");
    }

    @DataProvider
    public Object[][] dataProviderForDateValidation() {
        return new Object[][]{
                //Edges
                {StringUtils.EMPTY, PASS, "Empty"},
                {"292278993-12-31", PASS, "Maximum possible"},
                {"292278994-1-1", FAIL, "Year is over max possible value (system restrictions)"},
                {"0-1-1", PASS, "Minimum possible"},
                {"1000-9-9", PASS, "Zero is not necessary before 1 digit day/month"},
                {"9999-09-09", PASS, "Zero is not restricted before 1 digit day/month"},
                {"123456789-08-26", PASS, "All digits used in date field"},
                {"2017-0-1", FAIL, "Month can't be 0 value"},
                {"2017-1-0", FAIL, "Day can't be 0 value"},
                {"2017-13-1", FAIL, "Month can't be more than 12"},
                {"2017-1-32", FAIL, "Day can't be more than 31 for January"},
                {"2017-11-31", FAIL, "Day can't be more than 30 for November"},
                {"2017-02-29", FAIL, "Day can't be over max in leap year for February"},
                {"2016-02-29", PASS, "2016 is a leap year, so 29 of Feb exist"},

                //invalids
                {StringUtils.SPACE, FAIL, "Space is invalid input"},
                {"text", FAIL, "Invalid input - not a date"},
                {"2017-JAN-1", FAIL, "Month has incorrect formatting. Expecting 01-12"},
                {"2017-Jan-1", FAIL, "Month has incorrect formatting. Expecting 01-12"},
                {"2017 1 1", FAIL, "Incorrect separator, expected hyphen - "},
                {"2017,1,1", FAIL, "Incorrect separator, expected hyphen - "},
                {"2017.1.1", FAIL, "Incorrect separator, expected hyphen - "},
                {"%", FAIL, "Special symbol is not a valid input"},
                {"000000", FAIL, "Zeroes is not a valid input"},
                {"---", FAIL, "Input without values is invalid"},
                {"yyyy-MM-dd", FAIL, "Date format is invalid input"},
                {"1-1", FAIL, "Not enough values"},
                {"1-1-1-1", FAIL, "Too many values"},
                {"ійїЙЯЧєї", FAIL, "Cyrillic text is invalid input for date"},
        };
    }

    public static void main(String[] args) {
        System.out.println(RandomStringUtils.random(200, true , true));
    }

    @DataProvider
    public Object[][] dataProviderForCompanyValidation() {
        return new Object[][]{
                {"-- Choose a company --", PASS, ""},
                {"Apple Inc.", PASS, ""},
                {"Samsung Electronics", PASS, ""},
        };
    }

    @DataProvider
    public Object[][] dataProviderForNameValidation() {
        String longString = RandomStringUtils.random(200, true, true);
        return new Object[][]{
                {StringUtils.EMPTY, FAIL, "Empty name is invalid value"},
                {StringUtils.SPACE, FAIL, "Space is not a valid input"},
                {"      ", FAIL, "Multiple spaces is not a valid input"},
                {"Some random string", PASS, "Any text of correct size is a valid input"},
                {longString, FAIL, "Validation for max length missing, current input length: " + longString.length()},
                {"ійїЙЯЧєї", PASS, "Cyrillic text is a valid input for name"},
                {"!@#$%^&*()|\"}{?>|\n~\t?><123567890", PASS, "Special symbols and letters are valid input"},
        };
    }
}
