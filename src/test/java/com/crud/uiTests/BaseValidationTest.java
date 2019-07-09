package com.crud.uiTests;

import com.crud.base.BaseTest;
import com.crud.objects.Computer;
import com.crud.pages.ComputerPage;
import com.crud.pages.ComputerPage.Field;
import com.crud.pages.ComputersPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BaseValidationTest extends BaseTest {

    protected ComputerPage computerPage;
    protected ComputersPage computersPage;
    protected final boolean PASS = true;
    protected final boolean FAIL = false;

    protected static String incorrectValidation = "Validation is incorrect for %s field with value %s! \nValidation comment: %s";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        computerPage = openPage(ComputerPage.class);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        closeBrowser();
    }

    protected void checkValidationForFieldWithValue(Field FIELD, String value, boolean expectedResult, String comment) {
        boolean validation = computerPage
                .fillField(FIELD, value)
                .clickCreateThisComputerButton()
                .getValidationStatusOf(FIELD);

        assertThat(String.format(incorrectValidation, FIELD, value, comment), validation, is(expectedResult));
    }

    protected ComputersPage createComputer(Computer computer) {
        computersPage = openPage(ComputerPage.class)
                .fillForm(computer)
                .createNewComputer();

        String alert = computersPage.getAlertMessage();

        assertThat("Computer was not created", alert, is("Done! Computer " + computer.getName() + " has been created"));
        return computersPage;
    }
}
