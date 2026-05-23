package edu.praktikum.sprint7.utils;

import edu.praktikum.sprint7.generator.CourierGenerator;
import edu.praktikum.sprint7.models.Courier;

import java.util.Random;

public class Utils {

    // Метод для генерации случайной строки
    public static String randomString() {
        return randomString(10);
    }

    public static String randomString(int length) {
        Random random = new Random();
        int leftLimit = 97; // буквы a-z
        int rightLimit = 122;
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (float) (rightLimit - leftLimit + 1));
            buffer.append(Character.toChars(randomLimitedInt));
        }

        return buffer.toString();
    }

    // Создает уникального курьера и возвращает его
    public static Courier createUniqueCourier() {
        return CourierGenerator.randomCourier();
    }

    // Создает курьера с заданным логином
    public static Courier createCourierWithLogin(String login) {
        return new Courier()
                .setLogin(login)
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    // Создает курьера без пароля
    public static Courier createCourierWithoutPassword() {
        return new Courier()
                .setLogin(randomString())
                .setFirstName(randomString());
    }

    // Создает курьера без логина
    public static Courier createCourierWithoutLogin() {
        return new Courier()
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    // Создает курьера без имени
    public static Courier createCourierWithoutFirstName() {
        return new Courier()
                .setLogin(randomString())
                .setPassword(randomString());
    }

    // Создает курьера с пустым логином
    public static Courier createCourierWithEmptyLogin() {
        return new Courier()
                .setLogin("")
                .setPassword(randomString())
                .setFirstName(randomString());
    }

    // Создает курьера с пустым паролем
    public static Courier createCourierWithEmptyPassword() {
        return new Courier()
                .setLogin(randomString())
                .setPassword("")
                .setFirstName(randomString());
    }
}