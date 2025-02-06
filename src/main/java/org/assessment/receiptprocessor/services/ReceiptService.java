package org.assessment.receiptprocessor.services;

import org.assessment.receiptprocessor.entities.Item;
import org.assessment.receiptprocessor.entities.Receipt;
import org.assessment.receiptprocessor.exceptions.ReceiptNotFound;
import org.assessment.receiptprocessor.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(final ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public Map<String, Long> saveReceipt(Receipt receipt) {
        Long id = receiptRepository.save(receipt).getId();

        return Collections.singletonMap("id", id);
    }

    public Map<String, Integer> calculatePointsFromReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id).orElseThrow(() -> new ReceiptNotFound(id));

        int totalPoints = getPointsFromRetailer(receipt.getRetailer());
        totalPoints += getPointsFromTotal(receipt.getTotal());
        totalPoints += getPointsFromItemList(receipt.getItems());
        totalPoints += getPointsFromItemDescriptions(receipt.getItems());
        totalPoints += getPointsFromDate(receipt.getPurchaseDate());
        totalPoints += getPointsFromTime(receipt.getPurchaseTime());

        return Collections.singletonMap("points", totalPoints);
    }

    public int getPointsFromRetailer(String retailer) {
        int count = 0;

        for (char c : retailer.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                count++;
            }
        }

        return count;
    }

    public int getPointsFromTotal(BigDecimal total) {
        int pointsFromTotal = total.stripTrailingZeros().scale() > 0 ? 0 : 50;
        pointsFromTotal += total.remainder(new BigDecimal("0.25")).compareTo(BigDecimal.ZERO) == 0 ? 25 : 0;

        return pointsFromTotal;
    }

    public int getPointsFromItemList(List<Item> items) {
        return (items.size() / 2) * 5;
    }

    public int getPointsFromItemDescriptions(List<Item> items) {
        int points = 0;

        for(Item item : items) {
            if (item.getShortDescription().trim().length() % 3 == 0) {
                points += item.getPrice().multiply(new BigDecimal("0.2")).setScale(0, RoundingMode.UP).intValue();
            }
        }

        return points;
    }

    public int getPointsFromDate(LocalDate date) {
        return date.getDayOfMonth() % 2 == 1 ? 6 : 0;
    }

    public int getPointsFromTime(LocalTime time) {
        return time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0)) ? 10 : 0;
    }
}
