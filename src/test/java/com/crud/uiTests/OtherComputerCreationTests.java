package com.crud.uiTests;

import com.crud.helpers.TestData;
import com.crud.objects.Computer;
import com.crud.objects.MockComputer;
import com.crud.pages.ComputerPage;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class OtherComputerCreationTests extends BaseValidationTest {

    private String random = TestData.random(7);

    private String computerName = "Comp_DependTest_" + random + "_";
    private String jsInjection = "<script>alert(\"" + random + "\")</script>";

    private Computer injectedComputer = new Computer(jsInjection);
    private Computer duplicate = new Computer("duplicate"+ random, "1111-11-11", "2222-12-12", "RCA");

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        computerPage = openPage(ComputerPage.class);
    }

    @AfterClass(alwaysRun = true)
    public void deleteComputers() {
        IntStream.range(1, 6).forEach(i -> MockComputer.deleteComputerByName(computerName + i));
        MockComputer.deleteComputer(injectedComputer);
        MockComputer.deleteComputerByName(duplicate.getName());
        MockComputer.deleteComputerByName(duplicate.getName());
    }

    @Test(priority = 1, dataProvider = "computers")
    public void checkComputerCreationWithPartialData(Computer computer) {
        createComputer(computer);
    }

    @Test(priority = 2)
    public void checkInjectionOnNameField() {
        // if injected - alert is going to brake the test
        createComputer(injectedComputer);
    }

    @Test(priority = 3)
    public void checkDuplicatedComputerCanBeCreated() {
        String computerName = duplicate.getName();

        createComputer(duplicate);

        List<String> computers = createComputer(duplicate)
                .search(duplicate)
                .getComputerNamesList();

        assertThat("First computer is not present in the table!", computers.remove(computerName));
        assertThat("Duplicated computer is not present in the table!", computers, hasItem(computerName));
    }

    @DataProvider
    public Object[][] computers() {
        String introducedDate = "1991-1-11";
        String discontinuedDate = "2020-08-4";
        String company = "Apple Inc.";
        String notSelected = "-- Choose a company --";
        String empty = StringUtils.EMPTY;
        return new Object[][]{
                {new Computer(computerName + "1", notSelected)},
                {new Computer(computerName + "2", introducedDate, discontinuedDate, company)},
                {new Computer(computerName + "3", empty, empty, company)},
                {new Computer(computerName + "4", empty, discontinuedDate, notSelected)},
                {new Computer(computerName + "5", introducedDate, empty, notSelected)},
        };
    }
}
