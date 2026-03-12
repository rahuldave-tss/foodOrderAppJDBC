package com.tss.entity;

public abstract class User {
    private int id;
    private String userName;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private Role role;

    public User(int id,String userName, String name, String password, String email, String phoneNumber, Role role) {
        this.id=id;
        this.userName=userName;
        this.name = name;
        this.password = password;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.role=role;
    }
    public User(String userName, String name, String password, String email, String phoneNumber, Role role) {
        this.userName=userName;
        this.name = name;
        this.password = password;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.role=role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format(
                "+----+----------------+----------------+----------------------+--------------+----------+\n" +
                        "| ID | Name           | Password       | Email                | Phone Number | Role     |\n" +
                        "+----+----------------+----------------+----------------------+--------------+----------+\n" +
                        "| %-2d | %-14s | %-14s | %-20s | %-12s | %-8s |\n" +
                        "+----+----------------+----------------+----------------------+--------------+----------+",
                id,
                name,
                password,
                email,
                phoneNumber,
                role
        );
    }
}
