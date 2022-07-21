package com.example.pranav.cgpitmaterials;

public class User {
    String name, number, branch, sem, password;

    public User() {
    }

    public User(String name, String number, String branch, String sem, String password) {

        this.name = name;
        this.number = number;
        this.branch = branch;
        this.sem = sem;
        this.password = password;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
