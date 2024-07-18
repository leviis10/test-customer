package enigma.customer.service;

import enigma.customer.dto.TransactionDTO;
import enigma.customer.model.Customer;
import enigma.customer.model.Transaction;
import enigma.customer.repository.TransactionRepository;
import enigma.customer.service.implementation.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private List<Transaction> transactions;
    private TransactionDTO transactionDTO;
    private Customer customer;
    private Transaction transaction;

    @BeforeEach
    public void beforeEach() {
//        MockitoAnnotations.openMocks(this);
        transactions = mock(List.class);
        transactionDTO = TransactionDTO.builder()
                .customerId(1L)
                .productName("product")
                .quantity(1)
                .price(1)
                .build();
        customer = mock(Customer.class);
        transaction = mock(Transaction.class);
    }

    @Test
    void getAllTransaction() {
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> foundTransactions = transactionService.getAll();

        assertEquals(transactions, foundTransactions);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getTransactionById() {
        when(transactionRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(transaction));

        Transaction foundTransaction = transactionService.getById(1L);

        assertEquals(transaction, foundTransaction);
        verify(transactionRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void createTransaction_success() {
        when(customerService.getById(any(Long.class))).thenReturn(customer);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction createdTransaction = transactionService.create(transactionDTO);

        assertEquals(transaction, createdTransaction);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_fail() {
        Long nonExistentId = 999L;
        when(customerService.getById(nonExistentId))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> transactionService.create(transactionDTO))
                .isInstanceOf(RuntimeException.class);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_success() {
        when(transactionRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        Transaction updatedTransaction = transactionService.updateById(1L, transactionDTO);

        assertEquals(transaction, updatedTransaction);
        verify(transactionRepository, times(1)).findById(any(Long.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_success() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction))
                .thenReturn(Optional.empty());
        doNothing().when(transactionRepository).delete(transaction);

        transactionService.deleteById(1L);
        Optional<Transaction> found = transactionRepository.findById(1L);

        assertThat(found).isEmpty();
        verify(transactionRepository, times(2)).findById(1L);
        verify(transactionRepository, times(1)).delete(transaction);
    }
}
