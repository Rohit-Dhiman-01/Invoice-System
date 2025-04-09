package com.invoice.system.controller;

import com.invoice.system.dto.CustomerDto;
import com.invoice.system.model.CustomerEntity;
import com.invoice.system.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerEntity> createCustomer(
          @Valid @RequestBody CustomerDto customerDto
            ){
        return new ResponseEntity<>(customerService.createCustomer(customerDto), HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<List<CustomerEntity>> getCustomer(){
        return new ResponseEntity<>(customerService.getAllCustomer(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomerByID(
            @PathVariable Long id
    ){
        return new ResponseEntity<>(customerService.getCustomerByID(id),HttpStatus.OK);
    }
}
