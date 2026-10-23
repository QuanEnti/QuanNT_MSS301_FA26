package com.mss301.inventory.service;

import com.mss301.inventory.dto.ReserveRequest;
import com.mss301.inventory.entity.Product;
import com.mss301.inventory.entity.Reservation;
import com.mss301.inventory.entity.ReservationStatus;
import com.mss301.inventory.repository.ProductRepository;
import com.mss301.inventory.repository.ReservationRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReservationRepository reservationRepository;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(productRepository, reservationRepository);
    }

    @Test
    void reserveDoesNotDeductTwiceForTheSameOrder() {
        Reservation existing = reservation(10L, "P1", 2, ReservationStatus.RESERVED);
        when(reservationRepository.findById(10L)).thenReturn(Optional.of(existing));

        Reservation result = service.reserve(new ReserveRequest(10L, "P1", 2));

        assertThat(result).isSameAs(existing);
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reserveReturnsConflictWhenStockIsInsufficient() {
        when(reservationRepository.findById(11L)).thenReturn(Optional.empty());
        when(productRepository.findById("P2"))
                .thenReturn(Optional.of(new Product("P2", "Mouse", 20, 5)));

        assertThatThrownBy(() -> service.reserve(new ReserveRequest(11L, "P2", 6)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void releaseIsIdempotent() {
        Product product = new Product("P1", "Laptop", 1000, 8);
        Reservation reservation = reservation(12L, "P1", 2, ReservationStatus.RESERVED);
        when(reservationRepository.findById(12L)).thenReturn(Optional.of(reservation));
        when(productRepository.findById("P1")).thenReturn(Optional.of(product));

        service.release(12L);
        service.release(12L);

        assertThat(product.getStock()).isEqualTo(10);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RELEASED);
        verify(productRepository, times(1)).save(product);
    }

    private Reservation reservation(Long orderId, String productId, int quantity, ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setOrderId(orderId);
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setStatus(status);
        return reservation;
    }
}
