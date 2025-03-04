package com.taesan.tikkle.domain.account.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class BalanceSnapshotCustomRepositoryImpl implements BalanceSnapshotCustomRepository {

    private final JdbcClient jdbcClient;
    private static final Logger logger = LoggerFactory.getLogger(BalanceSnapshotCustomRepositoryImpl.class);

    public BalanceSnapshotCustomRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public int insertDirectlyFromAccounts() {
        logger.info("Starting INSERT INTO SELECT of balance snapshots directly from account table");

        // INSERT INTO SELECT 문으로 애플리케이션 메모리 사용 없이 DB 내부에서 직접 데이터 변환 및 삽입
        int rowsAffected = jdbcClient.sql(
                        "INSERT INTO balance_snapshot (id, account_id, time_qnt, ranking_point, created_at) " +
                                "SELECT " +
                                "  CONCAT(UNHEX(LPAD(HEX(ROUND(UNIX_TIMESTAMP(NOW(3)) * 1000)), 12, '0')), " +
                                "  UNHEX(LEFT(MD5(CONCAT(UUID(), RAND())), 20))), " + // 랜덤 부분 (80비트 = 10바이트)
                                "  id, time_qnt, ranking_point, NOW() " +
                                "FROM accounts")
                .update();

        logger.info("INSERT INTO SELECT completed, inserted {} records", rowsAffected);

        return rowsAffected;
    }
}
