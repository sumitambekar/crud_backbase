package com.crud.pages;

import com.codeborne.selenide.SelenideElement;
import com.crud.elements.ComputersTable;
import com.crud.objects.Computer;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.crud.base.PageConstructor.getInstance;
import static com.crud.pages.ComputersPage.Elements.*;
import static org.openqa.selenium.By.linkText;

public class ComputersPage extends ComputersTable {


    public ComputersPage() {
        waitUntilDisplayed(BASE_ELEMENT, 5000);
    }

    public ComputersPage search(Computer computer) {
        fillSearchField(computer.getName());
        return clickSearchButton();
    }

    public ComputersPage fillSearchField(String text) {
        fill(SEARCH_FIELD, text);
        return this;
    }

    public ComputersPage clickSearchButton() {
        clickOn(SEARCH_BUTTON);
        return this;
    }

    public ComputersPage clickAddNewComputerButton() {
        clickOn(ADD_COMPUTER_BUTTON);
        return getInstance(ComputersPage.class);
    }

    public String getCurrentPaginationText() {
        return getText(CURRENT_PAGE);
    }

    public ComputersPage clickPeviousPage() {
        clickOn(PREVIOUS_PAGE);
        return getInstance(ComputersPage.class);
    }

    public ComputersPage clickNextPage() {
        clickOn(NEXT_PAGE);
        return getInstance(ComputersPage.class);
    }

    public String getAlertMessage() {
        return getText(ALERT_MESSAGE);
    }

    public boolean isAlertVisible() {
        return isElementDisplayed(ALERT_MESSAGE);
    }

    interface Elements {
        SelenideElement BASE_ELEMENT = $("#actions");

        SelenideElement SEARCH_FIELD = $("#searchbox");
        SelenideElement SEARCH_BUTTON = $("#searchsubmit");
        SelenideElement ADD_COMPUTER_BUTTON = $("#add");

        SelenideElement PAGINATION_FORM = $("#pagination");
        SelenideElement PREVIOUS_PAGE = PAGINATION_FORM.find(linkText("← Previous"));
        SelenideElement CURRENT_PAGE = PAGINATION_FORM.find(".current>a");
        SelenideElement NEXT_PAGE = PAGINATION_FORM.find(linkText("Next →"));

        SelenideElement ALERT_MESSAGE = $(By.className("alert-message"));
    }
}
