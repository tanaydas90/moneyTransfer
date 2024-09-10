package com.dws.challenge.services.impl;

import com.dws.challenge.model.TransferRequest;
import com.dws.challenge.model.Account;

@Service
public class AccountServiceImpl implements AccountService {


    @Transactional
    public void transferMoney(TransferRequest request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        Long accountFromId = request.getAccountFromId();
        Long accountToId = request.getAccountToId();
        double amount = request.getAmount();

        if (accountFromId.equals(accountToId)) {
            throw new IllegalArgumentException("Cannot transfer money to the same account.");
        }

        Account accountFrom = accountRepository.findById(accountFromId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountFromId));
        Account accountTo = accountRepository.findById(accountToId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountToId));

        if (accountFrom.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds.");
        }

        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);

        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);

        // TODO: Notify both account holders
        notificationService.sendNotification(accountFromId, accountToId, amount);
        notificationService.sendNotification(accountToId, accountFromId, amount);
    }
}
