package com.taesan.tikkle.domain.account.service;

import com.taesan.tikkle.domain.account.entity.Account;
import com.taesan.tikkle.domain.account.entity.BalanceSnapshot;
import com.taesan.tikkle.domain.account.repository.AccountRepository;
import com.taesan.tikkle.domain.account.repository.BalanceSnapshotRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("pagingSnapshotStrategy")
@RequiredArgsConstructor
public class PagingSnapshotStrategy implements SnapshotStrategy {

    private final AccountRepository accountRepository;
    private final BalanceSnapshotRepository balanceSnapshotRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(PagingSnapshotStrategy.class);

    @Override
    @Transactional
    public void createSnapShots() {

        logger.info("Creating snapshots using Paging strategy");

        int pageSize = 1000;
        int pageNumber = 0;
        int totalProcessed = 0;
        boolean hasMoreData = true;

        while (hasMoreData) {

            // 1. COUNT 쿼리 없는 Slice로 account 가져오기
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Slice<Account> accountSlice = accountRepository.findAllAsSlice(pageable);

            // 2. 해당 Slice 내에서 BalanceSnapshot으로 변경
            List<BalanceSnapshot> batchSnapshots = accountSlice.getContent().stream()
                    .map(this::convertToSnapshot)
                    .collect(Collectors.toList());

            // 3. 영속화 이후 영속성 컨텍스트 초기화
            balanceSnapshotRepository.saveAll(batchSnapshots);
            entityManager.flush();
            entityManager.clear();

            totalProcessed += batchSnapshots.size();
            logger.debug("Processed page {} with {} accounts", pageNumber, batchSnapshots.size());

            // 4. 다음 Slice 준비
            pageNumber++;
            hasMoreData = accountSlice.hasNext();
        }

        logger.info("Created {} snapshots using Paging strategy", totalProcessed);
    }

    @Override
    public String getStrategyName() {
        return SnapshotStrategyType.PAGING.getDescription();
    }

    private BalanceSnapshot convertToSnapshot(Account account) {
        return BalanceSnapshot.builder()
                .accountId(account.getId())
                .timeQnt(account.getTimeQnt())
                .rankingPoint(account.getRankingPoint())
                .build();
    }
}
