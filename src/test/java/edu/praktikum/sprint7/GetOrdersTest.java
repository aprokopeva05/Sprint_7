package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Sprint 7")
@Feature("Список заказов")
@DisplayName("Тесты получения списка заказов")
public class GetOrdersTest {

    private CourierApiClient courierApiClient;
    private Response response;
    private List<Object> orders;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @Test
    @DisplayName("Получение списка заказов - возвращается непустой список заказов")
    @Description("Проверка, что GET /api/v1/orders возвращает тело ответа, содержащее непустой список заказов")
    public void getOrdersReturnsOrdersListTest() {
        sendGetOrdersRequest();
        verifyResponseIsValid();
        verifyOrdersListIsNotEmpty();
    }

    @Step("Отправка GET запроса на получение списка заказов")
    private void sendGetOrdersRequest() {
        response = courierApiClient.getOrders();
    }

    @Step("Проверка валидности ответа")
    private void verifyResponseIsValid() {
        assertThat("Код ответа должен быть 200 OK",
                response.statusCode(), equalTo(200));

        orders = response.jsonPath().getList("orders");

        assertThat("Тело ответа должно содержать поле orders",
                orders, notNullValue());
        assertThat("Поле orders должно быть списком",
                orders, instanceOf(List.class));
    }

    @Step("Проверка, что список заказов не пустой")
    private void verifyOrdersListIsNotEmpty() {
        assertThat("Список заказов не должен быть пустым",
                orders, not(empty()));
        assertThat("Список заказов должен содержать хотя бы один заказ",
                orders.size(), greaterThan(0));
    }
}