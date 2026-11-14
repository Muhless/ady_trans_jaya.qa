package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

public class ReportPage extends HomePage {
    public ReportPage(AndroidDriver driver) {
        super(driver);
    }

    public ReportPage open() {
        openTab("reports");
        return this;
    }

public boolean halamanTampil(){return  exists("")}
}
