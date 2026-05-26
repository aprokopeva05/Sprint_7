package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import edu.praktikum.sprint7.models.Courier;
import edu.praktikum.sprint7.models.CourierCreds;
import edu.praktikum.sprint7.models.CourierLoginResponse;
import edu.praktikum.sprint7.models.CreateCourierResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.praktikum.sprint7.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Sprint 7")
@Feature("Создать курьера")
@DisplayName("Тесты создания курьера")
public class CreateCourierTest {

    private CourierApiClient courierApiClient;
    private Courier createdCourier;
    private String courierId;
    private Response response;
    private Response loginResponse;

    @BeforeEach
    public void setUp() {
        courierApiClient = new CourierApiClient();
    }

    @AfterEach
    public void tearDown() {
        deleteCreatedCourier();
    }

    @Step("Создание уникального курьера со всеми полями")
    private Courier createUniqueCourier() {
        return new Courier()
                .setLogin(randomString())
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    @Step("Создание курьера без поля login")
    private Courier createCourierWithoutLogin() {
        return new Courier()
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    @Step("Создание курьера без поля password")
    private Courier createCourierWithoutPassword() {
        return new Courier()
                .setLogin(randomString())
                .setFirstName(randomString());
    }

    @Step("Создание курьера без поля firstName")
    private Courier createCourierWithoutFirstName() {
        return new Courier()
                .setLogin(randomString())
                .setPassword(randomString());
    }

    @Step("Создание курьера с пустым логином")
    private Courier createCourierWithEmptyLogin() {
        return new Courier()
                .setLogin("")
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    @Step("Создание курьера с пустым паролем")
    private Courier createCourierWithEmptyPassword() {
        return new Courier()
                .setLogin(randomString())
                .setPassword("")
                .setFirstName(randomString());
    }

    @Step("Удаление созданного курьера (ID: {courierId})")
    private void deleteCreatedCourier() {
        if (courierId != null && !courierId.isEmpty()) {
            courierApiClient.deleteCourier(courierId);
        }
    }

    @Step("Отправка запроса на создание курьера")
    private void sendCreateCourierRequest(Courier courier) {
        response = courierApiClient.createCourier(courier);
    }

    @Step("Отправка запроса на создание дубликата курьера")
    private void sendDuplicateCourierRequest() {
        response = courierApiClient.createCourier(createdCourier);
    }

    @Step("Отправка запроса на авторизацию курьера")
    private void sendLoginCourierRequest() {
        loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
    }

    @Step("Проверка, что код ответа равен {expectedStatusCode}")
    private void verifyStatusCode(Response response, int expectedStatusCode, String message) {
        assertThat(message, response.statusCode(), equalTo(expectedStatusCode));
    }

    @Step("Проверка, что поле ok равно true")
    private void verifyOkFieldIsTrue() {
        CreateCourierResponse createResponse = response.as(CreateCourierResponse.class);
        assertThat("Поле ok должно быть true", createResponse.isOk(), equalTo(true));
    }

    @Step("Проверка, что авторизация прошла успешно")
    private void verifyLoginSuccess() {
        assertThat("Курьер должен успешно авторизоваться", loginResponse.statusCode(), equalTo(200));
    }

    @Step("Получение ID курьера из ответа авторизации")
    private void extractAndSaveCourierId() {
        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        courierId = loginResponseBody.getId();
        assertThat("ID курьера не должен быть пустым", courierId, notNullValue());
    }

    @Step("Проверка сообщения об ошибке: {expectedMessage}")
    private void verifyErrorMessage(String expectedMessage) {
        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно соответствовать ожидаемому",
                errorResponse.getMessage(), equalTo(expectedMessage));
    }

    @Step("Проверка сообщения об ошибке при создании дубликата")
    private void verifyDuplicateErrorMessage() {
        CreateCourierResponse errorResponse = response.as(CreateCourierResponse.class);
        assertThat("Сообщение об ошибке должно быть о существующем логине",
                errorResponse.getMessage(), containsString("Этот логин уже используется"));
    }

    @Step("Проверка, что ID курьера получен и не пустой")
    private void verifyCourierIdIsNotNull() {
        assertThat("ID курьера не должен быть пустым", courierId, notNullValue());
    }

    @Test
    @DisplayName("Создание курьера - успешный сценарий")
    @Description("Проверка, что курьера можно успешно создать со всеми обязательными полями")
    public void createCourierSuccessTest() {
        createdCourier = createUniqueCourier();
        sendCreateCourierRequest(createdCourier);
        verifyStatusCode(response, 201, "Код ответа должен быть 201 Created");
        verifyOkFieldIsTrue();
        sendLoginCourierRequest();
        verifyLoginSuccess();
        extractAndSaveCourierId();
        verifyCourierIdIsNotNull();
    }

    @Test
    @DisplayName("Создание курьера - проверка обязательных полей")
    @Description("Проверка, что в запросе нужно передать все обязательные поля (login, password, firstName)")
    public void createCourierAllRequiredFieldsTest() {
        createdCourier = createUniqueCourier();
        sendCreateCourierRequest(createdCourier);
        verifyStatusCode(response, 201, "Код ответа должен быть 201");
        sendLoginCourierRequest();
        verifyLoginSuccess();
        extractAndSaveCourierId();
    }

    @Test
    @DisplayName("Создание курьера - нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что при попытке создать курьера с уже существующим логином возвращается ошибка")
    public void createDuplicateCourierTest() {
        createdCourier = createUniqueCourier();

        sendCreateCourierRequest(createdCourier);
        verifyStatusCode(response, 201, "Первый курьер должен создаться успешно");

        sendLoginCourierRequest();
        extractAndSaveCourierId();

        sendDuplicateCourierRequest();
        verifyStatusCode(response, 409, "Код ответа для дубликата должен быть 409 Conflict");
        verifyDuplicateErrorMessage();
    }

    @Test
    @DisplayName("Создание курьера - запрос без логина возвращает ошибку")
    @Description("Проверка, что при отсутствии поля login возвращается ошибка 400")
    public void createCourierWithoutLoginTest() {
        Courier courierWithoutLogin = createCourierWithoutLogin();
        sendCreateCourierRequest(courierWithoutLogin);
        verifyStatusCode(response, 400, "Код ответа должен быть 400 Bad Request");
        verifyErrorMessage("Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера - запрос без пароля возвращает ошибку")
    @Description("Проверка, что при отсутствии поля password возвращается ошибка 400")
    public void createCourierWithoutPasswordTest() {
        Courier courierWithoutPassword = createCourierWithoutPassword();
        sendCreateCourierRequest(courierWithoutPassword);
        verifyStatusCode(response, 400, "Код ответа должен быть 400 Bad Request");
        verifyErrorMessage("Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера - запрос с пустым логином возвращает ошибку")
    @Description("Проверка, что при пустом поле login возвращается ошибка 400")
    public void createCourierWithEmptyLoginTest() {
        Courier courierWithEmptyLogin = createCourierWithEmptyLogin();
        sendCreateCourierRequest(courierWithEmptyLogin);
        verifyStatusCode(response, 400, "Код ответа должен быть 400 Bad Request");
        verifyErrorMessage("Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера - запрос с пустым паролем возвращает ошибку")
    @Description("Проверка, что при пустом поле password возвращается ошибка 400")
    public void createCourierWithEmptyPasswordTest() {
        Courier courierWithEmptyPassword = createCourierWithEmptyPassword();
        sendCreateCourierRequest(courierWithEmptyPassword);
        verifyStatusCode(response, 400, "Код ответа должен быть 400 Bad Request");
        verifyErrorMessage("Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера - успешный запрос возвращает ok: true")
    @Description("Проверка, что поле ok в ответе равно true при успешном создании")
    public void createCourierReturnsOkTrueTest() {
        createdCourier = createUniqueCourier();
        sendCreateCourierRequest(createdCourier);
        verifyOkFieldIsTrue();
        sendLoginCourierRequest();
        extractAndSaveCourierId();
    }

    @Test
    @DisplayName("Создание курьера - firstName может быть пустым")
    @Description("Проверка, что поле firstName может быть пустым (необязательное поле)")
    public void createCourierWithEmptyFirstNameTest() {
        createdCourier = createCourierWithoutFirstName();
        sendCreateCourierRequest(createdCourier);
        verifyStatusCode(response, 201, "Код ответа должен быть 201 даже без firstName");
        verifyOkFieldIsTrue();
        sendLoginCourierRequest();
        verifyLoginSuccess();
        extractAndSaveCourierId();
    }
}