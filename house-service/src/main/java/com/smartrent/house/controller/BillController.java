package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.entity.Bill;
import com.smartrent.house.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public R<Long> create(@RequestBody Map<String, Object> body) {
        return R.ok(billService.createBill(
                Long.valueOf(body.get("contractId").toString()),
                Long.valueOf(body.get("userId").toString()),
                Long.valueOf(body.get("houseId").toString()),
                (String) body.get("billType"),
                new BigDecimal(body.get("amount").toString()),
                (String) body.get("billMonth"),
                (String) body.get("dueDate"),
                (String) body.get("remark")
        ));
    }

    @PostMapping("/{id}/pay")
    public R<Void> pay(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId,
                       @RequestBody Map<String, String> body) {
        billService.payBill(id, userId, body.get("payMethod"));
        return R.ok();
    }

    @GetMapping("/my")
    public R<List<Bill>> myBills(@RequestHeader("X-User-Id") Long userId,
                                 @RequestParam(value = "status", required = false) Integer status) {
        return R.ok(billService.getUserBills(userId, status));
    }

    @GetMapping("/contract/{contractId}")
    public R<List<Bill>> contractBills(@PathVariable Long contractId) {
        return R.ok(billService.getContractBills(contractId));
    }

    @GetMapping("/{id}")
    public R<Bill> detail(@PathVariable Long id) {
        return R.ok(billService.getBillDetail(id));
    }
}
