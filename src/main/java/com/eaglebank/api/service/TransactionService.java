package com.eaglebank.api.service;

import com.eaglebank.api.dto.TransactionRequest;
import com.eaglebank.api.dto.TransactionResponse;
import com.eaglebank.api.exception.*;
import com.eaglebank.api.model.BankAccount;
import com.eaglebank.api.model.Transaction;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public TransactionResponse createTransaction(String accountId, String currentUserEmail, TransactionRequest request) {
        if (request.getType() == null || request.getAmount() == null) {
            throw new BadRequestException("Transaction type and amount are required");
        }

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("Cannot create transaction on another user's account");
        }

        if (request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        if ("withdrawal".equalsIgnoreCase(request.getType()) && account.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException();
        }

        // Update balance
        if ("deposit".equalsIgnoreCase(request.getType())) {
            account.setBalance(account.getBalance() + request.getAmount());
        } else if ("withdrawal".equalsIgnoreCase(request.getType())) {
            account.setBalance(account.getBalance() - request.getAmount());
        } else {
            throw new BadRequestException("Invalid transaction type. Must be deposit or withdrawal.");
        }

        // Save transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(request.getType().toLowerCase());
        tx.setAmount(request.getAmount());
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);
        bankAccountRepository.save(account);

        return new TransactionResponse(tx.getId(), tx.getType(), tx.getAmount(), tx.getTimestamp());
    }

    public List<TransactionResponse> listTransactions(String accountId, String currentUserEmail) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("Cannot view another user's transactions");
        }

        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(t -> new TransactionResponse(t.getId(), t.getType(), t.getAmount(), t.getTimestamp()))
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransaction(String accountId, String transactionId, String currentUserEmail) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("Cannot view another user's transaction");
        }

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(TransactionNotFoundException::new);

        if (!tx.getAccount().getId().equals(accountId)) {
            throw new TransactionNotFoundException("Transaction does not belong to this account");
        }

        return new TransactionResponse(tx.getId(), tx.getType(), tx.getAmount(), tx.getTimestamp());
    }
}
