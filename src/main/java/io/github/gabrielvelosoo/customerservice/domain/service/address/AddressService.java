package io.github.gabrielvelosoo.customerservice.domain.service.address;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;

import java.util.List;

public interface AddressService {

    Address create(Address address);
    Address findById(Long addressId);
    List<Address> getAddressesLoggedCustomer(Long customerId);
    Address edit(Address address);
    void delete(Address address);
}
