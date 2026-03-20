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

    public String findHoliday(LocalDate date) {
        try {
            int year = date.getYear();
            List<HolidayDto> holidays = brasilApiClient.getHolidays(year).block();
            HolidayDto holidayVazio = new HolidayDto(null, null);
            if (holidays != null) {
                return holidays.stream().filter(h -> h.date().equals(date)).findFirst().orElse(holidayVazio).name();
            }
        } catch (Exception e) {
            logger.warn("Falha ao consultar feriados na Brasil API para a data {}: {}", date, e.getMessage());
        }
        return null;
    }
}