package com.example.assignment.models;

import java.util.List;

public class ApiResponseData {
    private HackNasa data;
    private String message;
    private int status;


    public ApiResponseData() {
    }

    public ApiResponseData(HackNasa data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public HackNasa getData() {
        return data;
    }

    public void setData(HackNasa data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
