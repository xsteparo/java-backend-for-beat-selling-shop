package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.error.ErrorDetailsDto;
import com.cz.cvut.fel.instumentalshop.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailsDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
                ));

        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation Failed")
                .details("Input validation errors occurred")
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeleteRequestException.class)
    public ResponseEntity<ErrorDetailsDto> handleYourCustomValidationException(DeleteRequestException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation error on delete operation")
                .details(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetailsDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Invalid argument")
                .details(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedProducerException.class)
    public ResponseEntity<String> handleUnauthorizedProducerException(UnauthorizedProducerException ex) {
        String errorMessage = "Unauthorized access. Please login to proceed.";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }


    @ExceptionHandler(InvalidProfitPercentageException.class)
    public ResponseEntity<ErrorDetailsDto> handleUsernameNotFoundException(InvalidProfitPercentageException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(String.format("Your total percentage is : %s", ex.getTotalPercentage()))
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicatedProducersException.class)
    public ResponseEntity<ErrorDetailsDto> handleDuplicatedProducersException(DuplicatedProducersException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(String.format("Duplicated producer names %s", ex.getDuplicatedUsernames()))
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MainProducerFoundInShareListException.class)
    public ResponseEntity<ErrorDetailsDto> handleDuplicatedProducersException(MainProducerFoundInShareListException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(String.format("Requesting producer name: %s", ex.getRequestingProducerName()))
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleUsernameNotFoundException(UserNotFoundException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message(String.format("Producer not found %s", ex.getMessage()))
                .details(String.format("Missing names: %s", ex.getMissingUsernames()))
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetailsDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detailMessage = ex.getMessage();

        if (detailMessage.contains("com.cvut.cz.fel.ear.instumentalshop.domain.enums.GenreType")) {
            detailMessage = "Invalid genre type provided. Available genres are: DNB, POP, ELECTRO.";
        }

        if (detailMessage.contains("com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType")) {
            detailMessage = "Invalid licence type. Available genres are: EXCLUSIVE, STANDARD.";
        }

        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Request parsing error")
                .details(detailMessage)
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetailsDto> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Access Denied")
                .details(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(LicenceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleIllegalStateException(LicenceAlreadyExistsException ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Illegal State")
                .details(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsDto> handleAllExceptions(Exception ex) {
        ErrorDetailsDto errorDetailsDto = ErrorDetailsDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Internal Server Error")
                .details(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorDetailsDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
