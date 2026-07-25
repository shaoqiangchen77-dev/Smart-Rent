package com.smartrent.house.service;

import com.smartrent.house.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Long createAppointment(Long userId, Long houseId, Long landlordId, String viewingTime, String contactPhone);

    void confirmAppointment(Long id, Long landlordId);

    void cancelAppointment(Long id, Long userId, String reason);

    void completeAppointment(Long id, Long landlordId);

    List<Appointment> getUserAppointments(Long userId);

    List<Appointment> getLandlordAppointments(Long landlordId);

    /**
     * 预约总数（管理员看板用）
     */
    long countAll();
}
