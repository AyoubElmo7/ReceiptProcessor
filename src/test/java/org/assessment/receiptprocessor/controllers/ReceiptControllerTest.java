package org.assessment.receiptprocessor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assessment.receiptprocessor.contollers.ReceiptController;
import org.assessment.receiptprocessor.entities.ReceiptDTO;
import org.assessment.receiptprocessor.services.ReceiptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiptController.class)
class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceiptService receiptService;

    @Autowired
    private ObjectMapper objectMapper;
    private final Long id = Long.valueOf("12345");

    private ReceiptDTO loadReceiptDTO() throws IOException {
        Resource resource = new ClassPathResource("mocks/ReceiptDTOMock.json");
        return objectMapper.readValue(resource.getInputStream(), ReceiptDTO.class);
    }

    @Test
    void processReceipt_ReturnsId() throws Exception {
        ReceiptDTO receiptDTO = loadReceiptDTO();

        when(receiptService.saveReceipt(receiptDTO.toReceipt())).thenReturn(Collections.singletonMap("id", id));

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiptDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getPoints_ReturnsPoints() throws Exception {
        when(receiptService.calculatePointsFromReceipt(id)).thenReturn(Collections.singletonMap("points", 10));

        mockMvc.perform(get("/receipts/{id}/points", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(10));
    }
}
