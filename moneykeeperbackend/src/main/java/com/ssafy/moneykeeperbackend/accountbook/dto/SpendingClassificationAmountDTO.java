package com.ssafy.moneykeeperbackend.accountbook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SpendingClassificationAmountDTO {

	private Long classificationId;

	private int amount;
}
