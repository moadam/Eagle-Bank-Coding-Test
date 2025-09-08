package com.eaglebank.api.controller;

import com.eaglebank.api.dto.TransactionRequest;
import com.eaglebank.api.dto.TransactionResponse;
import com.eaglebank.api.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountId,
                                                                 @RequestBody TransactionRequest request,
                                                                 Authentication auth) {
        TransactionResponse response = transactionService.createTransaction(accountId, auth.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(@PathVariable String accountId,
                                                                      Authentication auth) {
        return ResponseEntity.ok(transactionService.listTransactions(accountId, auth.getName()));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String accountId,
                                                              @PathVariable String transactionId,
                                                              Authentication auth) {
        return ResponseEntity.ok(transactionService.getTransaction(accountId, transactionId, auth.getName()));
    }
}
