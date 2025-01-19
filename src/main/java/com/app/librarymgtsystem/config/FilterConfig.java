package com.app.librarymgtsystem.config;

import com.app.librarymgtsystem.middleware.ThreadLocalCleanupFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public Filter threadLocalCleanupFilter() {
        return new ThreadLocalCleanupFilter();
    }
}
