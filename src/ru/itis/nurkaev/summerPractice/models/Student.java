/**
 * @nshamil Shamil Nurkaev
 * 11-905
 * Homework 3 (Repository)
 */

package ru.itis.nurkaev.summerPractice.models;

import java.util.List;
import java.util.Objects;

public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private int groupNumber;
    private List<Mentor> mentors;

    public Student(Long id, String firstName, String lastName, int age, int groupNumber, List<Mentor> mentors) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.groupNumber = groupNumber;
        this.mentors = mentors;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public List<Mentor> getMentors() {
        return mentors;
    }

    public void setMentors(List<Mentor> mentors) {
        this.mentors = mentors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age &&
                groupNumber == student.groupNumber &&
                Objects.equals(id, student.id) &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName) &&
                Objects.equals(mentors, student.mentors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, groupNumber, mentors);
    }

    @Override
    public String toString() {
        return String.format("Student{id=%d, firstName='%s', lastName='%s', age=%d, groupNumber=%d, mentors=%s}",
                id,
                firstName.trim(),
                lastName.trim(),
                age,
                groupNumber,
                mentors);
    }
}
