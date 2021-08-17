package com.triton.johnson.requestpojo;

public class CMRLTicketListRequest {

    /**
     * type : 1
     * station_id : 611510c34f912e1856fc6d44
     * break_down_reported_by : 6113acf26ee293224d81081c
     */

    private String type;
    private String station_id;
    private String break_down_reported_by;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getBreak_down_reported_by() {
        return break_down_reported_by;
    }

    public void setBreak_down_reported_by(String break_down_reported_by) {
        this.break_down_reported_by = break_down_reported_by;
    }
}
