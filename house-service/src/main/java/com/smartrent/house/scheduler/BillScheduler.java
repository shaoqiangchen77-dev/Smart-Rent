package com.smartrent.house.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.house.entity.Bill;
import com.smartrent.house.entity.Contract;
import com.smartrent.house.mapper.BillMapper;
import com.smartrent.house.mapper.ContractMapper;
import com.smartrent.house.mq.HouseUpdateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BillScheduler {

    private final BillMapper billMapper;
    private final ContractMapper contractMapper;
    private final HouseUpdateProducer houseUpdateProducer;

    /**
     * 每天凌晨1点检测逾期账单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOverdueBills() {
        log.info("开始检测逾期账单...");
        List<Bill> overdueBills = billMapper.selectList(
                new LambdaQueryWrapper<Bill>()
                        .eq(Bill::getStatus, 0)
                        .lt(Bill::getDueDate, LocalDate.now()));

        for (Bill bill : overdueBills) {
            bill.setStatus(2); // 已逾期
            billMapper.updateById(bill);
            houseUpdateProducer.sendBillOverdue(bill.getId(), bill.getUserId());
            log.info("账单逾期: billId={}, userId={}", bill.getId(), bill.getUserId());
        }
        log.info("逾期账单检测完成，共{}条", overdueBills.size());
    }

    /**
     * 每天凌晨2点检测到期合同
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkExpiredContracts() {
        log.info("开始检测到期合同...");
        List<Contract> expiredContracts = contractMapper.selectList(
                new LambdaQueryWrapper<Contract>()
                        .eq(Contract::getStatus, 1)
                        .lt(Contract::getEndDate, LocalDate.now()));

        for (Contract contract : expiredContracts) {
            contract.setStatus(2); // 已到期
            contractMapper.updateById(contract);
            log.info("合同到期: contractId={}, userId={}", contract.getId(), contract.getUserId());
        }
        log.info("到期合同检测完成，共{}条", expiredContracts.size());
    }
}
