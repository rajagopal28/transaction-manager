package com.revolut.assesment.project.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DataValidationException extends RuntimeException {
    private String fieldNames;
}
