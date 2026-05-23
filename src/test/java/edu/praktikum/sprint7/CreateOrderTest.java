package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import edu.praktikum.sprint7.models.CreateOrderResponse;
import edu.praktikum.sprint7.models.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static edu.praktikum.sprint7.generator.OrderGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Создание заказа")
public class CreateOrderTest {

    private CourierApiClient courierApiClient;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @Test
    @DisplayName("Создание заказа - с цветом BLACK")
    @Description("Проверка, что можно создать заказ с цветом BLACK")
    public void createOrderWithBlackColorTest() {
        Order order = orderWithBlackColor();

        Response response = courierApiClient.createOrder(order);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
        assertThat("Трек номер должен быть положительным числом",
                createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа - с цветом GREY")
    @Description("Проверка, что можно создать заказ с цветом GREY")
    public void createOrderWithGreyColorTest() {
        Order order = orderWithGreyColor();

        Response response = courierApiClient.createOrder(order);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
        assertThat("Трек номер должен быть положительным числом",
                createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа - с обоими цветами BLACK и GREY")
    @Description("Проверка, что можно создать заказ с обоими цветами")
    public void createOrderWithBothColorsTest() {
        Order order = orderWithBothColors();

        Response response = courierApiClient.createOrder(order);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
        assertThat("Трек номер должен быть положительным числом",
                createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа - без указания цвета")
    @Description("Проверка, что можно создать заказ без указания цвета")
    public void createOrderWithoutColorTest() {
        Order order = orderWithoutColor();

        Response response = courierApiClient.createOrder(order);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
        assertThat("Трек номер должен быть положительным числом",
                createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа - с пустым списком цветов")
    @Description("Проверка, что можно создать заказ с пустым списком цветов")
    public void createOrderWithEmptyColorListTest() {
        Order order = orderWithEmptyColorList();

        Response response = courierApiClient.createOrder(order);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер не должен быть 0",
                createResponse.getTrack(), not(equalTo(0)));
    }

    @Test
    @DisplayName("Создание заказа - тело ответа содержит track")
    @Description("Проверка, что в ответе присутствует поле track")
    public void createOrderResponseContainsTrackTest() {
        Order order = randomOrder();

        Response response = courierApiClient.createOrder(order);

        assertThat(response.statusCode(), equalTo(201));

        // Проверяем, что поле track существует и не пустое
        assertThat("Ответ должен содержать поле track",
                response.jsonPath().get("track"), notNullValue());

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat("Трек номер должен быть больше 0",
                createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа - все поля заполнены корректно")
    @Description("Проверка создания заказа со всеми обязательными полями")
    public void createOrderWithAllFieldsTest() {
        Order order = randomOrder()
                .setFirstName("Иван")
                .setLastName("Петров")
                .setAddress("Москва, ул. Тестовая, д. 1")
                .setMetroStation(5)
                .setPhone("+7 999 888-77-66")
                .setRentTime(3)
                .setDeliveryDate("2025-12-31")
                .setComment("Тестовый комментарий");

        Response response = courierApiClient.createOrder(order);

        assertThat(response.statusCode(), equalTo(201));

        CreateOrderResponse createResponse = response.as(CreateOrderResponse.class);
        assertThat(createResponse.getTrack(), greaterThan(0));
    }

    @Test
    @DisplayName("Создание нескольких заказов - трек номера уникальны")
    @Description("Проверка, что при создании нескольких заказов трек номера разные")
    public void createMultipleOrdersTracksAreUniqueTest() {
        Order order1 = randomOrder();
        Order order2 = randomOrder();
        Order order3 = randomOrder();

        Response response1 = courierApiClient.createOrder(order1);
        Response response2 = courierApiClient.createOrder(order2);
        Response response3 = courierApiClient.createOrder(order3);

        assertThat(response1.statusCode(), equalTo(201));
        assertThat(response2.statusCode(), equalTo(201));
        assertThat(response3.statusCode(), equalTo(201));

        int track1 = response1.as(CreateOrderResponse.class).getTrack();
        int track2 = response2.as(CreateOrderResponse.class).getTrack();
        int track3 = response3.as(CreateOrderResponse.class).getTrack();

        assertThat("Трек номера должны быть разными", track1, not(equalTo(track2)));
        assertThat("Трек номера должны быть разными", track1, not(equalTo(track3)));
        assertThat("Трек номера должны быть разными", track2, not(equalTo(track3)));
    }
}