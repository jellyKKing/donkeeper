package com.ssafy.moneykeeperbackend.accountbook.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.moneykeeperbackend.accountbook.dto.BudgetDTO;
import com.ssafy.moneykeeperbackend.accountbook.dto.SpendingClassificationAmountDTO;
import com.ssafy.moneykeeperbackend.accountbook.dto.SpendingClassificationNameAmountDTO;
import com.ssafy.moneykeeperbackend.accountbook.dto.response.BudgetResponse;
import com.ssafy.moneykeeperbackend.accountbook.entity.Budget;
import com.ssafy.moneykeeperbackend.accountbook.repository.BudgetRepository;
import com.ssafy.moneykeeperbackend.accountbook.repository.MajorSpendingClassificationRepository;
import com.ssafy.moneykeeperbackend.accountbook.repository.SpendingClassificationRepository;
import com.ssafy.moneykeeperbackend.accountbook.service.BudgetService;
import com.ssafy.moneykeeperbackend.exception.accountbook.AccountBookExceptionEnum;
import com.ssafy.moneykeeperbackend.exception.accountbook.AccountBookRuntimeException;
import com.ssafy.moneykeeperbackend.member.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

	private final BudgetRepository budgetRepository;

	private final SpendingClassificationRepository spendingClassificationRepository;

	/*
	 * 예산 입력 또는 수정
	 *
	 * @date 2023.05.09
	 * @author 정민지
	 * */
	@Transactional
	@Override
	public BudgetDTO upsertMonthBudget(BudgetDTO budgetDTO, Member member) {
		System.out.println();
		System.out.println(budgetDTO.getYear());
		System.out.println(budgetDTO.getMonth());
		System.out.println(budgetDTO.getTotal_amount());
		System.out.println("--------------------");
		for (SpendingClassificationAmountDTO a: budgetDTO.getDatas()
			 ) {
			System.out.println(a.getClassificationId());
			System.out.println(a.getAmount());
		}
		System.out.println("--------------------");
		System.out.println();

		// 전체 예산이 있으면 수정, 없으면 등록
		Budget budget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationIsNull(member,
			budgetDTO.getYear(), budgetDTO.getMonth()).orElse(null);

		if (budget == null) {
			budgetRepository.saveAndFlush(Budget.builder()
				.year(budgetDTO.getYear())
				.month(budgetDTO.getMonth())
				.amount(budgetDTO.getTotal_amount())
				.member(member)
				.build());
		} else {
			budget.setAmount(budgetDTO.getTotal_amount());
			budgetRepository.saveAndFlush(budget);
		}

		// 소비 분류 별 예산이 있으면 수정, 없으면 등록
		for (SpendingClassificationAmountDTO spendingClassificationAmountDTO : budgetDTO.getDatas()) {
			Budget classificationBudget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationId(
				member, budgetDTO.getYear(), budgetDTO.getMonth(),
				spendingClassificationAmountDTO.getClassificationId()).orElse(null);

			if (classificationBudget == null) {
				budgetRepository.saveAndFlush(Budget.builder()
					.year(budgetDTO.getYear())
					.month(budgetDTO.getMonth())
					.member(member)
					.amount(spendingClassificationAmountDTO.getAmount())
					.spendingClassification(spendingClassificationRepository.findById(
							spendingClassificationAmountDTO.getClassificationId())
						.orElseThrow(() -> new AccountBookRuntimeException(
							AccountBookExceptionEnum.SPENDING_CLASSIFICATION_ID_NULL)))
					.build());
			} else {
				classificationBudget.setAmount(spendingClassificationAmountDTO.getAmount());
				budgetRepository.saveAndFlush(classificationBudget);
			}
		}

		Budget resultTotalBudget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationIsNull(
			member, budgetDTO.getYear(), budgetDTO.getMonth()).orElse(null);

		BudgetDTO resultBudgetDTO = BudgetDTO.builder()
			.year(budgetDTO.getYear())
			.month(budgetDTO.getMonth())
			.total_amount(resultTotalBudget != null ? resultTotalBudget.getAmount() : 0)
			.build();

		List<Budget> resultClassificationBudget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationIsNotNull(
			member, budgetDTO.getYear(), budgetDTO.getMonth());

		List<SpendingClassificationAmountDTO> resultSpendingClassificationAmountDTOS = new ArrayList<>();
		for (Budget forBudget : resultClassificationBudget) {
			resultSpendingClassificationAmountDTOS.add(
				SpendingClassificationAmountDTO.builder()
					.classificationId(forBudget.getSpendingClassification().getId())
					.amount(forBudget.getAmount())
					.build()
			);
		}

		resultBudgetDTO.setDatas(resultSpendingClassificationAmountDTOS);
		return resultBudgetDTO;
	}

	/*
	 * 월별 예산 전체 가져오기
	 *
	 * @date 2023.05.11
	 * @author 정민지
	 * */
	@Override
	public BudgetResponse getMonthAllBudget(Member member, int year, int month) {
		Budget resultTotalBudget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationIsNull(
			member, year, month).orElse(null);

		BudgetResponse resultBudgetDTO = BudgetResponse.builder()
			.year(year)
			.month(month)
			.total_amount(resultTotalBudget != null ? resultTotalBudget.getAmount() : 0)
			.build();

		List<Budget> resultClassificationBudget = budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationIsNotNull(
			member, year, month);

		List<SpendingClassificationNameAmountDTO> resultSpendingClassificationAmountDTOS = new ArrayList<>();
		for (Budget forBudget : resultClassificationBudget) {
			resultSpendingClassificationAmountDTOS.add(
				SpendingClassificationNameAmountDTO.builder()
					.classificationId(forBudget.getSpendingClassification().getId())
					.name(forBudget.getSpendingClassification().getName())
					.amount(forBudget.getAmount())
					.build()
			);
		}

		resultBudgetDTO.setDatas(resultSpendingClassificationAmountDTOS);
		return resultBudgetDTO;
	}

	/*
	 * 예산 삭제
	 *
	 * @date 2023.05.09
	 * @author 정민지
	 * */
	@Transactional
	@Override
	public void deleteBudget(Member member, int year, int month, Long spendingClassificationId) {

		if (spendingClassificationId == null) {
			budgetRepository.deleteByMemberAndYearAndMonth(member, year, month);
		} else {
			budgetRepository.findByMemberAndYearAndMonthAndSpendingClassificationId(member, year, month,
					spendingClassificationId)
				.orElseThrow(() -> new AccountBookRuntimeException(AccountBookExceptionEnum.BUDGET_NULL));

			budgetRepository.deleteByMemberAndYearAndMonthAndSpendingClassificationId(member, year, month,
				spendingClassificationId);
		}

	}
}
