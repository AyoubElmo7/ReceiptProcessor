package org.assessment.receiptprocessor.contollers;

import org.assessment.receiptprocessor.entities.ReceiptDTO;
import org.assessment.receiptprocessor.services.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(final ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> processReceipt(@RequestBody @Validated ReceiptDTO receiptDTO) {
        return receiptService.saveReceipt(receiptDTO.toReceipt());
    }

    @GetMapping("/{id}/points")
    public Map<String, Integer> getPoints(@PathVariable Long id) {
        return receiptService.calculatePointsFromReceipt(id);
    }
}
