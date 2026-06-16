package com.certimaster.productivity.adapter.web.request;

/** Choose a Pomodoro preset (CLASSIC or DEEP); minutes are derived server-side. */
public class PomodoroSettingRequest {

    private String preset;

    public PomodoroSettingRequest() {
    }

    public String getPreset() { return preset; }
    public void setPreset(String preset) { this.preset = preset; }
}
