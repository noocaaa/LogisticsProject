package com.tsystems.logistics;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.repository.DriverRepository;

import java.util.List;

@Component
public class DriverHoursResetScheduler {

    private final DriverRepository driverRepository;

    public DriverHoursResetScheduler(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetDriverWorkingHours() {
        List<Driver> drivers = driverRepository.findAll();
        for (Driver driver : drivers) {
            driver.setWorkingHours(0);
            driverRepository.save(driver);
        }
    }
}
