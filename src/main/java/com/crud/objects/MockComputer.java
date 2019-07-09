package com.crud.objects;

import com.crud.base.BaseREST;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;

public class MockComputer extends BaseREST {

    private static Logger log = Logger.getLogger(MockComputer.class);

    public static void deleteComputer(Computer computer) {
        String id = computer.getId();
        log.info("REST: Deleting " + computer);
        deleteById(id);
    }

    public static void deleteComputerByName(String name) {
        String id = getComputerIdByName(name);
        log.info(String.format("REST: deleting computer by name:%s, id:%s", name, id));
        deleteById(id);
    }

    public static String getComputerIdByName(String computerName) {
        Response response = get("/computers?f=" + computerName);
        //gets href of 'a' element with text {computerName}
        String href;
        try {
            href = response.htmlPath().get("**.find{it.text() == '" + computerName + "'}.a.@href");
        } catch (IllegalArgumentException e) {
            String error = "REST: failed to get computer id by name! Error message: " + e.getMessage();
            log.info(error);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
        String id = StringUtils.substringAfterLast(href, "/");
        log.info(String.format("REST: getting id for computer. Result: name:%s, id:%s", computerName, id));
        return id;
    }

    public static void deleteById(String id) {
        String delete = "/computers/" + id + "/delete";
        post(delete).then().assertThat().statusCode(200);
        log.info("REST: computer with id " + id + " was deleted!");
    }

    public static String getResponseContent(Response response) {
        return ((RestAssuredResponseImpl) response).getContent().toString();
    }
}
