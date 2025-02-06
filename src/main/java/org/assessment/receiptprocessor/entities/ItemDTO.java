package org.assessment.receiptprocessor.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private String shortDescription;
    @NotNull
    private BigDecimal price;

    public Item toItem() {
        return new Item(shortDescription, price);
    }
}
