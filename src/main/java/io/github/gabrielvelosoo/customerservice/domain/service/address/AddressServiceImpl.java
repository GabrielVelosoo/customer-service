package io.github.gabrielvelosoo.customerservice.domain.service.address;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address create(Address address) {
        return addressRepository.save(address);
    }
}
