package com.smartrent.house.service;

import com.smartrent.house.entity.Bill;

import java.math.BigDecimal;
import java.util.List;

public interface BillService {

    Long createBill(Long contractId, Long userId, Long houseId, String billType,
                    BigDecimal amount, String billMonth, String dueDate, String remark);

    void payBill(Long id, Long userId, String payMethod);

    void voidBill(Long id);

    List<Bill> getUserBills(Long userId, Integer status);

    List<Bill> getContractBills(Long contractId);

    Bill getBillDetail(Long id);
}
