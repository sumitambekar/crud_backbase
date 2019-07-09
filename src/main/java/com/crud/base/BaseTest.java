package com.crud.base;

import com.codeborne.selenide.WebDriverRunner;
import com.crud.helpers.TestLogger;
import org.apache.log4j.Logger;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({TestLogger.class})
public class BaseTest {

    private static final Logger log = Logger.getLogger(BaseTest.class);

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebdriverManager.setupDriver();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        TemporaryFilesystem.getDefaultTmpFS().deleteTemporaryFiles();
        WebDriverRunner.closeWebDriver();
    }

    protected static <P extends BasePage> P openPage(Class<P> clazz, String... params) {
        if (!WebDriverRunner.hasWebDriverStarted()) WebdriverManager.setupDriver();
        String url = PageLinks.getUrl(clazz, params);
        log.info(String.format("Trying to openPage '%s' by URL: %s", clazz.getSimpleName(), url));
        WebDriverRunner.getWebDriver().get(url);
        return PageConstructor.getInstance(clazz);
    }

    public void closeBrowser() {
        WebDriverRunner.closeWebDriver();
        log.info("Browser was closed!");
    }
}
