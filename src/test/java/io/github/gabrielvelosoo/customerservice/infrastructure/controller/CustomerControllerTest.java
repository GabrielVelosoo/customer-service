package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    MockMvc mockMvc;

    @Mock
    CustomerUseCase customerUseCase;

    @InjectMocks
    CustomerController customerController;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    CustomerRequestDTO customerRequestDTO;
    CustomerResponseDTO customerResponseDTO;
    CustomerUpdateDTO customerUpdateDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .alwaysDo(print())
                .build();

        customerRequestDTO = new CustomerRequestDTO(
                "Gabriel",
                "Veloso",
                "abcd@example.com",
                "Test@123",
                "85656417085",
                "76964604",
                LocalDate.of(1990, 1, 1)
        );

        customerResponseDTO = new CustomerResponseDTO(
                1L,
                "Gabriel",
                "Veloso",
                "abcd@example.com",
                "85656417085",
                "76964604",
                LocalDate.of(1990, 1, 1)
        );

        customerUpdateDTO = new CustomerUpdateDTO(
                "Guilherme",
                "Veloso",
                "83303677034",
                LocalDate.of(2007, 5, 22)
        );
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        when(customerUseCase.create(customerRequestDTO)).thenReturn(customerResponseDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gabriel"))
                .andExpect(jsonPath("$.lastName").value("Veloso"));

        verify(customerUseCase, times(1)).create(customerRequestDTO);
    }

    @Test
    void shouldEditCustomerSuccessfully() throws Exception {
        CustomerResponseDTO updatedDTO = new CustomerResponseDTO(
                1L,
                "Guilherme",
                "Veloso",
                "abcd@example.com",
                "83303677034",
                "76964604",
                LocalDate.of(2007, 5, 22)
        );
        when(customerUseCase.edit(1L, customerUpdateDTO)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Guilherme"))
                .andExpect(jsonPath("$.lastName").value("Veloso"));

        verify(customerUseCase, times(1)).edit(1L, customerUpdateDTO);
    }

    @Test
    void shouldDeleteCustomerSuccessfully() throws Exception {
        doNothing().when(customerUseCase).delete(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerUseCase, times(1)).delete(1L);
    }
}
