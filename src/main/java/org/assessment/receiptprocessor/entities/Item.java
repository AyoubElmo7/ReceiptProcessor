package org.assessment.receiptprocessor.entities;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private String shortDescription;
    private BigDecimal price;

    public ItemDTO toItemDTO() {
        return new ItemDTO(shortDescription, price);
    }
}
