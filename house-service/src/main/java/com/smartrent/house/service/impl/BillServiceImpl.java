package com.smartrent.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.exception.BusinessException;
import com.smartrent.common.result.ResultCode;
import com.smartrent.house.entity.Bill;
import com.smartrent.house.mapper.BillMapper;
import com.smartrent.house.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;

    @Override
    public Long createBill(Long contractId, Long userId, Long houseId, String billType,
                           BigDecimal amount, String billMonth, String dueDate, String remark) {
        Bill bill = new Bill();
        bill.setBillNo("ZD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        bill.setContractId(contractId);
        bill.setUserId(userId);
        bill.setHouseId(houseId);
        bill.setBillType(billType);
        bill.setAmount(amount);
        bill.setBillMonth(billMonth);
        bill.setDueDate(LocalDate.parse(dueDate));
        bill.setRemark(remark);
        bill.setStatus(0);
        billMapper.insert(bill);
        return bill.getId();
    }

    @Override
    public void payBill(Long id, Long userId, String payMethod) {
        Bill bill = billMapper.selectById(id);
        if (bill == null || !bill.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (bill.getStatus() != 0) {
            throw new BusinessException("该账单不可支付");
        }
        bill.setStatus(1);
        bill.setPayTime(LocalDateTime.now());
        bill.setPayMethod(payMethod);
        billMapper.updateById(bill);
    }

    @Override
    public void voidBill(Long id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }
        bill.setStatus(3);
        billMapper.updateById(bill);
    }

    @Override
    public List<Bill> getUserBills(Long userId, Integer status) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<Bill>()
                .eq(Bill::getUserId, userId);
        if (status != null) {
            wrapper.eq(Bill::getStatus, status);
        }
        wrapper.orderByDesc(Bill::getCreateTime);
        return billMapper.selectList(wrapper);
    }

    @Override
    public List<Bill> getContractBills(Long contractId) {
        return billMapper.selectList(
                new LambdaQueryWrapper<Bill>()
                        .eq(Bill::getContractId, contractId)
                        .orderByDesc(Bill::getCreateTime));
    }

    @Override
    public Bill getBillDetail(Long id) {
        return billMapper.selectById(id);
    }
}
