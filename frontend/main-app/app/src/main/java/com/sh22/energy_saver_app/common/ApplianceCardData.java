package com.sh22.energy_saver_app.common;

/**
 * A utility class for holding data used in the ApplianceRecyclerView adapter to hold the card data
 */
public class ApplianceCardData {
    String applianceName;
    Float initialUsage;
    Float usageToday;
    Float weeklyUsage;
    Float national_average;

    public ApplianceCardData(String applianceName, Float initialUsage, Float usageToday, Float weeklyUsage, Float national_average) {
        this.applianceName = applianceName;
        this.initialUsage = initialUsage;
        this.usageToday = usageToday;
        this.weeklyUsage = weeklyUsage;
        this.national_average = national_average;
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

    public Float getNationalAverage() { return national_average; }
}
