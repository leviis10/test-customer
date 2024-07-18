package enigma.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import enigma.customer.dto.TransactionDTO;
import enigma.customer.model.Customer;
import enigma.customer.model.Transaction;
import enigma.customer.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private Customer customer;

    @BeforeEach
    void setup() {
        customer = Customer.builder()
                .id(1L)
                .name("Customer 1")
                .birthDate(LocalDate.now())
                .build();
        transaction = Transaction.builder()
                .id(1L)
                .customer(customer)
                .price(100)
                .quantity(10)
                .productName("Product 1")
                .build();
        transactionDTO = TransactionDTO.builder()
                .price(100)
                .quantity(10)
                .productName("Product 1")
                .customerId(customer.getId())
                .build();
    }

    @Test
    void createTransaction() throws Exception {
        when(transactionService.create(any(TransactionDTO.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.productName").value(transaction.getProductName()))
                .andExpect(jsonPath("$.quantity").value(transaction.getQuantity()))
                .andExpect(jsonPath("$.price").value(transaction.getPrice()));

        verify(transactionService).create(any(TransactionDTO.class));
    }

    @Test
    void getAllTransactions() throws Exception {
        List<Transaction> transactions = List.of(transaction, transaction);
        when(transactionService.getAll()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(transactionService).getAll();
    }

    @Test
    void getTransactionById() throws Exception {
        when(transactionService.getById(transaction.getId()))
                .thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()));

        verify(transactionService).getById(transaction.getId());
    }

    @Test
    void deleteTransactionById() throws Exception {
        doNothing().when(transactionService).deleteById(transaction.getId());

        mockMvc.perform(delete("/api/transactions/{id}", transaction.getId()))
                .andExpect(status().isOk());
        verify(transactionService).deleteById(transaction.getId());
    }
}
