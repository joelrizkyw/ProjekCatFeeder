package com.binus.catfeeder.Data;

import com.google.gson.annotations.SerializedName;

public class VirtualPin {

    @SerializedName("v0")
    private String timeSchedule1;

    @SerializedName("v1")
    private String timeSchedule2;

    @SerializedName("v2")
    private String numOfSpin;

    public String getTimeSchedule1() {
        return timeSchedule1;
    }

    public String getTimeSchedule2() {
        return timeSchedule2;
    }

    public String getNumOfSpin() {
        return numOfSpin;
    }
}
