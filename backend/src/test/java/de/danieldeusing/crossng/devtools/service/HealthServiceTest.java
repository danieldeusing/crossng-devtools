package de.danieldeusing.crossng.devtools.service;

import de.danieldeusing.crossng.devtools.model.HealthStatusDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class HealthServiceTest
{

    private final String containerName = "testContainer";
    @Value("${context.base-url}")
    private String baseUrl;
    @Autowired
    private HealthService healthService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void checkContainerHealth_UP()
    {
        String healthCheckUrl = baseUrl + containerName + "/status/health";
        when(restTemplate.getForEntity(healthCheckUrl, String.class))
            .thenReturn(new ResponseEntity<>("UP", HttpStatus.OK));

        HealthStatusDTO result = healthService.checkContainerHealth(containerName);
        assertEquals(200, result.getResponseCode());
        assertEquals("UP", result.getStatus());
    }

    @Test
    void checkContainerHealth_DOWN_NoExpectedStatus()
    {
        String healthCheckUrl = baseUrl + containerName + "/status/health";
        when(restTemplate.getForEntity(healthCheckUrl, String.class))
            .thenReturn(new ResponseEntity<>("UNKNOWN", HttpStatus.OK));

        HealthStatusDTO result = healthService.checkContainerHealth(containerName);
        assertEquals(200, result.getResponseCode());
        assertEquals("DOWN", result.getStatus());
        assertEquals("Service responded but without expected health status.", result.getErrorMessage());
    }

    @Test
    void checkContainerHealth_DOWN_HttpStatusCodeException()
    {
        String healthCheckUrl = baseUrl + containerName + "/status/health";
        HttpClientErrorException
            exception = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY,
            "<h1>Invalid request</h1>".getBytes(), Charset.defaultCharset());
        when(restTemplate.getForEntity(healthCheckUrl, String.class)).thenThrow(exception);


        HealthStatusDTO result = healthService.checkContainerHealth(containerName);
        assertEquals(200, result.getResponseCode());
        assertEquals("DOWN", result.getStatus());
        assertEquals("Invalid request", result.getErrorMessage());
    }

    @Test
    void checkContainerHealth_DOWN_RestClientException()
    {
        String healthCheckUrl = baseUrl + containerName + "/status/health";
        when(restTemplate.getForEntity(healthCheckUrl, String.class))
            .thenThrow(new RestClientException("Connection failed"));

        HealthStatusDTO result = healthService.checkContainerHealth(containerName);
        assertEquals(200, result.getResponseCode());
        assertEquals("DOWN", result.getStatus());
        assertEquals("Connection failed", result.getErrorMessage());
    }

    @Test
    void checkContainerHealth_DOWN_NoContainer()
    {
        String nonExistingContainerName = "RANDOM";
        String healthCheckUrl = baseUrl + nonExistingContainerName + "/status/health";
        HttpClientErrorException
            exception = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            HttpHeaders.EMPTY, ("<!doctype html><html lang=\\\"en\\\"><head><title>HTTP Status 404 – Not " +
                "Found</title><style type=\\\"text/css\\\">body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, " +
                "b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} " +
                "h3 {font-size: 14px;} p {font-size:12px;} a {color:black;} .line " +
                "{height:1px;background-color:#525D76;border:none;}</style></head><body><h1>HTTP Status 404 – " +
                "Not Found</h1></body></html>").getBytes(),
            Charset.defaultCharset());
        when(restTemplate.getForEntity(healthCheckUrl, String.class)).thenThrow(exception);

        HealthStatusDTO result = healthService.checkContainerHealth(nonExistingContainerName);
        assertEquals(200, result.getResponseCode());
        assertEquals("DOWN", result.getStatus());
        assertEquals("HTTP Status 404 – Not Found", result.getErrorMessage());
    }
}
