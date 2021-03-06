package com.example.attendancesystem;

public class LectureClass {
    private String course;
    private String start_time;
    private String end_time;
    private String lectId;

    public LectureClass() {
    }

    public LectureClass(String course, String start_time, String end_time, String lectId) {
        this.course = course;
        this.start_time = start_time;
        this.end_time = end_time;
        this.lectId = lectId;
    }

    public String getLectId() {
        return lectId;
    }

    public void setLectId(String lectId) {
        this.lectId = lectId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
