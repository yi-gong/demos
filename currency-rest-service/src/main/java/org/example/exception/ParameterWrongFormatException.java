package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The received parameter is in a wrong format.")
public class ParameterWrongFormatException extends RuntimeException {
}
