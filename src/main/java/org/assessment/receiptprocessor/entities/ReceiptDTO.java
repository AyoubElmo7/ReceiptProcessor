package org.assessment.receiptprocessor.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {

    private Long id;
    @NotNull
    private String retailer;
    @NotNull
    private LocalDate purchaseDate;
    @NotNull
    private LocalTime purchaseTime;
    @NotEmpty
    private List<ItemDTO> items;
    @NotNull
    private BigDecimal total;

    public Receipt toReceipt() {
        List<Item> itemList = items.stream().map(ItemDTO::toItem).toList();

        return new Receipt(null, retailer, purchaseDate, purchaseTime, itemList, total);
    }
}
