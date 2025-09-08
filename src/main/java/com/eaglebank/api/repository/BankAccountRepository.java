package com.eaglebank.api.repository;

import com.eaglebank.api.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
