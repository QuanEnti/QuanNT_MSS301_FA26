package com.mss301.account.repository;

import com.mss301.account.model.StoreAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreAccountRepository extends JpaRepository<StoreAccount, Integer> {

    Optional<StoreAccount> findByUsernameIgnoreCase(String username);
}

