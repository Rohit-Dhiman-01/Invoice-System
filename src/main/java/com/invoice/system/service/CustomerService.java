package com.invoice.system.service;

import com.invoice.system.config.exception.VendorNotFoundException;
import com.invoice.system.dto.CustomerDto;
import com.invoice.system.model.CustomerEntity;
import com.invoice.system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerEntity createCustomer(CustomerDto customerDto){
        CustomerEntity entity = new CustomerEntity(null,
                customerDto.getCustomerName(),
                customerDto.getEmail(),
                customerDto.getPhone(),
                customerDto.getGstNumber(),
                customerDto.getBillingAddress(),
                customerDto.getShippingAddress()
        );
        return customerRepository.save(entity);
    }
    public List<CustomerEntity> getAllCustomer(){

        return customerRepository.findAll();
    }
    public CustomerEntity getCustomerByID(Long id){
        Optional<CustomerEntity> optionalCustomerEntity = customerRepository.findById(id);
        if (optionalCustomerEntity.isEmpty()) {
            throw new VendorNotFoundException("Customer Not Found");
        }
        return optionalCustomerEntity.get();
    }
}
