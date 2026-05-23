package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import edu.praktikum.sprint7.models.Courier;
import edu.praktikum.sprint7.models.CourierCreds;
import edu.praktikum.sprint7.models.CourierLoginResponse;
import edu.praktikum.sprint7.models.CreateCourierResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.praktikum.sprint7.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Создать курьера")
public class CreateCourierTest {

    private CourierApiClient courierApiClient;
    private Courier createdCourier;
    private String courierId;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @AfterEach
    public void tearDown() {
        // Очистка: удаляем созданного курьера после теста
        if (courierId != null && !courierId.isEmpty()) {
            courierApiClient.deleteCourier(courierId);
        }
    }

    private Courier createCourierWithoutLogin() {
        return new Courier()
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    private Courier createCourierWithoutPassword() {
        return new Courier()
                .setLogin(randomString())
                .setFirstName(randomString());
    }

    private Courier createCourierWithoutFirstName() {
        return new Courier()
                .setLogin(randomString())
                .setPassword(randomString());
    }

    private Courier createCourierWithEmptyLogin() {
        return new Courier()
                .setLogin("")
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    private Courier createCourierWithEmptyPassword() {
        return new Courier()
                .setLogin(randomString())
                .setPassword("")
                .setFirstName(randomString());
    }

    @Test
    @DisplayName("Создание курьера - успешный сценарий")
    @Description("Проверка, что курьера можно успешно создать со всеми обязательными полями")
    public void createCourierSuccessTest() {
        createdCourier = createUniqueCourier();

        Response response = courierApiClient.createCourier(createdCourier);

        assertThat("Код ответа должен быть 201 Created",
                response.statusCode(), equalTo(201));

        CreateCourierResponse createResponse = response.as(CreateCourierResponse.class);
        assertThat("Поле ok должно быть true",
                createResponse.isOk(), equalTo(true));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        assertThat("Курьер должен успешно авторизоваться после создания",
                loginResponse.statusCode(), equalTo(200));

        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();
        assertThat("ID курьера не должен быть пустым",
                courierId, notNullValue());
    }

    @Test
    @DisplayName("Создание курьера - проверка обязательных полей")
    @Description("Проверка, что в запросе нужно передать все обязательные поля (login, password, firstName)")
    public void createCourierAllRequiredFieldsTest() {
        createdCourier = createUniqueCourier();

        Response response = courierApiClient.createCourier(createdCourier);

        assertThat("Код ответа должен быть 201",
                response.statusCode(), equalTo(201));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        assertThat("Авторизация должна быть успешной",
                loginResponse.statusCode(), equalTo(200));

        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();
    }

    @Test
    @DisplayName("Создание курьера - нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что при попытке создать курьера с уже существующим логином возвращается ошибка")
    public void createDuplicateCourierTest() {
        createdCourier = createUniqueCourier();

        Response firstResponse = courierApiClient.createCourier(createdCourier);
        assertThat("Первый курьер должен создаться успешно",
                firstResponse.statusCode(), equalTo(201));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();

        Response duplicateResponse = courierApiClient.createCourier(createdCourier);

        assertThat("Код ответа для дубликата должен быть 409 Conflict",
                duplicateResponse.statusCode(), equalTo(409));

        CreateCourierResponse errorResponse = duplicateResponse.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о существующем логине",
                errorResponse.getMessage(), containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера - запрос без логина возвращает ошибку")
    @Description("Проверка, что при отсутствии поля login возвращается ошибка 400")
    public void createCourierWithoutLoginTest() {
        Courier courierWithoutLogin = createCourierWithoutLogin();

        Response response = courierApiClient.createCourier(courierWithoutLogin);

        assertThat("Код ответа должен быть 400 Bad Request",
                response.statusCode(), equalTo(400));

        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о недостатке данных",
                errorResponse.getMessage(), equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - запрос без пароля возвращает ошибку")
    @Description("Проверка, что при отсутствии поля password возвращается ошибка 400")
    public void createCourierWithoutPasswordTest() {
        Courier courierWithoutPassword = createCourierWithoutPassword();

        Response response = courierApiClient.createCourier(courierWithoutPassword);

        assertThat("Код ответа должен быть 400 Bad Request",
                response.statusCode(), equalTo(400));

        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о недостатке данных",
                errorResponse.getMessage(), equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - запрос с пустым логином возвращает ошибку")
    @Description("Проверка, что при пустом поле login возвращается ошибка 400")
    public void createCourierWithEmptyLoginTest() {
        Courier courierWithEmptyLogin = createCourierWithEmptyLogin();

        Response response = courierApiClient.createCourier(courierWithEmptyLogin);

        assertThat("Код ответа должен быть 400 Bad Request",
                response.statusCode(), equalTo(400));

        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о недостатке данных",
                errorResponse.getMessage(), equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - запрос с пустым паролем возвращает ошибку")
    @Description("Проверка, что при пустом поле password возвращается ошибка 400")
    public void createCourierWithEmptyPasswordTest() {
        Courier courierWithEmptyPassword = createCourierWithEmptyPassword();

        Response response = courierApiClient.createCourier(courierWithEmptyPassword);

        assertThat("Код ответа должен быть 400 Bad Request",
                response.statusCode(), equalTo(400));

        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о недостатке данных",
                errorResponse.getMessage(), equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - успешный запрос возвращает ok: true")
    @Description("Проверка, что поле ok в ответе равно true при успешном создании")
    public void createCourierReturnsOkTrueTest() {
        createdCourier = createUniqueCourier();

        Response response = courierApiClient.createCourier(createdCourier);

        CreateCourierResponse createResponse = response.as(CreateCourierResponse.class);
        assertThat("Поле ok должно быть true",
                createResponse.isOk(), equalTo(true));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();
    }

    @Test
    @DisplayName("Создание курьера - firstName может быть пустым")
    @Description("Проверка, что поле firstName может быть пустым (необязательное поле)")
    public void createCourierWithEmptyFirstNameTest() {
        createdCourier = createCourierWithoutFirstName();

        Response response = courierApiClient.createCourier(createdCourier);

        assertThat("Код ответа должен быть 201 даже без firstName",
                response.statusCode(), equalTo(201));

        CreateCourierResponse createResponse = response.as(CreateCourierResponse.class);
        assertThat("Поле ok должно быть true",
                createResponse.isOk(), equalTo(true));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        assertThat("Курьер без firstName должен авторизоваться",
                loginResponse.statusCode(), equalTo(200));

        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();
    }
}