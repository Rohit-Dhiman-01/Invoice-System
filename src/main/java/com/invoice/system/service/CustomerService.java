package com.invoice.system.service;

import com.invoice.system.dto.CustomerDto;
import com.invoice.system.model.CustomerEntity;
import java.util.List;

public interface CustomerService {
  CustomerEntity createCustomer(CustomerDto customerDto);

  List<CustomerEntity> getAllCustomer();

  CustomerEntity getCustomerByID(Long id);
}
