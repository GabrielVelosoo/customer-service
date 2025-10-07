package io.github.gabrielvelosoo.customerservice.domain.service.address;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.repository.AddressRepository;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LogManager.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Address save(Address address) {
        logger.debug("Saving address");
        return addressRepository.save(address);
    }

    @Override
    public Address findById(Long addressId) {
        logger.debug("Searching for address with id: '{}'", addressId);
        return addressRepository.findById(addressId)
                .orElseThrow( () -> {
                    logger.warn("Address not found with id: '{}'", addressId);
                    return new RecordNotFoundException("Address not found: " + addressId);
                } );
    }

    @Override
    public List<Address> getAddressesLoggedCustomer(Long customerId) {
        logger.debug("Searching for addresses of logged customer with id: '{}'", customerId);
        if(!customerRepository.existsById(customerId)) {
            logger.warn("Customer not found with id: '{}'", customerId);
            throw new RecordNotFoundException("Customer not found: " + customerId);
        }
        return addressRepository.findByCustomerId(customerId);
    }

    @Override
    public void delete(Address address) {
        logger.debug("Deleting address with id: '{}'", address.getId());
        addressRepository.delete(address);
    }
}
