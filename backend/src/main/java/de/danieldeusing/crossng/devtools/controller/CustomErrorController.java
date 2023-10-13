package de.danieldeusing.crossng.devtools.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // If the error status code is 404, then forward to the Angular app's index page
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "forward:/";
            }
        }

        // For all other errors, use the default behavior
        return "error";
    }
}
