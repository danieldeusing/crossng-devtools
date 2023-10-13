package de.danieldeusing.crossng.devtools.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProxyFilterTest
{
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @MockBean
    private RestTemplate restTemplate;

    private ProxyFilter proxyFilter;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        proxyFilter = new ProxyFilter(restTemplate);
    }

    @Test
    void testDoFilterShouldProxy() throws Exception
    {
        when(request.getRequestURI()).thenReturn("/crossng-systemmanagement/somePath");
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        ResponseEntity<String> remoteResponse = new ResponseEntity<>("Response Content", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
            .thenReturn(remoteResponse);

        proxyFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpStatus.OK.value());

        String capturedResponseContent = stringWriter.toString();
        assertEquals("Response Content", capturedResponseContent);

        verify(filterChain, never()).doFilter(any(), any());
    }


    @Test
    void testDoFilterShouldNotProxy() throws Exception
    {
        when(request.getRequestURI()).thenReturn("/other-path");

        proxyFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
