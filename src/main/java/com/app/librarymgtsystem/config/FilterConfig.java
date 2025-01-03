package com.app.librarymgtsystem.config;

import com.app.librarymgtsystem.middleware.ThreadLocalCleanupFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration
public class FilterConfig {

    @Bean
    public Filter threadLocalCleanupFilter() {
        return new ThreadLocalCleanupFilter();
    }
}
