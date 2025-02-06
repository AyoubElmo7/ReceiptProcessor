package org.assessment.receiptprocessor.integretion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assessment.receiptprocessor.entities.Receipt;
import org.assessment.receiptprocessor.entities.ReceiptDTO;
import org.assessment.receiptprocessor.services.ReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReceiptIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReceiptService receiptService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private ReceiptDTO loadReceiptDTO() throws IOException {
        Resource resource = new ClassPathResource("mocks/ReceiptDTOMock.json");
        return objectMapper.readValue(resource.getInputStream(), ReceiptDTO.class);
    }

    @Test
    void processReceipt_returnsId() throws Exception {
        ReceiptDTO receiptDTO = loadReceiptDTO();

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiptDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidReceipts")
    void processReceipt_returnsBadRequest(String testName, ReceiptDTO receiptDTO) throws Exception {
        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiptDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPoints_returnsPoints() throws Exception {
        Receipt receipt = loadReceiptDTO().toReceipt();
        Map<String, Long> id = receiptService.saveReceipt(receipt);

        mockMvc.perform(get("/receipts/{id}/points", id.get("id"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.points").value(28));
    }

    @Test
    void getPoints_returnsNotFound() throws Exception {
        mockMvc.perform(get("/receipts/{id}/points", Long.valueOf("12345"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    static Stream<Arguments> invalidReceipts() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Resource resource = new ClassPathResource("mocks/ReceiptDTOMock.json");
        ReceiptDTO receiptDTO = objectMapper.readValue(resource.getInputStream(), ReceiptDTO.class);

        return Stream.of(
                Arguments.of("Missing Retailer",
                        new ReceiptDTO(null, null, receiptDTO.getPurchaseDate(), receiptDTO.getPurchaseTime(), receiptDTO.getItems(), receiptDTO.getTotal())),
                Arguments.of("Missing Purchase Date",
                        new ReceiptDTO(null, receiptDTO.getRetailer(), null, receiptDTO.getPurchaseTime(), receiptDTO.getItems(), receiptDTO.getTotal())),
                Arguments.of("Missing Purchase Date",
                        new ReceiptDTO(null, receiptDTO.getRetailer(), receiptDTO.getPurchaseDate(), null, receiptDTO.getItems(), receiptDTO.getTotal())),
                Arguments.of("Missing Item List",
                        new ReceiptDTO(null, receiptDTO.getRetailer(), receiptDTO.getPurchaseDate(), receiptDTO.getPurchaseTime(), null, receiptDTO.getTotal())),
                Arguments.of("Missing Total Price",
                        new ReceiptDTO(null, receiptDTO.getRetailer(), receiptDTO.getPurchaseDate(), receiptDTO.getPurchaseTime(), receiptDTO.getItems(), null))
        );
    }
}
