package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    MockMvc mockMvc;

    @Mock
    AddressUseCase addressUseCase;

    @InjectMocks
    AddressController addressController;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    AddressRequestDTO addressRequestDTO;
    AddressResponseDTO addressResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .alwaysDo(print())
                .build();

        addressRequestDTO = new AddressRequestDTO(
                "unit",
                "test",
                "31984537263",
                "Quadra SGAN 606 Módulo F",
                "11",
                "Asa Norte",
                "Brasília",
                "Distrito Federal",
                "70830256",
                ""
        );

        addressResponseDTO = new AddressResponseDTO(
                1L,
                "unit",
                "test",
                "31984537263",
                "Quadra SGAN 606 Módulo F",
                "11",
                "Asa Norte",
                "Brasília",
                "Distrito Federal",
                "70830256",
                ""
        );
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateAddressSuccessfully() throws Exception {
            when(addressUseCase.create(addressRequestDTO)).thenReturn(addressResponseDTO);

            mockMvc.perform(post("/api/v1/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addressRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.contactName").value("unit"))
                    .andExpect(jsonPath("$.contactLastName").value("test"));

            verify(addressUseCase, times(1)).create(addressRequestDTO);
        }
    }

    @Nested
    class GetAddressesTests {

        @Test
        void shouldGetAddressesLoggedCustomerSuccessfully() throws Exception {
            when(addressUseCase.getAddressesLoggedCustomer()).thenReturn(Collections.singletonList(addressResponseDTO));

            mockMvc.perform(get("/api/v1/addresses")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].contactName").value("unit"))
                    .andExpect(jsonPath("$[0].contactLastName").value("test"))
                    .andExpect(jsonPath("$[0].contactPhone").value("31984537263"))
                    .andExpect(jsonPath("$[0].address").value("Quadra SGAN 606 Módulo F"))
                    .andExpect(jsonPath("$[0].number").value("11"))
                    .andExpect(jsonPath("$[0].neighborhood").value("Asa Norte"))
                    .andExpect(jsonPath("$[0].city").value("Brasília"))
                    .andExpect(jsonPath("$[0].state").value("Distrito Federal"))
                    .andExpect(jsonPath("$[0].cep").value("70830256"))
                    .andExpect(jsonPath("$[0].complement").value(""));

            verify(addressUseCase, times(1)).getAddressesLoggedCustomer();
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldEditAddressSuccessfully() throws Exception {
            when(addressUseCase.edit(1L, addressRequestDTO)).thenReturn(addressResponseDTO);

            mockMvc.perform(put("/api/v1/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addressRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.contactName").value("unit"))
                    .andExpect(jsonPath("$.contactLastName").value("test"));

            verify(addressUseCase, times(1)).edit(1L, addressRequestDTO);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteAddressSuccessfully() throws Exception {
            doNothing().when(addressUseCase).delete(1L);

            mockMvc.perform(delete("/api/v1/addresses/1"))
                    .andExpect(status().isNoContent());

            verify(addressUseCase, times(1)).delete(1L);
        }
    }
}