package com.taesan.tikkle.domain.account.service;

import com.taesan.tikkle.domain.account.entity.Account;
import com.taesan.tikkle.domain.account.entity.BalanceSnapshot;
import com.taesan.tikkle.domain.account.repository.BalanceSnapshotCustomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("jdbcSnapshotStrategy")
@RequiredArgsConstructor
public class JdbcSnapshotStrategy implements SnapshotStrategy {

    private final BalanceSnapshotCustomRepository balanceSnapshotCustomRepository;
    private static final Logger logger = LoggerFactory.getLogger(JdbcSnapshotStrategy.class);

    @Override
    @Transactional
    public void createSnapShots() {
        logger.info("Creating snapshots using JDBC");
        int insertedCount = balanceSnapshotCustomRepository.insertDirectlyFromAccounts();
        logger.info("Created {} snapshots using JDBC bulk insert", insertedCount);
    }

    @Override
    public String getStrategyName() {
        return SnapshotStrategyType.JDBC.getDescription();
    }

    private BalanceSnapshot convertToSnapshot(Account account) {
        return BalanceSnapshot.builder()
                .accountId(account.getId())
                .timeQnt(account.getTimeQnt())
                .rankingPoint(account.getRankingPoint())
                .build();
    }
}
