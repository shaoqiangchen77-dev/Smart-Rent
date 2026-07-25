package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.entity.Contract;
import com.smartrent.house.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public R<Long> create(@RequestHeader("X-User-Id") Long userId,
                          @RequestBody Map<String, Object> body) {
        return R.ok(contractService.createContract(
                userId,
                Long.valueOf(body.get("houseId").toString()),
                Long.valueOf(body.get("landlordId").toString()),
                body.get("appointmentId") != null ? Long.valueOf(body.get("appointmentId").toString()) : null,
                (String) body.get("startDate"),
                (String) body.get("endDate"),
                new java.math.BigDecimal(body.get("monthlyRent").toString()),
                new java.math.BigDecimal(body.get("deposit").toString()),
                (String) body.get("paymentCycle"),
                body.get("payDay") != null ? Integer.valueOf(body.get("payDay").toString()) : null
        ));
    }

    @PostMapping("/{id}/sign")
    public R<Void> sign(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        contractService.signContract(id, userId);
        return R.ok();
    }

    @PostMapping("/{id}/terminate")
    public R<Void> terminate(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId,
                             @RequestBody Map<String, String> body) {
        contractService.terminateContract(id, userId, body.get("reason"));
        return R.ok();
    }

    @GetMapping("/my")
    public R<List<Contract>> myContracts(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(contractService.getUserContracts(userId));
    }

    @GetMapping("/landlord")
    public R<List<Contract>> landlordContracts(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(contractService.getLandlordContracts(userId));
    }

    @GetMapping("/{id}")
    public R<Contract> detail(@PathVariable Long id) {
        return R.ok(contractService.getContractDetail(id));
    }
}
