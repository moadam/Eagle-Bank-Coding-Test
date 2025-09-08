package com.eaglebank.api.controller;

import com.eaglebank.api.dto.BankAccountRequest;
import com.eaglebank.api.dto.BankAccountResponse;
import com.eaglebank.api.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@RequestBody BankAccountRequest request,
                                                             Authentication auth) {
        BankAccountResponse response = bankAccountService.createAccount(request, auth.getName());
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAccounts(Authentication auth) {
        return ResponseEntity.ok(bankAccountService.getAccounts(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getAccount(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(bankAccountService.getAccount(id, auth.getName()));
    }


}
