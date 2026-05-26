package edu.praktikum.sprint7.generator;

import edu.praktikum.sprint7.models.Courier;
import edu.praktikum.sprint7.utils.Utils;

public class CourierGenerator {

    public static Courier randomCourier() {
        return new Courier()
                .setLogin(Utils.randomString())
                .setPassword(Utils.randomString())
                .setFirstName(Utils.randomString());
    }
}