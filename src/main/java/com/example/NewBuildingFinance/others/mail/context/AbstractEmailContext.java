package com.example.NewBuildingFinance.others.mail.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class AbstractEmailContext {
    private String from;
    private String to;
    private String subject;
    private String templateLocation;
    private Map<String, Object> context;

    public void setTo(String to) {
        this.to = to;
        put("user", to);
    }

    public void setContext() {
        context = new HashMap<>();
    }

    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key.intern(), value);
    }
}
