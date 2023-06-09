package com.ssafy.moneykeeperbackend.statistics.fortest;

import com.ssafy.moneykeeperbackend.accountbook.entity.MajorSpendingClassification;
import com.ssafy.moneykeeperbackend.accountbook.repository.MajorSpendingClassificationRepository;
import com.ssafy.moneykeeperbackend.accountbook.service.SpendingService;
import com.ssafy.moneykeeperbackend.card.service.PutCardService;
import com.ssafy.moneykeeperbackend.member.entity.Member;
import com.ssafy.moneykeeperbackend.member.repository.MemberRepository;
import com.ssafy.moneykeeperbackend.statistics.entity.IncomeGroup;
import com.ssafy.moneykeeperbackend.statistics.repository.IncomeGroupRepository;
import com.ssafy.moneykeeperbackend.statistics.service.UpdateDataService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final UpdateDataService updateDataService;

    private final IndividualTestService individualTestService;

    private final PutCardService putCardService;
    private final TestService testService;

    private final MemberRepository memberRepository;

    private final IncomeGroupRepository incomeGroupRepository;

    private final MajorSpendingClassificationRepository majorSpendingClassificationRepository;

    @GetMapping("")
    @ApiOperation(value = "test", notes = "test")
    public ResponseEntity<?> initForTest() {
        testService.initCommonForTest();

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @GetMapping("/dummyalot")
    @ApiOperation(value = "test", notes = "test")
    public ResponseEntity<?> dummyData() {
        LocalDate now = LocalDate.now();

        LocalDate thisMonth = LocalDate.of(now.getYear(),now.getMonth(),1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        LocalDate end = LocalDate.of(lastMonth.getYear(),lastMonth.getMonth(),1);
        LocalDate start = end.minusMonths(2);

        List<MajorSpendingClassification> mscList = majorSpendingClassificationRepository.findAll();

        for (int i = 0; i < 30; i++) {
            Random random = new Random();
            int rn = random.nextInt(1000000000)+1;
            testService.generateMockMemberWithString(String.valueOf(rn)+i,mscList,start,end,true);
        }

        updateDataService.updateSpendingCompData();

        return new ResponseEntity<Integer>(0, HttpStatus.OK);
    }

    @GetMapping("/dummy")
    @ApiOperation(value = "test", notes = "test")
    public ResponseEntity<?> putDummyFor(@RequestParam Long id) { // 나중에 바꿀 것
        LocalDate now = LocalDate.now();

        LocalDate thisMonth = LocalDate.of(now.getYear(),now.getMonth(),1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        LocalDate end = LocalDate.of(lastMonth.getYear(),lastMonth.getMonth(),1);
        LocalDate start = end.minusMonths(2);

        Optional<Member> memberOptional = memberRepository.findById(id);

        if (memberOptional.isEmpty()) return new ResponseEntity<>("no such user", HttpStatus.OK);

        Member member = memberOptional.get();

        individualTestService.generateMockSpendingsWith(member,false);
        individualTestService.generateMockIncomesWith(member);
        List<MajorSpendingClassification> mscList = majorSpendingClassificationRepository.findAll();
        List<IncomeGroup> igList = incomeGroupRepository.findAll();
        updateDataService.generateGroupSpending(end,mscList,igList);
        updateDataService.determineIncomeGroupAndUpdateGroupSpending(member,start,end,mscList);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    @GetMapping("/savecards")
    public ResponseEntity<?> saveCards() {
        putCardService.saveCards();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
