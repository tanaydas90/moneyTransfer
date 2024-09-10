package com.dws.challenge.services;

import org.springframework.stereotype.Service;

@Service
public interface AccountService {

	 public void transferMoney(TransferRequest request);
}
