package com.crud.base;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.crud.elements.StatusWebElem;
import com.crud.helpers.WaitHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.crud.elements.StatusWebElem.*;

public abstract class BasePage {

    protected static Logger log = Logger.getLogger(BasePage.class);

    protected BasePage(SelenideElement element) {
        waitUntilDisplayed(element, 5000);
    }

    protected void waitUntilDisplayed(By locator, int millis) {
        if (!isElementDisplayed(locator)) {
            $(locator).waitUntil(visible, millis);
        }
    }

    protected void waitUntilDisplayed(SelenideElement element, int millis) {
        if (!isElementDisplayed(element)) {
            element.waitUntil(visible, millis);
        }
    }

    protected boolean isElementDisplayed(By by) {
        return isElementDisplayed($(by));
    }

    protected boolean isElementDisplayed(SelenideElement selenideElement) {
        try {
            return selenideElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isTextDisplayed(String textToBeChecked) {
        try {
            return $(By.xpath("//*[contains(text(), '" + textToBeChecked + "')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    protected boolean isElementDisplayedNow(SelenideElement selenideElement) {
        WebDriver driver = WebDriverRunner.getWebDriver();
        WaitHelper.setImplicitWait(driver, 0.5);
        try {
            return $(selenideElement).isDisplayed();
        } catch (Exception e) {
            return false;
        } finally {
            WaitHelper.setImplicitWaitDefault(driver);
        }
    }

    public WebElement findTextContains(String textToBeFound) {
        try {
            return $(By.xpath("//*[text() = '" + textToBeFound + "']"));
        } catch (Exception e) {
            return null;
        }
    }


    protected static void checkElementStatus(SelenideElement selenideElement, StatusWebElem expectedStatus) {
        if (expectedStatus.equals(VISIBLE)) {
            $(selenideElement).shouldBe(visible);
        } else if (expectedStatus.equals(ENABLED)) {
            $(selenideElement).shouldBe(visible, enabled);
        } else if (expectedStatus.equals(DISABLED)) {
            $(selenideElement).shouldBe(visible, disabled);
        } else if (expectedStatus.equals(NOT_VISIBLE)) {
            isElementsNotVisibleNow($(selenideElement));
        } else {
            Assert.assertTrue(false, "Incorrect expected status. Possible values: ENABLED / DISABLED / NOT_AVAILABLE");
        }
    }

    public static void isElementsNotVisibleNow(SelenideElement... selenideElements) {
        WebDriver driver = WebDriverRunner.getWebDriver();
        WaitHelper.setImplicitWait(driver, 0.5);
        for (SelenideElement elem : selenideElements) {
            $(elem).shouldNotBe(visible);
        }
        WaitHelper.setImplicitWaitDefault(driver);
    }

    /**
     * @param clazz - Class <P> of page that should be returned
     * @return new page instance of P class
     */
    protected <P extends BasePage> P clickOn(By locator, Class<P> clazz) {
        return clickOn($(locator), clazz);
    }

    protected <P extends BasePage> P clickOn(SelenideElement element, Class<P> clazz) {
        element.shouldBe(visible).click();
        return PageConstructor.getInstance(clazz);
    }

    protected SelenideElement fill(SelenideElement element, String text) {
        return element.shouldBe(visible).setValue(text);
    }

    protected String getText(SelenideElement element) {
        return element.shouldBe(visible).getText();
    }

    protected SelenideElement select(SelenideElement element, String text) {
        element.shouldBe(visible).selectOption(text);
        return element;
    }

    protected SelenideElement clickOn(SelenideElement element) {
        element.shouldBe(visible).click();
        return element;
    }
}
