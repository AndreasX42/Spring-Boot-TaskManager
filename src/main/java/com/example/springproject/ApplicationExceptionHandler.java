package com.example.springproject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.springproject.exception.EntityNotFoundException;

import com.example.springproject.exception.ErrorResponse;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        Pattern pattern = Pattern.compile("CONSTRAINT_INDEX_\\w+ ON PUBLIC.USERS\\((\\w+) NULLS FIRST\\)");
        Matcher matcher = null;

        if (ex.getCause() != null && ex.getCause().getCause() != null) {
            matcher = pattern.matcher(ex.getCause().getCause().getMessage());
        }

        if (matcher != null && matcher.find()) {
            String violatedField = matcher.group(1).toUpperCase();

            switch (violatedField) {
                case "USERNAME":
                    return new ResponseEntity<>(new ErrorResponse("Username is already in use."),
                            HttpStatus.BAD_REQUEST);
                case "EMAIL":
                    return new ResponseEntity<>(new ErrorResponse("Email address is already in use."),
                            HttpStatus.BAD_REQUEST);
                default:
                    break;
            }
        }

        return new ResponseEntity<>(new ErrorResponse(
                "Data Integrity Violation: we cannot process your request."),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        return new ResponseEntity<>(new ErrorResponse(errors), HttpStatus.BAD_REQUEST);
    }

}
