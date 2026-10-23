package com.mss301.payment.service;

import com.mss301.payment.dto.PaymentRequest;
import com.mss301.payment.entity.Payment;
import com.mss301.payment.entity.PaymentStatus;
import com.mss301.payment.entity.Wallet;
import com.mss301.payment.repository.PaymentRepository;
import com.mss301.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment pay(PaymentRequest request) {
        validate(request);

        var existing = paymentRepository.findById(request.orderId());
        if (existing.isPresent()) {
            log.info("PAY order={} đã được xử lý trước đó -> bỏ qua", request.orderId());
            return existing.get();
        }

        Wallet wallet = walletRepository.findByCustomerIdForUpdate(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ví của " + request.customerId()
                ));

        if (wallet.getBalance() < request.amount()) {
            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Số dư không đủ: còn " + wallet.getBalance() + ", cần " + request.amount()
            );
        }

        wallet.setBalance(wallet.getBalance() - request.amount());

        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setCustomerId(request.customerId());
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.PAID);

        log.info("PAY order={} customer={} amount={} -> balance còn {}",
                request.orderId(), request.customerId(), request.amount(), wallet.getBalance());
        return paymentRepository.save(payment);
    }

    @Transactional
    public void refund(Long orderId) {
        var existing = paymentRepository.findById(orderId);
        if (existing.isEmpty() || existing.get().getStatus() == PaymentStatus.REFUNDED) {
            log.info("REFUND order={} -> không có gì để hoàn tác", orderId);
            return;
        }

        Payment payment = existing.get();
        Wallet wallet = walletRepository.findByCustomerIdForUpdate(payment.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ví của " + payment.getCustomerId()
                ));

        wallet.setBalance(wallet.getBalance() + payment.getAmount());
        payment.setStatus(PaymentStatus.REFUNDED);
        log.info("REFUND order={} -> hoàn {} cho {}, balance = {}",
                orderId, payment.getAmount(), payment.getCustomerId(), wallet.getBalance());
    }

    private void validate(PaymentRequest request) {
        if (request.orderId() == null || request.customerId() == null
                || request.customerId().isBlank() || request.amount() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "orderId, customerId và amount dương là bắt buộc"
            );
        }
    }
}
