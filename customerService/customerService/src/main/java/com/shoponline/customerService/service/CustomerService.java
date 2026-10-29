package com.shoponline.customerService.service;

import com.shoponline.customerService.dto.CustomerDTO;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(UUID id);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO updateCustomer(UUID id, CustomerDTO customerDTO);
    void deleteCustomer(UUID id);
}
