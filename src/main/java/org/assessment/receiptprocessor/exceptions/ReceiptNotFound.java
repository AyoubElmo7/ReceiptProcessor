package org.assessment.receiptprocessor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReceiptNotFound extends RuntimeException {
    public ReceiptNotFound(Long id) {
        super("Receipt with id: " + id + " was not found");
    }
}
