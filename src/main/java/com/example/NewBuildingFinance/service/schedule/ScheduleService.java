package com.example.NewBuildingFinance.service.schedule;

public interface ScheduleService {
    /**
     * scheduled every day in 8:00
     * request for update currency table
     * @throws Exception
     */
    void getCurrencyFromApi() throws Exception;

    /**
     * scheduled every day in 1:00
     * update notifications
     */
    void updateNotifications();
}
