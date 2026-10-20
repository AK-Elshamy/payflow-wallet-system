package com.elshamy.payflow.repository;

import com.elshamy.payflow.entity.Transfer;
import com.elshamy.payflow.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findBySourceWalletId(Long walletId);

    List<Transfer> findByDestinationWalletId(Long walletId);

    List<Transfer> findByStatus(TransferStatus status);
}