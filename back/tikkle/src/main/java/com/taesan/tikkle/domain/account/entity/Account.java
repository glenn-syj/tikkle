package com.taesan.tikkle.domain.account.entity;

import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.domain.account.dto.ExchangeType;
import com.taesan.tikkle.domain.member.entity.Member;
import com.taesan.tikkle.global.entity.AuditableEntity;
import com.taesan.tikkle.global.errors.ErrorCode;
import com.taesan.tikkle.global.exceptions.CustomException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends AuditableEntity {
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id = UlidCreator.getMonotonicUlid().toUuid();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "time_qnt", columnDefinition = "INTEGER")
	private Integer timeQnt;

	@Column(name = "ranking_point", columnDefinition = "INTEGER")
	private Integer rankingPoint;

	@Builder
	private Account(Member member, Integer timeQnt) {
		this.member = member;
		this.timeQnt = timeQnt;
		this.rankingPoint = 0;
	}

	public void updateAccount(ExchangeType exchangeType, Integer rate, Integer quantity) {
		if (exchangeType.equals(ExchangeType.TTOR)) {
			this.timeQnt -= quantity;
			this.rankingPoint += rate * quantity;
			validTimeQntAndRankingPoint(timeQnt, rankingPoint);
		} else if (exchangeType.equals(ExchangeType.RTOT)) {
			this.timeQnt += quantity;
			this.rankingPoint -= rate * quantity;
			validTimeQntAndRankingPoint(timeQnt, rankingPoint);
		}
	}

	private void validTimeQntAndRankingPoint(Integer timeQnt, Integer rankingPoint) {
		if (timeQnt < 0 || rankingPoint < 0)
			throw new CustomException(ErrorCode.ACCOUNT_INSUFFICIENT_BALANCE);
	}

	public void setBalance(Integer timeQnt) {
		this.timeQnt = timeQnt;
	}

	public void setRankingPoint(Integer rankingPoint) { this.rankingPoint = rankingPoint; }
}

