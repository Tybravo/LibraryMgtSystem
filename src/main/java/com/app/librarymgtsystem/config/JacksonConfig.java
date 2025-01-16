package com.app.librarymgtsystem.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class JacksonConfig {
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
//        objectMapper.registerModule(module);
//        System.out.println("Custom ObjectMapper Registered!"); // Debug line
//        return objectMapper;
//    }
}
