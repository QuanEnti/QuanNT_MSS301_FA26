package com.fudn.customerservice.dto;

import com.fudn.customerservice.model.Customer;
import com.fudn.customerservice.model.CustomerStatus;

import java.time.LocalDate;

public record CustomerResponse(
        Long customerId,
        String customerName,
        String telephone,
        String email,
        LocalDate customerBirthday,
        CustomerStatus customerStatus) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getTelephone(),
                customer.getEmail(),
                customer.getCustomerBirthday(),
                customer.getCustomerStatus());
    }
}
