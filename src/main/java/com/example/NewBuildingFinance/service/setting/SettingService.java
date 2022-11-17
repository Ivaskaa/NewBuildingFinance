package com.example.NewBuildingFinance.service.setting;

import com.example.NewBuildingFinance.entities.setting.Setting;

public interface SettingService {
    /**
     * update settings in database
     * if setting id != 1; id = 1
     * @param setting settings
     * @return setting after save
     */
    Setting updateSettings(Setting setting);

    /**
     * get settings from database
     * @return setting from database
     */
    Setting getSettings();

    /**
     * create default settings if in database settings is empty
     * @return default settings
     */
    Setting createSettings();
}
