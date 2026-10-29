package com.shoponline.customerService.service;

import com.shoponline.customerService.dto.CustomerDTO;
import com.shoponline.customerService.entity.Customer;
import com.shoponline.customerService.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setAddress("123 Main St");
        customer.setEmail("john@example.com");

        customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setAddress("123 Main St");
        customerDTO.setEmail("john@example.com");
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(customer.getId());

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        verify(customerRepository, times(1)).findById(customer.getId());
    }

    @Test
    void getCustomerById_NotFound() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.getCustomerById(id));
    }

    @Test
    void getAllCustomers_Success() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        List<CustomerDTO> results = customerService.getAllCustomers();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO updatedDTO = new CustomerDTO();
        updatedDTO.setName("Jane Doe");
        updatedDTO.setAddress("456 Elm St");
        updatedDTO.setEmail("jane@example.com");

        CustomerDTO result = customerService.updateCustomer(customer.getId(), updatedDTO);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_Success() {
        doNothing().when(customerRepository).deleteById(customer.getId());

        customerService.deleteCustomer(customer.getId());

        verify(customerRepository, times(1)).deleteById(customer.getId());
    }
}
