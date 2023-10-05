package de.danieldeusing.crossng.devtools.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthStatusDTO
{
    private int responseCode;
    private String status;
    private String errorMessage;
}