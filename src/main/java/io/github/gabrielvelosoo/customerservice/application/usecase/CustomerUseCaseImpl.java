package io.github.gabrielvelosoo.customerservice.application.usecase;

import io.github.gabrielvelosoo.customerservice.application.dto.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.CustomerMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.CustomerValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.customer.CustomerService;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerUseCaseImpl implements CustomerUseCase {

    private final CustomerService customerService;
    private final IdentityProvider identityProvider;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;

    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        customerValidator.validateOnCreate(customer);
        String keycloakUserId = identityProvider.createUser(
                customerRequestDTO.email(),
                customerRequestDTO.password(),
                customerRequestDTO.name(),
                customerRequestDTO.lastName()
        );
        customer.setKeycloakUserId(keycloakUserId);
        Customer savedCustomer = customerService.create(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO edit(Long id, CustomerUpdateDTO customerUpdateDTO) {
        customerValidator.validateOnUpdate(id, customerUpdateDTO);
        Customer customer = customerService.findById(id);
        customerMapper.edit(customer, customerUpdateDTO);
        identityProvider.editUser(
                customer.getKeycloakUserId(),
                customerUpdateDTO.name(),
                customerUpdateDTO.lastName()
        );
        Customer editedCustomer = customerService.edit(customer);
        return customerMapper.toDTO(editedCustomer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerService.findById(id);
        identityProvider.deleteUser(customer.getKeycloakUserId());
        customerService.delete(customer);
    }
}
