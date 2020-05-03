package com.sparky.trak.email.service.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EmailDto {

    private String from;

    private String to;

    private String subject;

    private Map<String, Object> data = new HashMap<>();
}
