package edu.praktikum.sprint7.generator;

import edu.praktikum.sprint7.models.Order;
import edu.praktikum.sprint7.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderGenerator {

    private static final List<Integer> VALID_METRO_STATIONS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static Order randomOrder() {
        return new Order()
                .setFirstName("TestFirstName_" + Utils.randomString(5))
                .setLastName("TestLastName_" + Utils.randomString(5))
                .setAddress("Test Address " + Utils.randomString(10))
                .setMetroStation(4) // Станция метро по умолчанию
                .setPhone("+7 999 123-45-67")
                .setRentTime(5)
                .setDeliveryDate("2025-06-06")
                .setComment("Test comment " + Utils.randomString(10));
    }

    public static Order orderWithBlackColor() {
        return randomOrder().setColor(Collections.singletonList("BLACK"));
    }

    public static Order orderWithGreyColor() {
        return randomOrder().setColor(Collections.singletonList("GREY"));
    }

    public static Order orderWithBothColors() {
        return randomOrder().setColor(Arrays.asList("BLACK", "GREY"));
    }

    public static Order orderWithoutColor() {
        return randomOrder().setColor(null);
    }

    public static Order orderWithEmptyColorList() {
        return randomOrder().setColor(Collections.emptyList());
    }
}