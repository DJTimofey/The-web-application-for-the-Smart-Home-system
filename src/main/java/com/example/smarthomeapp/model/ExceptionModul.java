package com.example.smarthomeapp.model;

import lombok.Value;

@Value
public class ExceptionModul {
    String message;
    int statusCode;
    String statusMessage;
}