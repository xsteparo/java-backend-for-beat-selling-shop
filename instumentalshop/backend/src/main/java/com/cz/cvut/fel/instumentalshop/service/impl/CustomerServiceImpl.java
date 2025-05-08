package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.CustomerMapper;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.CustomerService;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
    public UserDto register(UserCreationRequestDto dto) {
        // 1) Validate unique username
        userValidator.validateUserCreationRequest(
                userRepository, dto.getUsername()
        );

        // 2) Build Customer entity
        Customer customer = Customer.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .registrationDate(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .role(Role.CUSTOMER)
                .build();

        // 3) Handle avatar file if present
        MultipartFile avatar = dto.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            // generate a unique filename
            String ext = StringUtils.getFilenameExtension(avatar.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + ext;
            // choose your upload dir
            File dest = new File("uploads/avatars/" + filename);
            dest.getParentFile().mkdirs();
            try {
                avatar.transferTo(dest);
                // save relative URL or full path in entity
                customer.setAvatarUrl("/uploads/avatars/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save avatar", e);
            }
        }

        // 4) Persist
        Customer saved = customerRepository.save(customer);

        // 5) Map to DTO and return
        return customerMapper.toResponseDto(saved);
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

