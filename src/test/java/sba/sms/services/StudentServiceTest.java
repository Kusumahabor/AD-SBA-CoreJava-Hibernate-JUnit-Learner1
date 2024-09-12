package sba.sms.services;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import sba.sms.dao.StudentI;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static javax.management.Query.eq;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static sba.sms.utils.HibernateUtil.buildSessionFactory;


class StudentServiceTest {
    //SessionFactory sessionFactory = buildSessionFactory();

    private StudentService studentService;
    private SessionFactory sessionFactory;
    private Session session;
    private StandardServiceRegistry serviceRegistry;
    //private Query<Student> query;

    @BeforeEach
    public void setUp() {
        studentService = new StudentService();

        MockitoAnnotations.openMocks(this);

        // Create the StandardServiceRegistry
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate-test.cfg.xml") // Adjust the configuration file if needed
                .build();

        // Create the Metadata
        Metadata metadata = new MetadataSources(serviceRegistry)
                .getMetadataBuilder()
                .build();

        // Create the SessionFactory
        sessionFactory = metadata.getSessionFactoryBuilder().build();

        //StudentService studentService = new StudentService(sessionFactory);
        StudentService studentService = new StudentService();
    }

    @AfterEach
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    @Test
    public void testGetAllStudents() {
        Student student = new Student();
        List<Student> students = new ArrayList<>();
        student.setEmail("reema@gmail.com");
        student.setName("reema brown");
        student.setPassword("password");
        students = studentService.getAllStudents();
    }

    @Test
    public void testCreateStudent() {
        Student student = new Student();
        student.setEmail("reema@gmail.com");
        student.setName("reema brown");
        student.setPassword("password");

        studentService.createStudent(student);
        assertNotNull(student.getEmail());
    }

    @Test
    public void testGetStudentByEmail() {
        Student student = new Student();
        student.setEmail("reema@gmail.com");
        student.setName("reema brown");
        student.setPassword("password");
        student = studentService.getStudentByEmail(student.getEmail());
    }
}




