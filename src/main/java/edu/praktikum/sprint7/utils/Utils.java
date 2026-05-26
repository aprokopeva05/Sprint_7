package edu.praktikum.sprint7.utils;

import com.github.javafaker.Faker;
import edu.praktikum.sprint7.models.Courier;

import java.util.Locale;

public class Utils {

    private static final Faker faker = new Faker(new Locale("ru"));

    // Метод для генерации случайной строки
    public static String randomString() {
        return randomString(10);
    }

    public static String randomString(int length) {
        return faker.lorem().characters(length, true, true);
    }

    // Генерация реальных имен, фамилий и т.д.
    public static String randomFirstName() {
        return faker.name().firstName();
    }

    public static String randomLastName() {
        return faker.name().lastName();
    }

    public static String randomPhone() {
        return faker.phoneNumber().phoneNumber();
    }

    public static String randomAddress() {
        return faker.address().fullAddress();
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    // Создает уникального курьера и возвращает его
    public static Courier createUniqueCourier() {
        return new Courier()
                .setLogin(faker.name().username())
                .setPassword(faker.internet().password())
                .setFirstName(faker.name().firstName());
    }

    // Создает курьера с заданным логином
    public static Courier createCourierWithLogin(String login) {
        return new Courier()
                .setLogin(login)
                .setPassword(faker.internet().password())
                .setFirstName(faker.name().firstName());
    }

    // Создает курьера без пароля
    public static Courier createCourierWithoutPassword() {
        return new Courier()
                .setLogin(faker.name().username())
                .setFirstName(faker.name().firstName());
    }

    // Создает курьера без логина
    public static Courier createCourierWithoutLogin() {
        return new Courier()
                .setPassword(faker.internet().password())
                .setFirstName(faker.name().firstName());
    }

    // Создает курьера без имени
    public static Courier createCourierWithoutFirstName() {
        return new Courier()
                .setLogin(faker.name().username())
                .setPassword(faker.internet().password());
    }

    // Создает курьера с пустым логином
    public static Courier createCourierWithEmptyLogin() {
        return new Courier()
                .setLogin("")
                .setPassword(faker.internet().password())
                .setFirstName(faker.name().firstName());
    }

    // Создает курьера с пустым паролем
    public static Courier createCourierWithEmptyPassword() {
        return new Courier()
                .setLogin(faker.name().username())
                .setPassword("")
                .setFirstName(faker.name().firstName());
    }
}