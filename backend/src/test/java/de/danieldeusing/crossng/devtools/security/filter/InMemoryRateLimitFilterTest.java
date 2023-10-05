package de.danieldeusing.crossng.devtools.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class InMemoryRateLimitFilterTest
{

    @Value("${security.rate-limit}")
    private long rateLimitPerMinute;

    private InMemoryRateLimitFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    public void setup()
    {
        filter = new InMemoryRateLimitFilter(rateLimitPerMinute);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    public void testPassesWhenUnderRateLimit() throws IOException, ServletException
    {
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testFailsWhenOverRateLimit() throws IOException, ServletException
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        for (int i = 0; i < rateLimitPerMinute; i++)
        {
            filter.doFilterInternal(request, response, filterChain);
        }

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times((int) rateLimitPerMinute)).doFilter(request, response);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(response, times(1)).setStatus(captor.capture());

        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, captor.getValue().intValue());

        printWriter.flush();
        String responseOutput = stringWriter.toString();
        assertTrue(responseOutput.contains("\"errorMessage\":\"Too many requests\""));
    }
}
