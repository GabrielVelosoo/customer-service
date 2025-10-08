package io.github.gabrielvelosoo.customerservice.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.address.AddressService;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressUseCaseImpl implements AddressUseCase {

    private static final Logger logger = LogManager.getLogger(AddressUseCaseImpl.class);

    private final AddressService addressService;
    private final AuthService authService;
    private final AddressMapper addressMapper;
    private final AddressValidator addressValidator;

    @Override
    @Transactional
    public AddressResponseDTO create(AddressRequestDTO addressRequestDTO) {
        logger.debug("Creating new address");
        logger.debug("Mapping AddressRequestDTO to entity");
        Address address = addressMapper.toEntity(addressRequestDTO);
        logger.debug("Getting logged customer to associate with address");
        Customer customer = authService.getLoggedCustomer();
        logger.debug("Setting logged customer '{}' to created address", customer.getId());
        address.setCustomer(customer);
        Address savedAddress = addressService.save(address);
        logger.info("Address persisted successfully with id '{}'", savedAddress.getId());
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesLoggedCustomer() {
        logger.debug("Getting addresses of logged customer");
        logger.debug("Getting logged customer");
        Customer customer = authService.getLoggedCustomer();
        List<Address> addresses = addressService.getAddressesLoggedCustomer(customer.getId());
        logger.info("Successfully get addresses for customer with id '{}'", customer.getId());
        return addressMapper.toDTOs(addresses);
    }

    @Override
    @Transactional
    public AddressResponseDTO edit(Long addressId, AddressRequestDTO addressRequestDTO) {
        logger.debug("Editing address with id '{}'", addressId);
        logger.debug("Searching for address to edit with id '{}'", addressId);
        Address address = addressService.findById(addressId);
        logger.debug("Validating update for address id: '{}'", addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressMapper.edit(address, addressRequestDTO);
        Address editedAddress = addressService.save(address);
        logger.info("Address id '{}' edited successfully", editedAddress.getId());
        return addressMapper.toDTO(editedAddress);
    }

    @Override
    @Transactional
    public void delete(Long addressId) {
        logger.debug("Deleting address with id '{}'", addressId);
        logger.debug("Searching for address to delete with id '{}'", addressId);
        Address address = addressService.findById(addressId);
        logger.debug("Validating delete for address id: '{}'", addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressService.delete(address);
        logger.info("Address id '{}' deleted successfully", addressId);
    }
}
