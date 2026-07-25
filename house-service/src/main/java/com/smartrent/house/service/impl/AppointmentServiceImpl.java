package com.smartrent.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.exception.BusinessException;
import com.smartrent.common.result.ResultCode;
import com.smartrent.house.entity.Appointment;
import com.smartrent.house.mapper.AppointmentMapper;
import com.smartrent.house.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;

    @Override
    public Long createAppointment(Long userId, Long houseId, Long landlordId, String viewingTime, String contactPhone) {
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setHouseId(houseId);
        appointment.setLandlordId(landlordId);
        appointment.setViewingTime(LocalDateTime.parse(viewingTime));
        appointment.setContactPhone(contactPhone);
        appointment.setStatus(0);
        appointmentMapper.insert(appointment);
        return appointment.getId();
    }

    @Override
    public void confirmAppointment(Long id, Long landlordId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null || !appointment.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        appointment.setStatus(1);
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void cancelAppointment(Long id, Long userId, String reason) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        appointment.setStatus(2);
        appointment.setCancelReason(reason);
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void completeAppointment(Long id, Long landlordId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null || !appointment.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        appointment.setStatus(3);
        appointmentMapper.updateById(appointment);
    }

    @Override
    public List<Appointment> getUserAppointments(Long userId) {
        return appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getUserId, userId)
                        .orderByDesc(Appointment::getCreateTime));
    }

    @Override
    public List<Appointment> getLandlordAppointments(Long landlordId) {
        return appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getLandlordId, landlordId)
                        .orderByDesc(Appointment::getCreateTime));
    }

    @Override
    public long countAll() {
        return appointmentMapper.selectCount(null);
    }
}
