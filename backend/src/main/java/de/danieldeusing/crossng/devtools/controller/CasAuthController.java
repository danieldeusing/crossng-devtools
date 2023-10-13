package de.danieldeusing.crossng.devtools.controller;

import de.danieldeusing.crossng.devtools.model.CasLoginResponseDTO;
import de.danieldeusing.crossng.devtools.service.CasAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.danieldeusing.crossng.devtools.util.WebConstants.API_BASE_PATH;

@RestController
@RequestMapping(API_BASE_PATH)
public class CasAuthController
{
    private final CasAuthService casAuthService;

    public CasAuthController(CasAuthService casAuthService)
    {
        this.casAuthService = casAuthService;
    }


    @GetMapping("/casLogin")
    public ResponseEntity<CasLoginResponseDTO> casLogin()
    {
        return ResponseEntity.ok(casAuthService.casLogin(false));
    }
}
