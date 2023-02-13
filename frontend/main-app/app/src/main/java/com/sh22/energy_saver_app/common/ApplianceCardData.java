package com.sh22.energy_saver_app.common;

// The class used in the recycler view used for each individual entry
public class ApplianceCardData {
    String applianceName;
    Float initialUsage;
    Float usageToday;
    Float weeklyUsage;

    public ApplianceCardData(String applianceName, Float initialUsage, Float usageToday, Float weeklyUsage) {
        this.applianceName = applianceName;
        this.initialUsage = initialUsage;
        this.usageToday = usageToday;
        this.weeklyUsage = weeklyUsage;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public Float getInitialUsage(){ return initialUsage; }

    public Float getUsageToday() {
        return usageToday;
    }

    public Float getWeeklyUsage() {
        return weeklyUsage;
    }
}
