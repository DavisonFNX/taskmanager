package com.davison.taskmanager.integration.brasilapi;

import java.time.LocalDate;

public record HolidayDto(
    LocalDate date,
    String name
) {}