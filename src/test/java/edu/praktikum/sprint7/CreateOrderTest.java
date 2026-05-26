package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import edu.praktikum.sprint7.models.CreateOrderResponse;
import edu.praktikum.sprint7.models.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static edu.praktikum.sprint7.generator.OrderGenerator.randomOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Sprint 7")
@Feature("Создание заказа")
@DisplayName("Тесты создания заказа")
public class CreateOrderTest {

    private CourierApiClient courierApiClient;
    private Response response;
    private Order order;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @Step("Преобразование строки цветов: {colorsString} в список")
    private List<String> parseColors(String colorsString) {
        if (colorsString == null || colorsString.equals("null")) {
            return null;
        }
        if (colorsString.isEmpty()) {
            return Collections.emptyList();
        }
        if (colorsString.contains(",")) {
            return Arrays.asList(colorsString.split(","));
        }
        return Collections.singletonList(colorsString);
    }

    @Step("Генерация случайного заказа с цветами: {colors}")
    private void generateOrderWithColors(List<String> colors) {
        order = randomOrder();
        order.setColor(colors);
    }

    @Step("Генерация случайного заказа")
    private void generateRandomOrder() {
        order = randomOrder();
    }

    @Step("Генерация трех случайных заказов")
    private void generateThreeRandomOrders() {
    }

    @Step("Отправка запроса на создание заказа")
    private void sendCreateOrderRequest() {
        response = courierApiClient.createOrder(order);
    }

    @Step("Отправка запроса на создание первого заказа")
    private void sendFirstOrderRequest() {
        response = courierApiClient.createOrder(order);
    }

    @Step("Проверка, что код ответа равен 201 Created")
    private void verifyStatusCodeIs201(Response response, String message) {
        assertThat(message, response.statusCode(), equalTo(201));
    }

    @Step("Проверка, что трек номер валидный (не 0 и положительный)")
    private void verifyTrackIsValid(Response response) {
        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
        assertThat("Трек номер должен быть положительным числом",
                createResponse.getTrack(), greaterThan(0));
    }

    @Step("Проверка, что ответ содержит поле track")
    private void verifyResponseContainsTrack(Response response) {
        assertThat("Ответ должен содержать поле track",
                response.jsonPath().get("track"), notNullValue());
    }

    @Step("Проверка, что трек номера всех заказов уникальны")
    private void verifyAllTracksAreUnique(int track1, int track2, int track3) {
        assertThat("Трек номера должны быть разными", track1, not(equalTo(track2)));
        assertThat("Трек номера должны быть разными", track1, not(equalTo(track3)));
        assertThat("Трек номера должны быть разными", track2, not(equalTo(track3)));
    }

    @ParameterizedTest(name = "Создание заказа - цвета: {0}")
    @CsvSource({
            "BLACK",
            "GREY",
            "BLACK,GREY",
            "null"
    })
    @DisplayName("Создание заказа с различными вариантами цветов")
    @Description("Проверка, что заказ успешно создается с разными комбинациями цветов")
    public void createOrderWithDifferentColorsTest(String colorsString) {
        List<String> colors = parseColors(colorsString);
        generateOrderWithColors(colors);
        sendCreateOrderRequest();
        verifyStatusCodeIs201(response, "Код ответа должен быть 201 Created");
        verifyTrackIsValid(response);
    }

    @Test
    @DisplayName("Создание заказа - тело ответа содержит track")
    @Description("Проверка, что в ответе присутствует поле track")
    public void createOrderResponseContainsTrackTest() {
        generateRandomOrder();
        sendCreateOrderRequest();
        verifyStatusCodeIs201(response, "Код ответа должен быть 201 Created");
        verifyResponseContainsTrack(response);
        verifyTrackIsValid(response);
    }

    @Test
    @DisplayName("Создание нескольких заказов - трек номера уникальны")
    @Description("Проверка, что при создании нескольких заказов трек номера разные")
    public void createMultipleOrdersTracksAreUniqueTest() {
        // Создание первого заказа
        createOrderAndGetTrack(1);
        int track1 = response.as(CreateOrderResponse.class).getTrack();

        // Создание второго заказа
        createOrderAndGetTrack(2);
        int track2 = response.as(CreateOrderResponse.class).getTrack();

        // Создание третьего заказа
        createOrderAndGetTrack(3);
        int track3 = response.as(CreateOrderResponse.class).getTrack();

        verifyAllTracksAreUnique(track1, track2, track3);
    }

    @Step("Создание заказа №{orderNumber} и получение трек номера")
    private void createOrderAndGetTrack(int orderNumber) {
        generateRandomOrder();
        sendCreateOrderRequest();
        verifyStatusCodeIs201(response, "Заказ №" + orderNumber + " должен создаться успешно");
    }
}