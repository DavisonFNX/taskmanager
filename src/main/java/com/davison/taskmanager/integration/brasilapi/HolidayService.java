package com.davison.taskmanager.integration.brasilapi;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {

    private final BrasilApiClient brasilApiClient;

    public HolidayService(BrasilApiClient brasilApiClient) {
        this.brasilApiClient = brasilApiClient;
    }

    public boolean isHoliday(LocalDate date) {
        try {
            int year = date.getYear();
            List<HolidayDto> holidays = brasilApiClient.getHolidays(year).block();
            if (holidays != null) {
                return holidays.stream().anyMatch(h -> h.date().equals(date));
            }
        } catch (Exception e) {
            // Ignore API failure
        }
        return false;
    }
}