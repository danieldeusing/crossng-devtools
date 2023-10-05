package de.danieldeusing.crossng.devtools.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ApiKeyInterceptorTest
{

    @Value("${security.api-key}")
    private String apiKey;

    @SpyBean
    private ApiKeyInterceptor apiKeyInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidApiKey() throws Exception
    {
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(request.getRequestURI()).thenReturn("/api/someEndpoint");
        assertTrue(apiKeyInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void testInvalidApiKey() throws Exception
    {
        when(request.getHeader("X-API-KEY")).thenReturn("invalid-api-key");
        when(request.getRequestURI()).thenReturn("/api/someEndpoint");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        assertFalse(apiKeyInterceptor.preHandle(request, response, new Object()));

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        printWriter.flush();
        String responseOutput = stringWriter.toString();
        assertTrue(responseOutput.contains("\"errorMessage\":\"Invalid API Key\""));
    }

    @Test
    void testNonApiRequestAllowedWithoutApiKey() throws Exception
    {
        when(request.getHeader("X-API-KEY")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/nonApiEndpoint");

        assertTrue(apiKeyInterceptor.preHandle(request, response, new Object()));

        verify(response, times(0)).setStatus(
            HttpServletResponse.SC_UNAUTHORIZED);
    }
}
