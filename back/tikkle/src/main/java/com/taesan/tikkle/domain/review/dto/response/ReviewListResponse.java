package com.taesan.tikkle.domain.review.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
	private UUID reviewId;
	private String type;
}