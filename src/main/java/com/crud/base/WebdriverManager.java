package com.crud.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.crud.helpers.EnvPropertiesHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.apache.commons.lang3.StringUtils.EMPTY;


public class WebdriverManager {

    private static final Logger LOGGER = Logger.getLogger(WebdriverManager.class);
    private static EnvPropertiesHandler properties = EnvPropertiesHandler.getInstance();

    private static String browser;
    private static String environment;

    private static boolean useRemoteWebDriver;

    static {
        Configuration.baseUrl = properties.getProperty(EnvPropertiesHandler.BASE_URL);
        Configuration.reportsFolder = properties.getProperty(EnvPropertiesHandler.STORE_SCREEN_TO);
        Configuration.startMaximized = true;
    }

    public static void setupDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) return;
        initializeStaticFields();

        WebDriver driver = initialiseWebDriver();
        adjustDriverConfiguration(driver);
        WebDriverRunner.setWebDriver(driver);
    }

    private static void adjustDriverConfiguration(WebDriver driver) {
        // uncomment to start tests in 2nd monitor if available
        //driver.manage().window().setPosition(new Point(1920, 24));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
    }

    private enum Os {
        WINDOWS32("webdrivers/", "chromedriver_win32", "IEDriverServer", ".exe"),
        WINDOWS64("webdrivers/", "chromedriver_win32", "IEDriverServer", ".exe"),
        LINUX32("webdrivers/", "chromedriver_linux32", null, EMPTY),
        LINUX64("webdrivers/", "chromedriver_linux64", null, EMPTY),
        MACOS32("webdrivers/", "chromedriver_mac64", null, EMPTY);

        private final String chromePath;
        private final String prefix;
        private final String suffix;

        Os(String prefix, String chromePath, String iePath, String suffix) {
            this.prefix = prefix;
            this.chromePath = chromePath;
            this.suffix = suffix;
        }

        public String getChromeDriverPath() {
            return prefix + chromePath + suffix;
        }
    }

    private static Os getOs() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return SystemUtils.OS_ARCH.contains("32") ? Os.WINDOWS32 : Os.WINDOWS64;
        } else if (SystemUtils.IS_OS_LINUX) {
            return SystemUtils.OS_ARCH.contains("32") ? Os.LINUX32 : Os.LINUX64;
        } else if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_MAC) {
            return Os.MACOS32;
        }
        throw new IllegalStateException("Unknown OS: " + SystemUtils.OS_NAME);
    }

    private static void setSystemPropertyForTempDriver(String browserSystemVariable, Os os) {
        if (os.chromePath == null) return;

        String chromeDriverPath = os.getChromeDriverPath();

        //opening inputStream
        InputStream inputStream = Optional
                .ofNullable(getSystemResourceAsStream(chromeDriverPath))
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot locate driver on classpath (missing dependency): " + chromeDriverPath));
        try {
            //copying chromeDriver to temp file
            File temp = File.createTempFile(os.chromePath, os.suffix);
            temp.setExecutable(true);
            FileUtils.copyInputStreamToFile(inputStream, temp);
            //setting path to temp chromeDriver to sys properties
            System.setProperty(browserSystemVariable, temp.getAbsolutePath());

        } catch (IOException e) {
            final String msg = "Error while copying driver executable";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                final String msg = "Error while closing the input stream";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

    }

    private static void initializeStaticFields() {
        environment = Optional.ofNullable(System.getProperty("testEnv")).orElse("default");

        LOGGER.info("Environment is set to: " + environment);

        browser = properties.getProperty(EnvPropertiesHandler.BROWSER);

        Optional<String> remoteProperty = Optional.ofNullable(properties.getProperty(EnvPropertiesHandler.USE_REMOTE_WEBDRIVER));
        useRemoteWebDriver = remoteProperty.isPresent() && Boolean.parseBoolean(remoteProperty.get());
    }

    private static WebDriver initialiseWebDriver() {
        return useRemoteWebDriver ? initialiseRemoteWebDriver() : initialiseLocalWebDriver();
    }

    private static WebDriver initialiseRemoteWebDriver() {
        WebDriver driver;
        DesiredCapabilities desiredCapabilities;

        String remoteWebDriverUrl = properties.getProperty(EnvPropertiesHandler.REMOTE_WEBDRIVER_URL);
        if (remoteWebDriverUrl == null) throw new RuntimeException("Remote webdriver url was not defined!");

        if (browser.equalsIgnoreCase("firefox")) {
            desiredCapabilities = DesiredCapabilities.firefox();
            addProfileToCapability(desiredCapabilities);
        } else if (browser.equalsIgnoreCase("internetexplorer")) {
            desiredCapabilities = DesiredCapabilities.internetExplorer();
            desiredCapabilities.setCapability("ignoreZoomSetting", true);
        } else if (browser.equalsIgnoreCase("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
        } else if (browser.equalsIgnoreCase("opera")) {
            desiredCapabilities = DesiredCapabilities.opera();
        } else if (browser.equalsIgnoreCase("mobileSafari")) {
            desiredCapabilities = new DesiredCapabilities();
        } else if (browser.equalsIgnoreCase("androidWeb")) {
            desiredCapabilities = DesiredCapabilities.android();
        } else {
            desiredCapabilities = new DesiredCapabilities();
        }

        try {
            driver = new RemoteWebDriver(new URL(remoteWebDriverUrl), setCapabilities(desiredCapabilities));
            ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        } catch (MalformedURLException e) {
            final String msg = "Error while initializing remote webdriver with url: " + remoteWebDriverUrl;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        logBrowserSettings(desiredCapabilities);
        return driver;
    }

    private static WebDriver initialiseLocalWebDriver() {
        Os os = getOs();
        setSystemPropertyForTempDriver("webdriver.chrome.driver", os);

        WebDriver driver;
        DesiredCapabilities desiredCapabilities;

        if (browser.equals("firefox")) {
            desiredCapabilities = DesiredCapabilities.firefox();
            addProfileToCapability(desiredCapabilities);
            driver = new FirefoxDriver(setCapabilities(desiredCapabilities));
        } else if (browser.equalsIgnoreCase("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
            desiredCapabilities = setCapabilities(desiredCapabilities);
            driver = new ChromeDriver(desiredCapabilities);
        } else {
            final String msg = "No proper setup or browser settings, check environment.properties";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
        logBrowserSettings(desiredCapabilities);
        return driver;
    }

    private static void setCapability(DesiredCapabilities desiredCapabilities, String capability, String property, Function<String, Object> func) {
        if (properties.getProperty(property) != null) {
            desiredCapabilities.setCapability(capability, func.apply(property));
        }
    }

    private static DesiredCapabilities setCapabilities(DesiredCapabilities dc) {
        setCapability(dc, CapabilityType.ACCEPT_SSL_CERTS, EnvPropertiesHandler.ACCEPT_SSL_CERTS, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.BROWSER_NAME, EnvPropertiesHandler.BROWSER_NAME, prop -> prop);
        setCapability(dc, CapabilityType.ENABLE_PROFILING_CAPABILITY, EnvPropertiesHandler.ENABLE_PROFILING_CAPABILITY, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.HAS_NATIVE_EVENTS, EnvPropertiesHandler.HAS_NATIVE_EVENTS, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.PLATFORM, EnvPropertiesHandler.PLATFORM, Platform::valueOf);
        setCapability(dc, CapabilityType.ROTATABLE, EnvPropertiesHandler.ROTATABLE, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_ALERTS, EnvPropertiesHandler.SUPPORTS_ALERTS, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_APPLICATION_CACHE, EnvPropertiesHandler.SUPPORTS_APPLICATION_CACHE, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_FINDING_BY_CSS, EnvPropertiesHandler.SUPPORTS_FINDING_BY_CSS, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_LOCATION_CONTEXT, EnvPropertiesHandler.SUPPORTS_LOCATION_CONTEXT, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_SQL_DATABASE, EnvPropertiesHandler.SUPPORTS_SQL_DATABASE, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.SUPPORTS_WEB_STORAGE, EnvPropertiesHandler.SUPPORTS_WEB_STORAGE, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.TAKES_SCREENSHOT, EnvPropertiesHandler.TAKES_SCREENSHOT, Boolean::parseBoolean);
        setCapability(dc, CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, EnvPropertiesHandler.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour::fromString);
        setCapability(dc, CapabilityType.VERSION, EnvPropertiesHandler.VERSION, prop -> prop);
        setCapability(dc, "ignoreZoomSetting", EnvPropertiesHandler.IGNORE_ZOOM_SETTING, prop -> prop);
        setCapability(dc, "ignoreProtectedModeSettings", EnvPropertiesHandler.IGNORE_PROTECTED_MODE_SETTINGS, prop -> prop);

        if (properties.getProperty(EnvPropertiesHandler.PROXY_TYPE) != null
                && properties.getProperty(EnvPropertiesHandler.PROXY_ADDRESS) != null) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.valueOf(properties.getProperty(EnvPropertiesHandler.PROXY_TYPE)));
            String proxyAddress = properties
                    .getProperty(EnvPropertiesHandler.PROXY_ADDRESS);
            proxy.setHttpProxy(proxyAddress);
            dc.setCapability(CapabilityType.PROXY, proxy);
        }
        return dc;
    }

    private static void addProfileToCapability(DesiredCapabilities desiredCapabilities) {
        FirefoxProfile profile = new FirefoxProfile();
        final boolean allowAuth = StringUtils.equals(
                properties.getProperty(EnvPropertiesHandler.ALLOW_BROWSER_AUTHENTICATION),
                "true");
        final boolean automaticallySave = properties.getProperty(EnvPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK) != null;
        if (allowAuth) {
            String trustedDomains = StringUtils.defaultString(properties.getProperty(EnvPropertiesHandler.LIST_OF_TRUSTED_DOMAINS_FOR_BROWSER_AUTHENTICATION));
            profile.setPreference("network.http.phishy-userpass-length", 255);
            profile.setPreference("network.automatic-ntlm-auth.trusted-uris", trustedDomains);
        } else if (automaticallySave) {
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.dir", properties.getProperty(EnvPropertiesHandler.DOWNLOAD_FILE_TO));
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", properties.getProperty(EnvPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK));
        }
        if (allowAuth || automaticallySave) {
            LOGGER.info("Adding profile to " + desiredCapabilities.getBrowserName());
            desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
        }
    }

    private static void logBrowserSettings(DesiredCapabilities capability) {
        String remote = useRemoteWebDriver ? "remote " : "";
        String msg = String.format("Initializing %swebdriver with %s, %s, %s.",
                remote, capability.getBrowserName(), capability.getCapability(browser), capability.getPlatform());
        LOGGER.info(msg);
    }
}
