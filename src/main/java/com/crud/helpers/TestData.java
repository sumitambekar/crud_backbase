package com.crud.helpers;

import org.apache.commons.lang3.RandomStringUtils;

public class TestData {

    public static String random(int length){
        return RandomStringUtils.random(length, true, true);
    }
}
