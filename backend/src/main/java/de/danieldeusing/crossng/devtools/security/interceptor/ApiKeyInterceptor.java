package de.danieldeusing.crossng.devtools.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor
{

    @Value("${security.x_api-key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {

        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        String uri = request.getRequestURI();

        if (uri.startsWith("/api") && (!isValidApiKey(request.getHeader("X-API-KEY"))))
        {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("responseCode", 401);
            responseData.put("errorMessage", "Invalid API Key");

            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonResponse);

            return false;

        }

        return true;
    }

    private boolean isValidApiKey(String apiKey)
    {
        return this.apiKey.equals(apiKey);
    }
}