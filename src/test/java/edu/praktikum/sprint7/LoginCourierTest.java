package edu.praktikum.sprint7;

import edu.praktikum.sprint7.clients.CourierApiClient;
import edu.praktikum.sprint7.models.Courier;
import edu.praktikum.sprint7.models.CourierCreds;
import edu.praktikum.sprint7.models.CourierLoginResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static edu.praktikum.sprint7.utils.Utils.createUniqueCourier;
import static edu.praktikum.sprint7.utils.Utils.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Логин курьера")
public class LoginCourierTest {

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

    private CourierCreds createValidCredentials(Courier courier) {
        return CourierCreds.fromCourier(courier);
    }

    private CourierCreds createInvalidCredentials() {
        return new CourierCreds("invalid_login_" + randomString(), "invalid_password");
    }

    // Приватный конструктор для CourierCreds? Добавим статический метод создания
    private CourierCreds createCredentials(String login, String password) {
        // Временное решение - нужно будет добавить метод в CourierCreds
        return CourierCreds.fromLoginAndPassword(login, password);
    }

    @Test
    @DisplayName("Логин курьера - успешная авторизация")
    @Description("Проверка, что курьер может успешно авторизоваться с правильными логином и паролем")
    public void courierCanLoginSuccessTest() {
        // Arrange - сначала создаем курьера
        createdCourier = createUniqueCourier();
        Response createResponse = courierApiClient.createCourier(createdCourier);
        assertThat("Курьер должен создаться успешно", createResponse.statusCode(), equalTo(201));

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));

        assertThat("Код ответа должен быть 200 OK",
                loginResponse.statusCode(), equalTo(200));

        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        assertThat("ID не должен быть null",
                loginResponseBody.getId(), notNullValue());
        assertThat("ID не должен быть пустым",
                loginResponseBody.getId(), not(emptyString()));

        courierId = loginResponseBody.getId();
    }

    @Test
    @DisplayName("Логин курьера - для авторизации нужны все обязательные поля")
    @Description("Проверка, что при авторизации нужно передать login и password")
    public void loginRequiresAllRequiredFieldsTest() {
        createdCourier = createUniqueCourier();
        courierApiClient.createCourier(createdCourier);

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        courierId = loginResponse.as(CourierLoginResponse.class).getId();

        assertThat("С правильными полями вход должен быть успешным",
                loginResponse.statusCode(), equalTo(200));
    }

    @Test
    @DisplayName("Логин курьера - ошибка при неправильном логине")
    @Description("Проверка, что при неправильном логине возвращается ошибка 404")
    public void loginWithWrongLoginTest() {
        createdCourier = createUniqueCourier();
        courierApiClient.createCourier(createdCourier);

        Response validLoginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        courierId = validLoginResponse.as(CourierLoginResponse.class).getId();

        String wrongLogin = "wrong_" + createdCourier.getLogin();
        CourierCreds wrongCreds = createCredentials(wrongLogin, createdCourier.getPassword());
        Response loginResponse = courierApiClient.loginCourier(wrongCreds);

        assertThat("Код ответа должен быть 404 Not Found",
                loginResponse.statusCode(), equalTo(404));

        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat("Сообщение об ошибке должно быть об отсутствии учетной записи",
                errorMessage, equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - ошибка при неправильном пароле")
    @Description("Проверка, что при неправильном пароле возвращается ошибка 404")
    public void loginWithWrongPasswordTest() {
        createdCourier = createUniqueCourier();
        courierApiClient.createCourier(createdCourier);

        Response validLoginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));
        courierId = validLoginResponse.as(CourierLoginResponse.class).getId();

        String wrongPassword = "wrong_" + createdCourier.getPassword();
        CourierCreds wrongCreds = createCredentials(createdCourier.getLogin(), wrongPassword);
        Response loginResponse = courierApiClient.loginCourier(wrongCreds);

        assertThat("Код ответа должен быть 404 Not Found",
                loginResponse.statusCode(), equalTo(404));

        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat("Сообщение об ошибке должно быть об отсутствии учетной записи",
                errorMessage, equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - ошибка при отсутствии логина")
    @Description("Проверка, что при отсутствии поля login возвращается ошибка 400")
    public void loginWithoutLoginTest() {
        createdCourier = createUniqueCourier();
        courierApiClient.createCourier(createdCourier);

        Response loginResponse = courierApiClient.loginCourierWithoutLogin(createdCourier.getPassword());

        assertThat("Код ответа должен быть 400 Bad Request",
                loginResponse.statusCode(), equalTo(400));

        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat("Сообщение об ошибке должно быть о недостатке данных",
                errorMessage, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера - ошибка при авторизации несуществующего пользователя")
    @Description("Проверка, что при попытке войти с несуществующей парой логин-пароль возвращается ошибка 404")
    public void loginWithNonExistentUserTest() {
        String nonExistentLogin = "nonexistent_" + randomString();
        String nonExistentPassword = "nonexistent_" + randomString();
        CourierCreds nonExistentCreds = createCredentials(nonExistentLogin, nonExistentPassword);

        Response loginResponse = courierApiClient.loginCourier(nonExistentCreds);

        assertThat("Код ответа должен быть 404 Not Found",
                loginResponse.statusCode(), equalTo(404));

        String errorMessage = loginResponse.jsonPath().getString("message");
        assertThat("Сообщение об ошибке должно быть об отсутствии учетной записи",
                errorMessage, equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - успешный запрос возвращает id")
    @Description("Проверка, что при успешном логине возвращается id курьера")
    public void successfulLoginReturnsIdTest() {
        createdCourier = createUniqueCourier();
        courierApiClient.createCourier(createdCourier);

        Response loginResponse = courierApiClient.loginCourier(CourierCreds.fromCourier(createdCourier));

        assertThat("Код ответа должен быть 200", loginResponse.statusCode(), equalTo(200));

        CourierLoginResponse loginResponseBody = loginResponse.as(CourierLoginResponse.class);
        assertThat("ID должен быть числом",
                loginResponseBody.getId(), matchesPattern("\\d+"));

        courierId = loginResponseBody.getId();
    }
}