package com.elshamy.payflow.repository;

import com.elshamy.payflow.entity.EntryType;
import com.elshamy.payflow.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByWalletId(Long walletId);

    List<LedgerEntry> findByTransferId(Long transferId);

    List<LedgerEntry> findByWalletIdAndEntryType(
            Long walletId,
            EntryType entryType
    );
}