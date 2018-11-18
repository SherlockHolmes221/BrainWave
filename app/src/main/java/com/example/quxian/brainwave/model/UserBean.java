package com.example.quxian.brainwave.model;

public class UserBean {
    private String account;
    private String password;
    private double weight;
    private double height;
    private int age;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWegiht() {
        return weight;
    }

    public void setWegiht(double wegiht) {
        this.weight = wegiht;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", wegiht=" + weight +
                ", height=" + height +
                ", age=" + age +
                '}';
    }
}
