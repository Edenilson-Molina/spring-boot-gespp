package com.template.spring_mvc.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewAttributesAdvice {

    @ModelAttribute("path")
    public String addCurrentPath(HttpServletRequest request) {
        if (request == null) {
            return "/";
        }
        String uri = request.getRequestURI();
        return (uri == null || uri.isEmpty()) ? "/" : uri;
    }
}
