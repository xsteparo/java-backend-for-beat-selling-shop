package com.cvut.cz.fel.ear.instumentalshop.service.impl;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Role;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.BalanceMapper;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.CustomerMapper;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import com.cvut.cz.fel.ear.instumentalshop.repository.CustomerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.UserRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import com.cvut.cz.fel.ear.instumentalshop.service.CustomerService;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final AuthenticationService authenticationService;

    private final CustomerRepository customerRepository;

    private final UserRepository userRepository;

    private final CustomerMapper customerMapper;

    private final BalanceMapper balanceMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;

    @Override
    @Transactional
    public UserDto register(UserCreationRequestDto requestDto) {
        userValidator.validateUserCreationRequest(userRepository, requestDto.getUsername());

        Customer customer = buildCustomer(requestDto);

        customer = customerRepository.save(customer);

        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public BalanceResponseDto increaseBalance(IncreaseBalanceRequestDto requestDto) {
        Customer customer = authenticationService.getRequestingCustomerFromSecurityContext();

        customer.setBalance(customer.getBalance().add(requestDto.getBalanceRecharge()));

        customerRepository.save(customer);

        return balanceMapper.toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponseDto getBalance() {
        Customer customer = authenticationService.getRequestingCustomerFromSecurityContext();

        return balanceMapper.toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer with id " + id + " does not exist"));
        return customerMapper.toGetResponseDto(customer);
    }

    @Override
    @Transactional
    public UserDto updateCustomer(UserUpdateRequestDto requestDto) {
        Customer customer = authenticationService.getRequestingCustomerFromSecurityContext();

        updateCustomerEntity(customer, requestDto);

        customerRepository.save(customer);

        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer() {
        Customer customer = authenticationService.getRequestingCustomerFromSecurityContext();

        userValidator.validateCustomerDeletionRequest(customer);

        customerRepository.delete(customer);
    }

    private void updateCustomerEntity(Customer customer, UserUpdateRequestDto requestDto) {
        if (requestDto.getUsername() != null) {
            customer.setUsername(requestDto.getUsername());
        }
        if (requestDto.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
    }


    private Customer buildCustomer(UserCreationRequestDto requestDto) {
        return Customer.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .registrationDate(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .role(Role.CUSTOMER)
                .build();
    }

}

