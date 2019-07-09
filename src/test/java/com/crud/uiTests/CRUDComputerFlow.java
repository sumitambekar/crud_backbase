package com.crud.uiTests;

import com.crud.objects.Computer;
import com.crud.objects.MockComputer;
import com.crud.pages.ComputerPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.crud.helpers.TestData.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CRUDComputerFlow extends BaseValidationTest {

    private String random = random(7);
    private String computerId;

    private Computer computer = new Computer("Computer_" + random ,"2000-12-31" , "1999-1-1", "RCA");
    private Computer updated = new Computer("updated_" + random, "1819-1-11", "2000-01-01", "E.S.R. Inc.");

    @AfterClass(alwaysRun = true)
    public void deleteComputers() {
        MockComputer.deleteComputer(updated);
    }

    @Test(priority = 1)
    public void checkComputerCreation() {
        createComputer(computer);
    }

    @Test(priority = 2)
    public void checkDataConsistencyInTable() {
        Computer found = computersPage
                .search(computer)
                .getComputerDataByName(computer.getName());

        assertThat("Data in search is not same, as in setup!", found, is(computer.normalized()));
    }

    @Test(priority = 3)
    public void checkDataConsistencyOnComputerPage() {
        computerPage = computersPage.editComputer(computer);

        Computer filled = computerPage
                .getFilledValues();

        assertThat("Data in search is not same, as in setup!", filled, is(computer));
    }

    @Test(priority = 4)
    public void checkCancelButtonDoesNotSaveData() {
        //test data
        computerId = computer.getId();

        //test steps
        computerPage
                .fillForm(updated)
                .clickCancelButton();

        Computer filled = openPage(ComputerPage.class, computerId)
                .getFilledValues();

        assertThat("Data was not changed after clicking Cancel!", filled, is(computer));
    }

    @Test(priority = 5)
    public void checkComputerUpdate() {
        String expectedAlert = String.format("Done! Computer %s has been updated", updated.getName());
        updated.setId(computerId);

        //test steps
        String alert = openPage(ComputerPage.class, computerId)
                .fillForm(updated)
                .saveComputer()
                .getAlertMessage();

        assertThat("Computer was not updated!", alert, is(expectedAlert));
    }

    @Test(priority = 6)
    public void checkDataConsistencyOnComputerUpdate() {
        Computer filled = computersPage
                .fillSearchField(updated.getName())
                .clickSearchButton()
                .editComputer(updated)
                .getFilledValues();

        assertThat("Data was not updated correctly!", filled, is(updated.normalized()));
    }

    @Test(priority = 7)
    public void checkComputerDeleting() {
        String alert = openPage(ComputerPage.class, updated.getId())
                .deleteComputer()
                .getAlertMessage();

        assertThat("Data was not deleted!", alert, is("Done! Computer has been deleted"));

        List<String> searchResult = computersPage
                .search(updated)
                .getComputerNamesList();

        assertThat("Computer is present in search after removal!", searchResult, empty());
    }
}

