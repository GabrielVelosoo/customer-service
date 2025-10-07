package io.github.gabrielvelosoo.customerservice.domain.service.address;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;

import java.util.List;

public interface AddressService {

    Address save(Address address);
    Address findById(Long addressId);
    List<Address> getAddressesLoggedCustomer(Long customerId);
    void delete(Address address);
}
