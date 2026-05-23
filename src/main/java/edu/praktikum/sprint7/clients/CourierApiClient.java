package edu.praktikum.sprint7.clients;

import edu.praktikum.sprint7.models.Courier;
import edu.praktikum.sprint7.models.CourierCreds;
import edu.praktikum.sprint7.models.Order;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class CourierApiClient {

    private static final String API_V1_COURIER = "/api/v1/courier";
    private static final String API_V1_COURIER_LOGIN = "/api/v1/courier/login";
    private static final String API_V1_ORDERS = "/api/v1/orders";

    public CourierApiClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    public Response createCourier(Courier courier) {
        return given()
                .contentType(JSON)
                .and()
                .body(courier)
                .when()
                .post(API_V1_COURIER);
    }

    public Response loginCourier(CourierCreds creds) {
        return given()
                .contentType(JSON)
                .and()
                .body(creds)
                .when()
                .post(API_V1_COURIER_LOGIN);
    }

    public Response loginCourierWithoutLogin(String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", password);

        return given()
                .contentType(JSON)
                .and()
                .body(requestBody)
                .when()
                .post(API_V1_COURIER_LOGIN);
    }

    public Response loginCourierWithoutPassword(String login) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", login);

        return given()
                .contentType(JSON)
                .and()
                .body(requestBody)
                .when()
                .post(API_V1_COURIER_LOGIN);
    }

    public Response deleteCourier(String id) {
        return given()
                .contentType(JSON)
                .when()
                .delete(API_V1_COURIER + "/" + id);
    }
    public Response createOrder(Order order) {
        return given()
                .contentType(JSON)
                .and()
                .body(order)
                .when()
                .post(API_V1_ORDERS);
    }
    public Response getOrders() {
        return given()
                .contentType(JSON)
                .when()
                .get(API_V1_ORDERS);
    }
}