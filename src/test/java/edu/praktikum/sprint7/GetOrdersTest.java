package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Список заказов")
public class GetOrdersTest {

    private CourierApiClient courierApiClient;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @Test
    @DisplayName("Получение списка заказов - возвращается список заказов")
    @Description("Проверка, что GET /api/v1/orders возвращает тело ответа, содержащее список заказов")
    public void getOrdersReturnsOrdersListTest() {
        Response response = courierApiClient.getOrders();

        assertThat("Код ответа должен быть 200 OK",
                response.statusCode(), equalTo(200));

        // Проверяем, что в ответе есть поле orders и оно является списком
        List<Object> orders = response.jsonPath().getList("orders");
        assertThat("Тело ответа должно содержать поле orders",
                orders, notNullValue());
        assertThat("Поле orders должно быть списком",
                orders, instanceOf(List.class));
    }
}