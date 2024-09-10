import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @Test
    void testTransferMoney() {
        Account accountFrom = new Account(1L, 100.0);
        Account accountTo = new Account(2L, 50.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        TransferRequest request = new TransferRequest();
        request.setAccountFromId(1L);
        request.setAccountToId(2L);
        request.setAmount(25.0);

        accountService.transferMoney(request);

        assertEquals(75.0, accountFrom.getBalance());
        assertEquals(75.0, accountTo.getBalance());

        verify(accountRepository).save(accountFrom);
        verify(accountRepository).save(accountTo);
        verify(notificationService).sendNotification(1L, 2L, 25.0);
        verify(notificationService).sendNotification(2L, 1L, 25.0);
    }

    @Test
    void testTransferMoneyInsufficientFunds() {
        Account accountFrom = new Account(1L, 10.0);
        Account accountTo = new Account(2L, 50.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        TransferRequest request = new TransferRequest();
        request.setAccountFromId(1L);
        request.setAccountToId(2L);
        request.setAmount(25.0);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            accountService.transferMoney(request);
        });

        assertEquals("Insufficient funds.", thrown.getMessage());
    }
}
