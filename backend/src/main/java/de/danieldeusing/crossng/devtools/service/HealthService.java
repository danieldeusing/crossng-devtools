package de.danieldeusing.crossng.devtools.service;

import de.danieldeusing.crossng.devtools.model.HealthStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HealthService
{

    private final RestTemplate restTemplate;
    @Value("${context.base-url}")
    private String baseUrl;

    @Autowired
    public HealthService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public HealthStatusDTO checkContainerHealth(String containerName)
    {
        String healthCheckUrl = baseUrl + containerName + "/status/health";

        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);

            if (response.getBody() != null && response.getBody().contains("UP"))
            {
                return new HealthStatusDTO(200, "UP", null);
            }
            else
            {
                return new HealthStatusDTO(200, "DOWN", "Service responded but without expected health status.");
            }
        }
        catch (HttpStatusCodeException e)
        {
            String errorMessage = extractRelevantErrorMessage(e.getResponseBodyAsString());
            return new HealthStatusDTO(200, "DOWN", errorMessage);
        }
        catch (RestClientException e)
        {
            return new HealthStatusDTO(200, "DOWN", e.getMessage());
        }
    }

    private String extractRelevantErrorMessage(String errorBody)
    {
        Pattern pattern = Pattern.compile("<h1>([^<]+)</h1>");
        Matcher matcher = pattern.matcher(errorBody);
        if (matcher.find())
        {
            return matcher.group(1).trim();
        }
        else
        {
            return "An error occurred, but the specific details could not be extracted.";
        }
    }
}
