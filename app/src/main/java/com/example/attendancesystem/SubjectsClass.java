package com.example.attendancesystem;

public class SubjectsClass {
    String sapid, course, subject, teacher;

    public SubjectsClass(String sapid, String course, String subject, String teacher) {
        this.sapid = sapid;
        this.course = course;
        this.subject = subject;
        this.teacher = teacher;
    }

    public String getSapid() {
        return sapid;
    }

    public void setSapid(String sapid) {
        this.sapid = sapid;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
