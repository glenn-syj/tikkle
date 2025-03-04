package com.taesan.tikkle.domain.account;

import com.taesan.tikkle.domain.account.repository.AccountCustomRepository;
import com.taesan.tikkle.domain.account.repository.AccountCustomRepositoryImpl;
import com.taesan.tikkle.domain.account.repository.BalanceSnapshotCustomRepository;
import com.taesan.tikkle.domain.account.repository.BalanceSnapshotCustomRepositoryImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

@TestConfiguration
public class SnapshotTestConfig {

    @Bean
    public BalanceSnapshotCustomRepository balanceSnapshotCustomRepository(JdbcClient jdbcClient) {
        return new BalanceSnapshotCustomRepositoryImpl(jdbcClient);
    }

    @Bean
    public AccountCustomRepository accountCustomRepository(JdbcClient jdbcClient) {
        return new AccountCustomRepositoryImpl();
    }
}
