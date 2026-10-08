package com.fudn.customerservice.controller;

import com.fudn.customerservice.dto.AdminCustomerRequest;
import com.fudn.customerservice.dto.ChangePasswordRequest;
import com.fudn.customerservice.dto.CustomerResponse;
import com.fudn.customerservice.dto.ProfileUpdateRequest;
import com.fudn.customerservice.dto.RegisterRequest;
import com.fudn.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private static final String USER_ID = "X-User-Id";

    private final CustomerService customerService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse register(@Valid @RequestBody RegisterRequest request) {
        return customerService.register(request);
    }

    @GetMapping("/me")
    public CustomerResponse getProfile(@RequestHeader(USER_ID) Long userId) {
        return customerService.getProfile(userId);
    }

    @PutMapping("/me")
    public CustomerResponse updateProfile(@RequestHeader(USER_ID) Long userId,
                                          @Valid @RequestBody ProfileUpdateRequest request) {
        return customerService.updateProfile(userId, request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestHeader(USER_ID) Long userId,
                               @Valid @RequestBody ChangePasswordRequest request) {
        customerService.changePassword(userId, request);
    }

    @GetMapping
    public List<CustomerResponse> search(@RequestParam(required = false) String keyword) {
        return customerService.search(keyword);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody AdminCustomerRequest request) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id,
                                   @Valid @RequestBody AdminCustomerRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}
