package com.smartrent.house.service;

import com.smartrent.house.entity.Contract;

import java.util.List;

public interface ContractService {

    Long createContract(Long userId, Long houseId, Long landlordId, Long appointmentId,
                        String startDate, String endDate, java.math.BigDecimal monthlyRent,
                        java.math.BigDecimal deposit, String paymentCycle, Integer payDay);

    void signContract(Long id, Long userId);

    void terminateContract(Long id, Long userId, String reason);

    List<Contract> getUserContracts(Long userId);

    List<Contract> getLandlordContracts(Long landlordId);

    Contract getContractDetail(Long id);
}
