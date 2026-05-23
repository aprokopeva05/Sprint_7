package edu.praktikum.sprint7.models;

public class CourierCreds {

    private String login;
    private String password;

    public CourierCreds(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static CourierCreds fromCourier(Courier courier) {
        return new CourierCreds(courier.getLogin(), courier.getPassword());
    }
    public static CourierCreds fromLoginAndPassword(String login, String password) {
        return new CourierCreds(login, password);
    }
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
