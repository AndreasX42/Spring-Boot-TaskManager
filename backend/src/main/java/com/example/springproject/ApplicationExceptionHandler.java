package com.example.springproject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.springproject.exception.DuplicateEntityException;
import com.example.springproject.exception.EntityNotFoundException;

import com.example.springproject.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler({ EntityNotFoundException.class })
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(EntityNotFoundException ex,
                        HttpServletRequest request) {

                ErrorResponse reponse = new ErrorResponse(request.getPathInfo(),
                                Arrays.asList(ex.getMessage()), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());

                return new ResponseEntity<>(reponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler({ DuplicateEntityException.class })
        public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateEntityException ex,
                        HttpServletRequest request) {

                ErrorResponse reponse = new ErrorResponse(request.getPathInfo(),
                                Arrays.asList(ex.getMessage()), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

                return new ResponseEntity<>(reponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handlResponseEntity(DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                ErrorResponse reponse = new ErrorResponse(request.getRequestURI(),
                                Arrays.asList("Data Integrity Violation: we cannot process your request."),
                                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

                return new ResponseEntity<>(reponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleDeniedAccess(AccessDeniedException ex,
                        HttpServletRequest request) {

                ErrorResponse reponse = new ErrorResponse(request.getRequestURI(),
                                Arrays.asList(ex.getMessage()),
                                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

                return new ResponseEntity<>(reponse, HttpStatus.FORBIDDEN);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
                        WebRequest request) {

                String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

                List<String> messages = ex.getBindingResult().getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.toList());

                ErrorResponse response = new ErrorResponse(requestUri,
                                messages, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

}
