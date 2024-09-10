import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferMoneySuccess() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .accountFromId(1L)
                .accountToId(2L)
                .amount(50.0)
                .build();

        doNothing().when(accountService).transferMoney(request);

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));
    }

    @Test
    void testTransferMoneyInvalidAmount() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .accountFromId(1L)
                .accountToId(2L)
                .amount(-10.0)
                .build();

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be positive."));
    }

    @Test
    void testTransferMoneyInsufficientFunds() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .accountFromId(1L)
                .accountToId(2L)
                .amount(200.0)
                .build();

        doThrow(new RuntimeException("Insufficient funds.")).when(accountService).transferMoney(request);

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Insufficient funds."));
    }

    @Test
    void testTransferMoneySameAccount() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .accountFromId(1L)
                .accountToId(1L)
                .amount(50.0)
                .build();

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot transfer money to the same account."));
    }
}
