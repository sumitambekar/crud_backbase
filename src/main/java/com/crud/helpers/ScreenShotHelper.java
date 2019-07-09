package com.crud.helpers;

import static com.codeborne.selenide.Selenide.screenshot;

public class ScreenShotHelper {

    public static void makeScreenShot(String fileName) {
        screenshot(fileName);
    }
}
