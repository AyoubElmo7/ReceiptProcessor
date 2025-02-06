package org.assessment.receiptprocessor.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assessment.receiptprocessor.entities.Receipt;
import org.assessment.receiptprocessor.exceptions.ReceiptNotFound;
import org.assessment.receiptprocessor.repositories.ReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Receipt loadReceipt() throws IOException {
        Resource resource = new ClassPathResource("mocks/ReceiptMock.json");
        return objectMapper.readValue(resource.getInputStream(), Receipt.class);
    }

    @Test
    void saveReceipt_returnsReceiptId() throws IOException {
        Receipt receipt = loadReceipt();

        when(receiptRepository.save(receipt)).thenReturn(receipt);

        Map<String, Long> actualResult = receiptService.saveReceipt(receipt);
        Map<String, Long> expectedResult = Collections.singletonMap("id", receipt.getId());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculatePointsFromReceipt_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        when(receiptRepository.findById(receipt.getId())).thenReturn(Optional.of(receipt));

        Map<String, Integer> actualResult = receiptService.calculatePointsFromReceipt(receipt.getId());
        Map<String, Integer> expectedResult = Collections.singletonMap("points", 28);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculatePointsFromReceipt_throwsReceiptNotFound() {
        assertThatThrownBy(() -> receiptService.calculatePointsFromReceipt(Long.valueOf("12345")))
                .isInstanceOf(ReceiptNotFound.class);
    }

    @Test
    void getPointsFromRetailer_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromRetailer(receipt.getRetailer());

        assertEquals(6, actualResult);
    }

    @Test
    void getPointsFromTotal_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromTotal(receipt.getTotal());

        assertEquals(0, actualResult);
    }

    @Test
    void getPointsFromItemList_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromItemList(receipt.getItems());

        assertEquals(10, actualResult);
    }

    @Test
    void getPointsFromItemDescription_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromItemDescriptions(receipt.getItems());

        assertEquals(6, actualResult);
    }

    @Test
    void getPointsFromDate_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromDate(receipt.getPurchaseDate());

        assertEquals(6, actualResult);
    }

    @Test
    void getPointsFromTime_returnsPoints() throws IOException {
        Receipt receipt = loadReceipt();

        int actualResult = receiptService.getPointsFromTime(receipt.getPurchaseTime());

        assertEquals(0, actualResult);
    }
}
