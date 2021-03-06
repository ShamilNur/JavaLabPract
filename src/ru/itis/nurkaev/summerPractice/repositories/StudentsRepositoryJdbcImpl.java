/**
 * @nshamil Shamil Nurkaev
 * 11-905
 * Homework 3 (Repository)
 */

package ru.itis.nurkaev.summerPractice.repositories;

import ru.itis.nurkaev.summerPractice.models.Mentor;
import ru.itis.nurkaev.summerPractice.models.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static ru.itis.nurkaev.summerPractice.repositories.SqlRequests.*;

public class StudentsRepositoryJdbcImpl implements StudentsRepository {

    private final Connection connection;

    public StudentsRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {
        List<Student> students = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALLSTUDENTS_BY_AGE + age);
            while (resultSet.next()) {
                List<Mentor> mentors = new ArrayList<>();
                Student student = new Student(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("group_number"),
                        mentors);

                // getting all the mentors of the student by ID and putting to the student
                try (Statement statement1 = connection.createStatement()) {
                    getMentorByStudentId(statement1, mentors, student, student.getId());
                }
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_STUDENTS)) {
            if (resultSet.next()) {
                addNewStudent(students, resultSet);
            }

            long tempID = students.get(students.size() - 1).getId();
            while (resultSet.next()) {
                if (tempID == resultSet.getLong("s_id")) {
                    Student studentWithTempID = students.get(students.size() - 1);
                    Mentor mentor = new Mentor(
                            resultSet.getLong("m_id"),
                            resultSet.getString("m_first_name"),
                            resultSet.getString("m_last_name"),
                            resultSet.getString("subject_id"),
                            studentWithTempID);
                    studentWithTempID.getMentors().add(mentor);

                    // putting the subject field title in mentor instead subject_id
                    putTitleInsteadSubjectID(mentor);
                } else {
                    addNewStudent(students, resultSet);
                    tempID = students.get(students.size() - 1).getId();
                }
            }
            return students;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void addNewStudent(List<Student> students, ResultSet resultSet) throws SQLException {
        List<Mentor> mentors = new ArrayList<>();

        Student student = new Student(
                resultSet.getLong("s_id"),
                resultSet.getString("s_first_name"),
                resultSet.getString("s_last_name"),
                resultSet.getInt("age"),
                resultSet.getInt("group_number"),
                mentors);
        students.add(student);

        if (resultSet.getLong("m_id") != 0) {
            Mentor mentor = new Mentor(
                    resultSet.getLong("m_id"),
                    resultSet.getString("m_first_name"),
                    resultSet.getString("m_last_name"),
                    resultSet.getString("subject_id"),
                    student
            );

            // putting the subject field title in mentor instead subject_id
            putTitleInsteadSubjectID(mentor);
            mentors.add(mentor);
        }
    }

    @Override
    public Student findById(Long id) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_SELECT_BY_ID + id)) {
            if (resultSet.next()) {
                List<Mentor> mentors = new ArrayList<>();
                Student student = new Student(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("group_number"),
                        mentors);

                // getting all the mentors of the student by ID and putting to the student
                try (Statement statement1 = connection.createStatement()) {
                    getMentorByStudentId(statement1, mentors, student, id);
                }
                return student;
            } else return null;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void save(Student entity) {
        try (Statement statement = connection.createStatement()) {
            String insertStudentStatement = String.format(SQL_INSERT_STUDENT,
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getAge(),
                    entity.getGroupNumber());
            statement.executeUpdate(insertStudentStatement, Statement.RETURN_GENERATED_KEYS);
            // setting the value of the ID for the entity generated by the DB
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                long s_id = resultSet.getInt(1);
                entity.setId(s_id);
            }

            // putting all the mentors of the entity in the DB
            putMentors(entity, statement);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void update(Student entity) {
        try (Statement statement = connection.createStatement()) {
            // updating student by entity's ID
            String updateStudentStatement = String.format(SQL_SET_STUDENT_BY_ID,
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getAge(),
                    entity.getGroupNumber(),
                    entity.getId());
            statement.executeUpdate(updateStudentStatement);

            // updating mentors of the entity by their ID
            String deletePastMentorsStatement = String.format(SQL_DELETE_MENTORS_BY_STUDENTID, entity.getId());
            statement.executeUpdate(deletePastMentorsStatement);
            // putting all the mentors of the entity to the DB
            putMentors(entity, statement);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void putMentors(Student entity, Statement statement) {
        List<Mentor> mentors = entity.getMentors();
        for (Mentor mentor : mentors) {
            try {
                long subjectId;
                // make a request to get the subject ID
                ResultSet resultSet = statement.executeQuery(String.format(SQL_SELECT_SUBJECT_ID, mentor.getSubject()));
                // get the ID if the subject is already in the DB
                if (resultSet.next()) {
                    subjectId = resultSet.getLong("id");
                    // add subject to the DB if it is a new and get it's ID
                } else {
                    statement.executeUpdate(String.format(SQL_INSERT_SUBJECT, mentor.getSubject()),
                            Statement.RETURN_GENERATED_KEYS);
                    resultSet = statement.getGeneratedKeys();
                    resultSet.next();
                    subjectId = resultSet.getLong(1);
                }
                // putting all the mentors of the entity and extracting their ID
                String insertMentorStatement = String.format(SQL_INSERT_MENTOR,
                        mentor.getFirstName(),
                        mentor.getLastName(),
                        subjectId,
                        entity.getId());
                statement.executeUpdate(insertMentorStatement, Statement.RETURN_GENERATED_KEYS);
                try (ResultSet result = statement.getGeneratedKeys()) {
                    result.next();
                    long m_id = result.getInt(1);
                    mentor.setId(m_id);
                }
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private void getMentorByStudentId(Statement statement, List<Mentor> mentors, Student student, Long id) {
        // getting all the mentors of the student by ID and putting to the student
        try (ResultSet resultSet = statement.executeQuery(SQL_SELECT_MENTORS_BY_STUDENTID + id)) {
            while (resultSet.next()) {
                Mentor mentor = new Mentor(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("subject_id"),
                        student);
                mentors.add(mentor);

                // putting the subject field title in mentor instead subject_id
                putTitleInsteadSubjectID(mentor);
                mentors.add(mentor);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void putTitleInsteadSubjectID(Mentor mentor) throws SQLException {
        // putting the subject field title in mentor instead subject_id
        try (Statement statement1 = connection.createStatement();
             ResultSet resultSet1 = statement1.executeQuery(SQL_SELECT_MSUBJECT_BY_SUBJECTID +
                     Long.parseLong(mentor.getSubject()))) {
            if (resultSet1.next()) {
                mentor.setSubject(resultSet1.getString("title"));
            }
        }
    }
}