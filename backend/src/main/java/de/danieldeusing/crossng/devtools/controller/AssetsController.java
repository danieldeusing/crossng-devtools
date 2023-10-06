package de.danieldeusing.crossng.devtools.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
public class AssetsController
{

    @GetMapping("/env.js")
    public Resource serveEnvJs()
    {
        return new FileSystemResource("/app/assets/env.js");
    }
}

