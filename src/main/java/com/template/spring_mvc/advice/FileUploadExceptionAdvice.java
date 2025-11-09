package com.template.spring_mvc.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSize(MaxUploadSizeExceededException ex,
                                HttpServletRequest request,
                                RedirectAttributes ra) {
        ra.addFlashAttribute("uploadError", "El archivo excede el tamaño máximo permitido (" + ex.getMaxUploadSize() + " bytes).");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer == null || referer.isBlank() ? "/" : referer);
    }
}
