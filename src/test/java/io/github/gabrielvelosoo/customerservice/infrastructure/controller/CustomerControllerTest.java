package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.CustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomerUseCase customerUseCase;

    CustomerRequestDTO customerRequestDTO;
    CustomerResponseDTO customerResponseDTO;

    @BeforeEach
    void setUp() {
        customerRequestDTO = new CustomerRequestDTO(
                "XXXXX",
                "YYYYY",
                "abcd@example.com",
                "abcd",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 1, 1)
        );

        customerResponseDTO = new CustomerResponseDTO(
                1L,
                "XXXXX",
                "YYYYY",
                "abcd@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        when(customerUseCase.create(customerRequestDTO)).thenReturn(customerResponseDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("XXXXX"))
                .andExpect(jsonPath("$.lastName").value("YYYYY"));
    }
}
