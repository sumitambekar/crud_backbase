package com.crud.helpers;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class TestLogger implements ITestListener {

    private Logger log = Logger.getLogger(TestLogger.class);

    private static String msg = "Test %s: %s %s";

    @Override
    public void onTestStart(ITestResult iTestResult) {
        log.info(String.format(msg, "started", iTestResult.getName(), Arrays.toString(iTestResult.getParameters())));
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        log.info(String.format(msg, "passed", iTestResult.getName(), Arrays.toString(iTestResult.getParameters())));
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        String testName = iTestResult.getName();
        log.info(String.format(msg, "failed", testName, Arrays.toString(iTestResult.getParameters())));
        Optional.ofNullable(iTestResult.getThrowable()).ifPresent(Throwable::printStackTrace);
        ScreenShotHelper.makeScreenShot(testName + "_"+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        log.info(String.format(msg, "skipped", iTestResult.getName(), Arrays.toString(iTestResult.getParameters())));
        Optional.ofNullable(iTestResult.getThrowable()).ifPresent(Throwable::printStackTrace);

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        Optional.ofNullable(iTestResult.getThrowable()).ifPresent(Throwable::printStackTrace);
    }

    @Override
    public void onStart(ITestContext iTestContext) {
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
    }
}
