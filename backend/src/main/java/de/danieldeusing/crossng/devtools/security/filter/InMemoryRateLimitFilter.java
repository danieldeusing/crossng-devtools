package de.danieldeusing.crossng.devtools.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class InMemoryRateLimitFilter extends OncePerRequestFilter
{
    private final Bucket bucket;

    public InMemoryRateLimitFilter(long rateLimitPerMinute)
    {
        Bandwidth limit =
            Bandwidth.classic(rateLimitPerMinute, Refill.intervally(rateLimitPerMinute, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {

        if (bucket.tryConsume(1))
        {
            filterChain.doFilter(request, response);
        }
        else
        {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("responseCode", 500);
            responseData.put("errorMessage", "Too many requests");

            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(jsonResponse);
        }
    }
}
