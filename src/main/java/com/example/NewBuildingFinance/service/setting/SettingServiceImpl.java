package com.example.NewBuildingFinance.service.setting;

import com.example.NewBuildingFinance.entities.setting.Setting;
import com.example.NewBuildingFinance.repository.SettingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class SettingServiceImpl implements SettingService{
    private final SettingRepository settingRepository;

    @Override
    public Setting getSettings() {
        log.info("get settings");
        Setting setting = settingRepository.findById(1L).orElse(null);
        if(setting == null){
            setting = createSettings();
        }
        log.info("success get settings");
        return setting;
    }

    @Override
    public Setting updateSettings(Setting setting) {
        log.info("update setting: {}", setting);
        setting.setId(1L);
        Setting settingAfterSave = settingRepository.save(setting);
        log.info("success update setting");
        return settingAfterSave;
    }

    @Override
    public Setting createSettings() {
        log.info("create settings");
        Setting setting = new Setting();
        setting.setId(1L);
        setting.setNotificationAgency(true);
        setting.setNotificationContract(true);
        setting.setNotificationFlatPayment(true);
        setting.setSendEmailToBuyers(true);
        setting.setPdfFooter("");
        settingRepository.save(setting);
        log.info("success get settings");
        return setting;
    }


}
