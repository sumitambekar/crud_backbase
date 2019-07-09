package com.crud.elements;

import com.codeborne.selenide.SelenideElement;
import com.crud.base.BasePage;
import com.crud.helpers.DateHelper;
import com.crud.objects.Computer;
import com.crud.pages.ComputerPage;
import org.openqa.selenium.By;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.crud.elements.ComputersTable.Elements.*;

public class ComputersTable extends BasePage {

    public ComputersTable() {
        super(BASE_LOCATOR);
    }

    public Computer getComputerDataByName(String computerName) {
        SelenideElement row = getRowByName(computerName);
        String name = getColumnValueForRow(row, Column.COMPUTER_NAME);
        String introduced = getColumnValueForRow(row, Column.INTRODUCED);
        String discontinued = getColumnValueForRow(row, Column.DISCONTINUED);
        String company = getColumnValueForRow(row, Column.COMPANY);

        introduced = DateHelper.convertToDefaultFormat(introduced, DateHelper.tableFormat);
        discontinued = DateHelper.convertToDefaultFormat(discontinued, DateHelper.tableFormat);

        return new Computer(name, introduced, discontinued, company);
    }

    public ComputerPage editComputer(Computer computer) {
        return clickOn(By.linkText(computer.getName()), ComputerPage.class);
    }

    public List<String> getComputerNamesList() {
        return NOTHING_TO_DISPLAY_ELEM.isDisplayed()
                ? Collections.emptyList()
                : TABLE.findAll("tbody>tr")
                .stream()
                .map(row -> getColumnValueForRow(row, Column.COMPUTER_NAME))
                .collect(Collectors.toList());
    }

    public SelenideElement getRowByName(String name) {
        return $(By.linkText(name)).find(By.xpath("./ancestor::tr[1]"));
    }

    public String getColumnValueForRow(SelenideElement row, Column column) {
        String text = getText(row.find(By.xpath("./td[" + column.position + "]")));
        return text.equals("-") ? "" : text;
    }

    interface Elements {
        // base locator searches for table element or element that is shown when table is empty
        String tableCss = "table.computers";
        String nothingElemCss = ".well>em";
        SelenideElement BASE_LOCATOR = $(tableCss + "," + nothingElemCss);

        SelenideElement TABLE = $(tableCss);
        SelenideElement NOTHING_TO_DISPLAY_ELEM = $(nothingElemCss);
    }

    enum Column {
        COMPUTER_NAME("Computer name", 1),
        INTRODUCED("Introduced", 2),
        DISCONTINUED("Discontinued", 3),
        COMPANY("Company", 4);

        private String name;
        private int position;

        Column(String name, int position) {
            this.name = name;
            this.position = position;
        }
    }
}
