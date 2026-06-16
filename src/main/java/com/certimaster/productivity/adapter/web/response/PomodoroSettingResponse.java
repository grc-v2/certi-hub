package com.certimaster.productivity.adapter.web.response;

public class PomodoroSettingResponse {

    private String preset;
    private int focusMinutes;
    private int breakMinutes;

    public PomodoroSettingResponse() {
    }

    public PomodoroSettingResponse(String preset, int focusMinutes, int breakMinutes) {
        this.preset = preset;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
    }

    public String getPreset() { return preset; }
    public void setPreset(String preset) { this.preset = preset; }

    public int getFocusMinutes() { return focusMinutes; }
    public void setFocusMinutes(int focusMinutes) { this.focusMinutes = focusMinutes; }

    public int getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }
}
