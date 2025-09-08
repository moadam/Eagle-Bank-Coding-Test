package com.eaglebank.api.service;

import com.eaglebank.api.dto.BankAccountRequest;
import com.eaglebank.api.dto.BankAccountResponse;
import com.eaglebank.api.exception.*;
import com.eaglebank.api.model.BankAccount;
import com.eaglebank.api.model.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public BankAccountResponse createAccount(BankAccountRequest request, String currentUserEmail) {
        if (request.getAccountType() == null) {
            throw new BadRequestException("Account type is required");
        }

        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(UserNotFoundException::new);

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountType(request.getAccountType());
        account.setBalance(0.0);

        bankAccountRepository.save(account);

        return new BankAccountResponse(account.getId(), account.getAccountType(), account.getBalance());
    }

    public List<BankAccountResponse> getAccounts(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(UserNotFoundException::new);

        return bankAccountRepository.findByUserId(user.getId())
                .stream()
                .map(a -> new BankAccountResponse(a.getId(), a.getAccountType(), a.getBalance()))
                .collect(Collectors.toList());
    }

    public BankAccountResponse getAccount(String accountId, String currentUserEmail) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("Cannot access another user's account");
        }

        return new BankAccountResponse(account.getId(), account.getAccountType(), account.getBalance());
    }
}
