package com.backend.petshelter.controller;

import com.backend.petshelter.dto.AccountDTO;
import com.backend.petshelter.dto.AccountDetailsDTO;
import com.backend.petshelter.dto.PhonesDTO;
import com.backend.petshelter.model.Account;
import com.backend.petshelter.model.AccountDetails;
import com.backend.petshelter.model.Phones;
import com.backend.petshelter.security.AccountPrincipal;
import com.backend.petshelter.service.AccountService;
import com.backend.petshelter.util.enums.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PutMapping("change/{role}")
    public ResponseEntity<?> changeRol(@AuthenticationPrincipal AccountPrincipal accountPrincipal, @PathVariable Role role) {
        try {
            if (role == null) {
                throw new IllegalArgumentException("Invalid rol");
            }
            accountService.changeRole(role, accountPrincipal.getUsername());

            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping()
    public ResponseEntity<?> getCurrentAccount(@AuthenticationPrincipal AccountPrincipal accountPrincipal) {
        try {
            Account account = accountService.findByAccountReturnToken(accountPrincipal.getUsername());
            if (account != null && account.getAccountDetails() != null) {
                AccountDTO accountDTO = new AccountDTO();
                accountDTO.setUuid(account.getAccountUuid());
                accountDTO.setEmail(account.getEmail());
                accountDTO.setPassword(account.getPassword());
                accountDTO.setRol(account.getRol().toString());

                AccountDetails accountDetails = account.getAccountDetails();
                AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
                accountDetailsDTO.setFullName(accountDetails.getFullName());
                accountDetailsDTO.setIdentificationType(accountDetails.getIdentificationType());
                accountDetailsDTO.setIdentification(accountDetails.getIdentification());
                accountDetailsDTO.setCity(accountDetails.getCity());
                accountDetailsDTO.setAddress(accountDetails.getAddress());

                List<Phones> phonesList = accountDetails.getPhonesList();
                if (phonesList != null && !phonesList.isEmpty()) {
                    List<PhonesDTO> phonesDTOList = phonesList.stream()
                            .map(phones -> {
                                PhonesDTO phonesDTO = new PhonesDTO();
                                phonesDTO.setPhoneUuid(phones.getPhoneUuid());
                                phonesDTO.setPhoneLabel(phones.getPhoneLabel());
                                phonesDTO.setNumber(phones.getNumber());
                                phonesDTO.setCityCode(phones.getCityCode());
                                phonesDTO.setCountryCode(phones.getCountryCode());
                                return phonesDTO;
                            })
                            .collect(Collectors.toList());
                    accountDetailsDTO.setPhonesList(phonesDTOList);
                }

                accountDTO.setAccountDetails(accountDetailsDTO);
                accountDTO.setToken(account.getToken());

                return new ResponseEntity<>(accountDTO, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("updateAccount/{email}")
    public ResponseEntity<String> putUpdateAccount(@PathVariable String email, @Valid @RequestBody AccountDTO accountDTO) {
        try {
            accountDTO.setEmail(email);
            Account updatedAccount = accountService.updateAccount(accountDTO);
            if (updatedAccount != null) {
                return ResponseEntity.ok("Account updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update account");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the account" + e.getMessage());
        }
    }
}
