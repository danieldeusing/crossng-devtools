package de.danieldeusing.crossng.devtools.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class ProxyFilter implements Filter
{
    private final RestTemplate restTemplate;
    @Value("${context.base-url}")
    private String baseUrl;

    @Autowired
    public ProxyFilter(RestTemplate restTemplateBuilder)
    {
        this.restTemplate = restTemplateBuilder;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (shouldProxy(request))
        {
            try
            {
                ResponseEntity<String> proxyResponse = restTemplate.exchange(
                    baseUrl + request.getRequestURI(),
                    HttpMethod.valueOf(request.getMethod()),
                    new HttpEntity<>(getBody(request), getHeaders(request)),
                    String.class);

                HttpStatusCode statusCode = proxyResponse.getStatusCode();
                response.setStatus(statusCode.value());

                String responseBody = proxyResponse.getBody();
                if (responseBody != null && response.getWriter() != null)
                {
                    response.getWriter().write(responseBody);
                }

            }
            catch (Exception e)
            {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("Proxy error");
            }
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }


    private boolean shouldProxy(HttpServletRequest request)
    {
        return request.getRequestURI().startsWith("/crossng-systemmanagement");
    }

    private HttpHeaders getHeaders(HttpServletRequest request)
    {
        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements())
        {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerValue != null)
            {
                headers.set(headerName, headerValue);
            }
        }

        headers.remove("Host");

        return headers;
    }

    private String getBody(HttpServletRequest request)
    {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader())
        {
            if (bufferedReader != null)
            {
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) != -1)
                {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
