package de.danieldeusing.crossng.devtools.config;

import de.danieldeusing.crossng.devtools.security.filter.InMemoryRateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.danieldeusing.crossng.devtools.util.WebConstants.API_BASE_PATH;

@Configuration
public class FilterConfig
{
    @Value("${security.rate-limit:120}")
    private long rateLimitPerMinute;

    @Bean
    public FilterRegistrationBean<InMemoryRateLimitFilter> loggingFilter()
    {
        FilterRegistrationBean<InMemoryRateLimitFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new InMemoryRateLimitFilter(rateLimitPerMinute));
        registrationBean.addUrlPatterns(API_BASE_PATH + "/*");

        return registrationBean;
    }
}
