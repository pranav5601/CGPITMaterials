package com.example.pranav.cgpitmaterials;

public class SubjectList {
    String code;
    String sub_name;

    public SubjectList() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public SubjectList(String code, String sub_name) {
        this.code = code;
        this.sub_name = sub_name;
    }
}
