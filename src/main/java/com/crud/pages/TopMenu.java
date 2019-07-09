package com.crud.pages;

import com.codeborne.selenide.SelenideElement;
import com.crud.base.BasePage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.crud.pages.TopMenu.Elements.linkedText;

public class TopMenu extends BasePage {


    private TopMenu() {
        super(linkedText);
    }

    public static TopMenu topMenu() {
        return new TopMenu();
    }

    public ComputersPage clickLikedText() {
        return clickOn(linkedText, ComputersPage.class);
    }

    interface Elements {
        SelenideElement linkedText = $(By.linkText("Play sample application — Computer database"));
    }
}
