
package com.elshamy.payflow.entity;

import com.elshamy.payflow.exception.IllegalOperationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    public Wallet(User user, CurrencyCode currency) {
        this.user = user;
        this.currency = currency;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void debit(BigDecimal amount) {
        validateAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalOperationException(
                    "Insufficient wallet balance"
            );
        }

        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        validateAmount(amount);

        BigDecimal newBalance = balance.add(amount);

        if (newBalance.scale() > 2) {
            newBalance = newBalance.setScale(2, RoundingMode.UNNECESSARY);
        }

        if (newBalance.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalOperationException(
                    "Wallet balance exceeds the maximum allowed amount"
            );
        }

        balance = newBalance;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalOperationException(
                    "Amount must be greater than zero"
            );
        }

        if (amount.scale() > 2) {
            throw new IllegalOperationException(
                    "Amount must have at most two decimal places"
            );
        }
    }


}
