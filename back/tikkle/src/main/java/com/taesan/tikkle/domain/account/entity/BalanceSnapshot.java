package com.taesan.tikkle.domain.account.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.domain.member.entity.Member;
import com.taesan.tikkle.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "balance_snapshot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceSnapshot extends BaseEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id = UlidCreator.getMonotonicUlid().toUuid();

    // 계좌 ID 이외의 정보는 필요하지 않으므로 Id만 이용
    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private int timeQnt;

    @Column(nullable = false)
    private int rankingPoint;

    @Builder
    private BalanceSnapshot(UUID accountId, int timeQnt, int rankingPoint) {
        this.accountId = accountId;
        this.timeQnt = timeQnt;
        this.rankingPoint = rankingPoint;
    }

}
