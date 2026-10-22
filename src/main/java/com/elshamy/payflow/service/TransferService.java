
package com.elshamy.payflow.service;

import com.elshamy.payflow.dto.CreateTransferRequestDTO;
import com.elshamy.payflow.dto.TransferResponseDTO;
import com.elshamy.payflow.entity.EntryType;
import com.elshamy.payflow.entity.LedgerEntry;
import com.elshamy.payflow.entity.Transfer;
import com.elshamy.payflow.entity.User;
import com.elshamy.payflow.entity.Wallet;
import com.elshamy.payflow.exception.IllegalOperationException;
import com.elshamy.payflow.exception.ResourceNotFoundException;
import com.elshamy.payflow.repository.LedgerEntryRepository;
import com.elshamy.payflow.repository.TransferRepository;
import com.elshamy.payflow.repository.WalletRepository;
import com.elshamy.payflow.security.CurrentUserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final CurrentUserService currentUserService;

    public TransferService(
            WalletRepository walletRepository,
            TransferRepository transferRepository,
            LedgerEntryRepository ledgerEntryRepository,
            CurrentUserService currentUserService
    ) {
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public TransferResponseDTO createTransfer(
            CreateTransferRequestDTO request
    ) {
        User currentUser = currentUserService.getCurrentUser();

        Wallet sourceWallet = walletRepository
                .findByUserId(currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Source wallet not found"
                        )
                );

        Long sourceWalletId = sourceWallet.getId();
        Long destinationWalletId = request.destinationWalletId();

        if (sourceWalletId.equals(destinationWalletId)) {
            throw new IllegalOperationException(
                    "Cannot transfer money to the same wallet"
            );
        }

        // Lock wallets in a consistent order to reduce deadlocks.
        Long firstWalletId = Math.min(
                sourceWalletId,
                destinationWalletId
        );

        Long secondWalletId = Math.max(
                sourceWalletId,
                destinationWalletId
        );

        Wallet firstWallet = walletRepository
                .findByIdForUpdate(firstWalletId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found: " + firstWalletId
                        )
                );

        Wallet secondWallet = walletRepository
                .findByIdForUpdate(secondWalletId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found: " + secondWalletId
                        )
                );

        Wallet lockedSource = sourceWalletId.equals(firstWalletId)
                ? firstWallet : secondWallet;

        Wallet destinationWallet =
                destinationWalletId.equals(firstWalletId)
                        ? firstWallet : secondWallet;

        BigDecimal amount = request.amount();

        if (lockedSource.getCurrency()
                != destinationWallet.getCurrency()) {
            throw new IllegalOperationException(
                    "Wallet currencies must match"
            );
        }

        Transfer transfer = new Transfer(
                lockedSource,
                destinationWallet,
                amount
        );

        if (lockedSource.getBalance().compareTo(amount) < 0) {
            transfer.markFailed("INSUFFICIENT_FUNDS");

            Transfer failedTransfer =
                    transferRepository.save(transfer);

            // No balance updates and no ledger entries.
            return TransferResponseDTO.fromEntity(failedTransfer);
        }

        lockedSource.debit(amount);
        destinationWallet.credit(amount);

        transfer.markCompleted();

        Transfer savedTransfer =
                transferRepository.save(transfer);

        LedgerEntry debitEntry = new LedgerEntry(
                savedTransfer,
                lockedSource,
                amount,
                EntryType.DEBIT
        );

        LedgerEntry creditEntry = new LedgerEntry(
                savedTransfer,
                destinationWallet,
                amount,
                EntryType.CREDIT
        );

        ledgerEntryRepository.saveAll(
                List.of(debitEntry, creditEntry)
        );

        return TransferResponseDTO.fromEntity(savedTransfer);
    }
}
