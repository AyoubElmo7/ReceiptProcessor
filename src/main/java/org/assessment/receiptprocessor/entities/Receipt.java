package org.assessment.receiptprocessor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String retailer;
    private LocalDate purchaseDate;
    private LocalTime purchaseTime;
    @ElementCollection
    private List<Item> items;
    private BigDecimal total;

    public ReceiptDTO toReceiptDTO() {
        List<ItemDTO> itemDTOs = items.stream()
                .map(item -> new ItemDTO(item.getShortDescription(), item.getPrice()))
                .toList();

        return new ReceiptDTO(id, retailer, purchaseDate, purchaseTime, itemDTOs , total);
    }
}
