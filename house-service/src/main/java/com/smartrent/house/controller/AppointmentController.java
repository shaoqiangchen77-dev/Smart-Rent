package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.entity.Appointment;
import com.smartrent.house.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public R<Long> create(@RequestHeader("X-User-Id") Long userId,
                          @RequestBody Map<String, Object> body) {
        Long houseId = Long.valueOf(body.get("houseId").toString());
        Long landlordId = Long.valueOf(body.get("landlordId").toString());
        String viewingTime = (String) body.get("viewingTime");
        String contactPhone = (String) body.get("contactPhone");
        return R.ok(appointmentService.createAppointment(userId, houseId, landlordId, viewingTime, contactPhone));
    }

    @PostMapping("/{id}/confirm")
    public R<Void> confirm(@PathVariable Long id,
                           @RequestHeader("X-User-Id") Long userId) {
        appointmentService.confirmAppointment(id, userId);
        return R.ok();
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id,
                          @RequestHeader("X-User-Id") Long userId,
                          @RequestBody Map<String, String> body) {
        appointmentService.cancelAppointment(id, userId, body.get("reason"));
        return R.ok();
    }

    @PostMapping("/{id}/complete")
    public R<Void> complete(@PathVariable Long id,
                            @RequestHeader("X-User-Id") Long userId) {
        appointmentService.completeAppointment(id, userId);
        return R.ok();
    }

    @GetMapping("/my")
    public R<List<Appointment>> myAppointments(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(appointmentService.getUserAppointments(userId));
    }

    @GetMapping("/landlord")
    public R<List<Appointment>> landlordAppointments(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(appointmentService.getLandlordAppointments(userId));
    }

    /**
     * 预约总数（管理员看板，内部调用，无需登录态）
     */
    @GetMapping("/count")
    public R<Long> count() {
        return R.ok(appointmentService.countAll());
    }
}
