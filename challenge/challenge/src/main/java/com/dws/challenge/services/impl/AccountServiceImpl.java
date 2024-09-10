package com.dws.challenge.services.impl;

import com.dws.challenge.model.TransferRequest;
import com.dws.challenge.model.Account;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private final NotificationService notificationService;

	private static final Logger LOGGER = Loggerfactory.getLogger(AccountServiceImpl.class);

	/**
	 * Transfers money from one account to another.
	 * 
	 * This method performs a money transfer by deducting the specified amount from
	 * the source account and adding it to the destination account. It ensures that
	 * the operation is atomic and that no account ends up with a negative balance.
	 */
	@Transactional
	public void transferMoney(TransferRequest request) {
		if (request.getAmount() <= 0) {
			LOGGER.error("Amount must be positive");
			throw new IllegalArgumentException("Amount must be positive.");
		}

		Long accountFromId = request.getAccountFromId();
		Long accountToId = request.getAccountToId();
		double amount = request.getAmount();

		if (accountFromId.equals(accountToId)) {
			LOGGER.error("Cannot transfer money to the same account.");
			throw new IllegalArgumentException("Cannot transfer money to the same account.");
		}

		Account accountFrom;
		Account accountTo;

		synchronized (this) {
			// Retrieve accounts in a synchronized block
			accountFrom = accountRepository.findById(accountFromId)
					.orElseThrow(() -> new RuntimeException("Account not found: " + accountFromId));
			accountTo = accountRepository.findById(accountToId)
					.orElseThrow(() -> new RuntimeException("Account not found: " + accountToId));

			if (accountFrom.getBalance() < amount) {
				LOGGER.error("Insufficient funds.");
				throw new RuntimeException("Insufficient funds.");
			}

			// Perform the balance update within the synchronized block
			accountFrom.setBalance(accountFrom.getBalance() - amount);
			accountTo.setBalance(accountTo.getBalance() + amount);

			accountRepository.save(accountFrom);
			accountRepository.save(accountTo);
		}

		// TODO: Notify account holders
		sendNotifications(accountFrom, accountTo, amount);
	}

	private void sendNotifications(Account accountFrom, Account accountTo, int amount) {
		notificationService.sendNotification(accountFrom.getId(),
				String.format("Transferred {} to account {}", amount, accountTo.getId()));
		notificationService.sendNotification(accountTo.getId(),
				String.format("Received {} from account {}", amount, accountFrom.getId()));
	}
}
