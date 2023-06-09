package com.ssafy.moneykeeperbackend.accountbook.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.moneykeeperbackend.accountbook.dto.request.SpendingClassificationRequest;
import com.ssafy.moneykeeperbackend.accountbook.service.SpendingClassificationService;
import com.ssafy.moneykeeperbackend.security.userDetail.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account-book/spendingclassification")
public class SpendingClassificationController {

	private final SpendingClassificationService spendingClassificationService;

	/*
	 * 소비 분류 전체 가져오기
	 *
	 * @date 2023.05.09
	 * @author 정민지
	 * */
	@GetMapping()
	public ResponseEntity<?> getAllSpendingClassification(@AuthenticationPrincipal CustomUserDetails member) {
		return new ResponseEntity<>(spendingClassificationService.getAllSpendingClassification(member.getMember()),
			HttpStatus.OK);
	}

	/*
	 * 소비 분류 생성
	 *
	 * @date 2023.05.09
	 * @author 정민지
	 * */
	@PostMapping()
	public ResponseEntity<?> addSpendingClassification(@AuthenticationPrincipal CustomUserDetails member,
		@RequestBody SpendingClassificationRequest spendingClassificationRequest) {
		return new ResponseEntity<>(
			spendingClassificationService.addSpendingClassification(member.getMember(), spendingClassificationRequest),
			HttpStatus.OK);
	}

	/*
	 * 특정 소비 분류 수정
	 *
	 * @date 2023.05.11
	 * @author 정민지
	 * */
	@PatchMapping("/{spendingclassificationId}")
	public ResponseEntity<?> updateSpendingClassification(@AuthenticationPrincipal CustomUserDetails member,
		@RequestBody SpendingClassificationRequest spendingClassificationRequest,
		@PathVariable Long spendingclassificationId) {
		return new ResponseEntity<>(
			spendingClassificationService.updateSpendingClassification(member.getMember(),
				spendingClassificationRequest, spendingclassificationId),
			HttpStatus.OK);
	}

	/*
	 * 특정 소비 분류 삭제
	 *
	 * @date 2023.05.11
	 * @author 정민지
	 * */
	@DeleteMapping("/{spendingclassificationId}")
	public ResponseEntity<?> deleteSpendingClassification(@PathVariable Long spendingclassificationId) {
		spendingClassificationService.deleteSpendingClassification(spendingclassificationId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
