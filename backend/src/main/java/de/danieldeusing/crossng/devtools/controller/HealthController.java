package de.danieldeusing.crossng.devtools.controller;

import de.danieldeusing.crossng.devtools.model.HealthStatusDTO;
import de.danieldeusing.crossng.devtools.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.danieldeusing.crossng.devtools.util.WebConstants.API_BASE_PATH;


@RestController
@RequestMapping(API_BASE_PATH)
public class HealthController
{

    private final HealthService healthService;

    public HealthController(HealthService healthService)
    {
        this.healthService = healthService;
    }

    @GetMapping("/check-health/{containerName}")
    public ResponseEntity<HealthStatusDTO> checkHealth(@PathVariable String containerName)
    {
        HealthStatusDTO status = healthService.checkContainerHealth(containerName);
        return ResponseEntity.status(status.getResponseCode()).body(status);
    }
}
