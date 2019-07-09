package com.crud.pages;

import com.codeborne.selenide.SelenideElement;
import com.crud.base.BasePage;
import com.crud.base.PageConstructor;
import com.crud.helpers.DateHelper;
import com.crud.objects.Computer;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.crud.helpers.DateHelper.convertToDefaultFormat;
import static com.crud.pages.ComputerPage.Elements.CREATE_COMPUTER_BUTTON;
import static com.crud.pages.ComputerPage.Elements.DELETE_COMPUTER_BUTTON;
import static com.crud.pages.ComputerPage.Elements.SAVE_COMPUTER_BUTTON;

public class ComputerPage extends BasePage {

    public ComputerPage() {
        super(Elements.computerForm);
    }

    public ComputerPage fillField(Field field, String value) {
        if (value == null) return this;
        SelenideElement element = field.getElement();
        if (field.equals(Field.COMPANY)) {
            value = value.isEmpty() ? "-- Choose a company --" : value;
            select(element, value);
        } else {
            fill(element, value);
        }
        return this;
    }

    public ComputerPage fillForm(Computer computer) {
        fillField(Field.COMPUTER_NAME, computer.getName());
        fillField(Field.INTRODUCED_DATE, computer.getIntroducedDate());
        fillField(Field.DISCONTINUED_DATE, computer.getDiscontinuedDate());
        fillField(Field.COMPANY, computer.getCompany());
        return this;
    }

    public ComputerPage clickCreateThisComputerButton() {
        clickOn(CREATE_COMPUTER_BUTTON);
        return this;
    }

    public boolean getValidationStatusOf(Field field) {
        return !field.getElement().find(By.xpath("./ancestor::div[contains(@class,'clearfix')]")).shouldBe(visible).has(cssClass("error"));
    }

    public ComputersPage createNewComputer() {
        return clickOn(CREATE_COMPUTER_BUTTON, ComputersPage.class);
    }

    public ComputersPage clickCancelButton(){
        return clickOn(Elements.CANCEL_BUTTON, ComputersPage.class);
    }

    public String getFieldValue(Field field) {
        if (field.equals(Field.COMPANY)) {
            String text = field.getElement().getSelectedText();
            return text.equals("-- Choose a company --") ? "" : text;
        }
        return field.getElement().getAttribute("value");
    }

    public ComputersPage saveComputer(){
        return clickOn(SAVE_COMPUTER_BUTTON, ComputersPage.class);
    }

    public ComputersPage deleteComputer(){
        return clickOn(DELETE_COMPUTER_BUTTON, ComputersPage.class);
    }

    public Computer getFilledValues() {
        String introducedDate = getFieldValue(Field.INTRODUCED_DATE);
        String discontinuedDate = getFieldValue(Field.DISCONTINUED_DATE);
        return new Computer(
                getFieldValue(Field.COMPUTER_NAME),
                convertToDefaultFormat(introducedDate, DateHelper.defaultFormat),
                convertToDefaultFormat(discontinuedDate, DateHelper.defaultFormat),
                getFieldValue(Field.COMPANY)
        );
    }

    public enum Field {
        COMPUTER_NAME("Computer name", "name"),
        INTRODUCED_DATE("Introduced date", "introduced"),
        DISCONTINUED_DATE("Discontinued date", "discontinued"),
        COMPANY("Company", "company");

        private String fieldName;
        private String elementId;

        Field(String fieldName, String elementId) {
            this.fieldName = fieldName;
            this.elementId = elementId;
        }

        public SelenideElement getElement() {
            return $(By.id(elementId));
        }

        public String getName() {
            return fieldName;
        }
    }

    interface Elements {
        SelenideElement computerForm = $("form[action*='/computers']");
        SelenideElement ACTIONS_PANEL = $("div.actions");
        SelenideElement CREATE_COMPUTER_BUTTON = ACTIONS_PANEL.find("input[value='Create this computer']");
        SelenideElement SAVE_COMPUTER_BUTTON = ACTIONS_PANEL.find("input[value='Save this computer']");
        SelenideElement CANCEL_BUTTON = ACTIONS_PANEL.find(By.linkText("Cancel"));
        SelenideElement DELETE_COMPUTER_BUTTON = $("input[value='Delete this computer']");

    }
}
