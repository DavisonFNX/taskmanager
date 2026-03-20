package com.davison.taskmanager.integration.brasilapi;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);

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
            logger.warn("Falha ao consultar feriados na Brasil API para a data {}: {}", date, e.getMessage());
        }
        return false;
    }
}