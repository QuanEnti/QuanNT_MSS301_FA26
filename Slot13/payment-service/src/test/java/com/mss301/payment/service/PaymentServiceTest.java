package com.mss301.payment.service;

import com.mss301.payment.dto.PaymentRequest;
import com.mss301.payment.entity.Payment;
import com.mss301.payment.entity.PaymentStatus;
import com.mss301.payment.entity.Wallet;
import com.mss301.payment.repository.PaymentRepository;
import com.mss301.payment.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService(walletRepository, paymentRepository);
    }

    @Test
    void payDoesNotChargeTwiceForTheSameOrder() {
        Payment existing = payment(20L, "C1", 1000, PaymentStatus.PAID);
        when(paymentRepository.findById(20L)).thenReturn(Optional.of(existing));

        Payment result = service.pay(new PaymentRequest(20L, "C1", 1000));

        assertThat(result).isSameAs(existing);
        verify(walletRepository, never()).findByCustomerIdForUpdate(any());
    }

    @Test
    void payReturns402WhenBalanceIsInsufficient() {
        when(paymentRepository.findById(21L)).thenReturn(Optional.empty());
        when(walletRepository.findByCustomerIdForUpdate("C2"))
                .thenReturn(Optional.of(new Wallet("C2", 100)));

        assertThatThrownBy(() -> service.pay(new PaymentRequest(21L, "C2", 1000)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.PAYMENT_REQUIRED))
                .hasMessageContaining("Số dư không đủ");
    }

    @Test
    void payDeductsBalanceAndStoresPaidPayment() {
        Wallet wallet = new Wallet("C1", 5000);
        when(paymentRepository.findById(22L)).thenReturn(Optional.empty());
        when(walletRepository.findByCustomerIdForUpdate("C1")).thenReturn(Optional.of(wallet));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = service.pay(new PaymentRequest(22L, "C1", 2000));

        assertThat(wallet.getBalance()).isEqualTo(3000);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void refundIsIdempotent() {
        Wallet wallet = new Wallet("C1", 3000);
        Payment payment = payment(23L, "C1", 2000, PaymentStatus.PAID);
        when(paymentRepository.findById(23L)).thenReturn(Optional.of(payment));
        when(walletRepository.findByCustomerIdForUpdate("C1")).thenReturn(Optional.of(wallet));

        service.refund(23L);
        service.refund(23L);

        assertThat(wallet.getBalance()).isEqualTo(5000);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(walletRepository, times(1)).findByCustomerIdForUpdate("C1");
    }

    private Payment payment(Long orderId, String customerId, double amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);
        payment.setStatus(status);
        return payment;
    }
}
