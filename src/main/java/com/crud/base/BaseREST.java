package com.crud.base;

import io.restassured.RestAssured;

public abstract class BaseREST {

    static {
        RestAssured.baseURI = "http://computer-database.herokuapp.com";
    }

}
