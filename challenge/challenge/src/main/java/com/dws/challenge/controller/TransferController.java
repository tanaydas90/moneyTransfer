/**
 * DB Copyright
 */

package com.dws.challenge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling money transfers between accounts.
 * 
 * Handles HTTP requests to transfer money from one account to another.
 * Validates requests and performs transactions while ensuring notifications are
 * sent.
 * 
 * @author tanaydas
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class TransferController {

	private static final Logger LOGGER = Loggerfactory.getLogger(TransferController.class);

	private final AccountService accountService;

	/**
	 * Handles the transfer of money between accounts.
	 * 
	 * This endpoint allows for the transfer of a specified amount of money from one
	 * account to another. The request must include the source account ID,
	 * destination account ID, and the amount to be transferred. The method performs
	 * validation to ensure that the amount is positive, and the source and
	 * destination accounts are different. If the transfer is successful,
	 * notifications are sent to both account holders.
	 * 
	 * In case of invalid input, such as a negative amount or identical account IDs,
	 * the method will return a bad request response with an appropriate error
	 * message. If an account does not exist or if there are insufficient funds in
	 * the source account, a runtime exception will be thrown, resulting in an
	 * internal server error response.
	 * 
	 * @param transferRequest The request body containing details of the transfer,
	 *                        including accountFromId, accountToId, amount.
	 *
	 */
	@PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest transferRequest) {
        try {
        	//if checkmarx or equivalent tool is there, we may need to sanitize the request object
            accountService.transferMoney(transferRequest);
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
