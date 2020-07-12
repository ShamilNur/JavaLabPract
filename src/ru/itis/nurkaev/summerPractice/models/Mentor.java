package ru.itis.nurkaev.summerPractice.models;

import java.util.Objects;

public class Mentor {
    private Long id;
    private String firstName;
    private String lastName;
    private String subject;
    private Student student;

    public Mentor(Long id, String firstName, String lastName, String subject, Student student) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mentor mentor = (Mentor) o;
        return Objects.equals(id, mentor.id) &&
                Objects.equals(firstName, mentor.firstName) &&
                Objects.equals(lastName, mentor.lastName) &&
                Objects.equals(subject, mentor.subject) &&
                Objects.equals(student, mentor.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, subject, student);
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "id=" + id +
                ", firstName='" + firstName.trim() + '\'' +
                ", lastName='" + lastName.trim() + '\'' +
                ", subject='" + subject.trim() + '\'' +
                ", studentId=" + student.getId() +
                '}';
    }
}
