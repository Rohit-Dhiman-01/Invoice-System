package com.invoice.system.service.impl;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.DuplicateException;
import com.invoice.system.dto.CustomerDto;
import com.invoice.system.model.CustomerEntity;
import com.invoice.system.repository.CustomerRepository;
import com.invoice.system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerEntity createCustomer(CustomerDto customerDto) {
        if (customerRepository.existsByEmail(customerDto.getEmail()))
            throw new DuplicateException("Email already exists");

        if (customerRepository.existsByPhone(customerDto.getPhone()))
            throw new DuplicateException("Phone Number already exists");
        
        CustomerEntity entity =
                new CustomerEntity(
                        null,
                        customerDto.getCustomerName(),
                        customerDto.getEmail(),
                        customerDto.getPhone(),
                        customerDto.getGstNumber(),
                        customerDto.getBillingAddress(),
                        customerDto.getShippingAddress(),
                        null);
        return customerRepository.save(entity);
    }

    @Override
    public List<CustomerEntity> getAllCustomer() {

        return customerRepository.findAll();
    }

    @Override
    public CustomerEntity getCustomerByID(Long id) {
        Optional<CustomerEntity> optionalCustomerEntity = customerRepository.findById(id);
        if (optionalCustomerEntity.isEmpty()) {
            throw new CustomerNotFoundException("Customer Not Found");
        }
        return optionalCustomerEntity.get();
    }
}
