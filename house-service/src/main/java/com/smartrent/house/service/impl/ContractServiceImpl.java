package com.smartrent.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.exception.BusinessException;
import com.smartrent.common.result.ResultCode;
import com.smartrent.house.entity.Contract;
import com.smartrent.house.entity.House;
import com.smartrent.house.mapper.ContractMapper;
import com.smartrent.house.mapper.HouseMapper;
import com.smartrent.house.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final HouseMapper houseMapper;

    @Override
    @Transactional
    public Long createContract(Long userId, Long houseId, Long landlordId, Long appointmentId,
                               String startDate, String endDate, BigDecimal monthlyRent,
                               BigDecimal deposit, String paymentCycle, Integer payDay) {
        // 检查房源是否有生效中的合同
        Long count = contractMapper.selectCount(
                new LambdaQueryWrapper<Contract>()
                        .eq(Contract::getHouseId, houseId)
                        .eq(Contract::getStatus, 1));
        if (count > 0) {
            throw new BusinessException(ResultCode.CONTRACT_ACTIVE);
        }

        Contract contract = new Contract();
        contract.setContractNo("HT" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        contract.setUserId(userId);
        contract.setHouseId(houseId);
        contract.setLandlordId(landlordId);
        contract.setAppointmentId(appointmentId);
        contract.setStartDate(LocalDate.parse(startDate));
        contract.setEndDate(LocalDate.parse(endDate));
        contract.setMonthlyRent(monthlyRent);
        contract.setDeposit(deposit);
        contract.setPaymentCycle(paymentCycle != null ? paymentCycle : "月付");
        contract.setPayDay(payDay != null ? payDay : 1);
        contract.setStatus(0); // 待生效
        contract.setIsDeleted(0);
        contractMapper.insert(contract);

        return contract.getId();
    }

    @Override
    public void signContract(Long id, Long userId) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null || !contract.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        contract.setStatus(1);
        contract.setSignTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        // 更新房源状态为已租出
        House house = houseMapper.selectById(contract.getHouseId());
        if (house != null) {
            house.setStatus(3);
            houseMapper.updateById(house);
        }
    }

    @Override
    public void terminateContract(Long id, Long userId, String reason) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        if (!contract.getUserId().equals(userId) && !contract.getLandlordId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        contract.setStatus(3);
        contract.setTerminateReason(reason);
        contractMapper.updateById(contract);

        // 更新房源状态为已上架
        House house = houseMapper.selectById(contract.getHouseId());
        if (house != null) {
            house.setStatus(1);
            houseMapper.updateById(house);
        }
    }

    @Override
    public List<Contract> getUserContracts(Long userId) {
        return contractMapper.selectList(
                new LambdaQueryWrapper<Contract>()
                        .eq(Contract::getUserId, userId)
                        .eq(Contract::getIsDeleted, 0)
                        .orderByDesc(Contract::getCreateTime));
    }

    @Override
    public List<Contract> getLandlordContracts(Long landlordId) {
        return contractMapper.selectList(
                new LambdaQueryWrapper<Contract>()
                        .eq(Contract::getLandlordId, landlordId)
                        .eq(Contract::getIsDeleted, 0)
                        .orderByDesc(Contract::getCreateTime));
    }

    @Override
    public Contract getContractDetail(Long id) {
        return contractMapper.selectById(id);
    }
}
