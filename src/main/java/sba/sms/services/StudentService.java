package sba.sms.services;

import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;


import java.util.ArrayList;
import java.util.List;
import java.lang.*;

//import static jdk.vm.ci.meta.JavaKind.Object;
//import static sba.sms.utils.HibernateUtil.sessionFactory;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods and
 * provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */

public class StudentService implements StudentI {

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("from Student", Student.class);
            students = query.list();
        } catch (HibernateException e) {
            e.getMessage();
        }
        return students;
    }

    @Override
    public void createStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        Student student = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            student = session.get(Student.class, email);
        } catch (HibernateException e) {
            e.getMessage();
        }
        return student;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        boolean isValid = false;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("from Student where email = :email and password = :password", Student.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            Student student = query.uniqueResult();

            if (student != null) {
                String storedHashedPassword = student.getPassword();
                isValid = student.getPassword().equals(storedHashedPassword);
            }//isValid = query.uniqueResult() != null;
        } catch (HibernateException e) {
            e.getMessage();
        }
        return isValid;
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, email);
            Course course = session.get(Course.class, courseId);
            if (student != null && course != null) {
                student.getCourses().add(course);
                session.merge(student);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.getMessage();
        }
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        Transaction transaction = null;
        List<Course> courses = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Begin transaction
            transaction = session.beginTransaction();

            // Fetch student and check existence
            Student student = session.createQuery("from Student where email = :email", Student.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (student != null) {
                // Fetch courses for the student
                Query<Course> query = session.createQuery("select c from Course c join c.students s where s.email = :email", Course.class);
                query.setParameter("email", email);
                courses = query.list();
            }

            // Commit the transaction
            transaction.commit();
        } catch (HibernateException e) {
            // Rollback transaction if any error occurs
            if (transaction != null) {
                transaction.rollback();
            }
            // Print stack trace for debugging
            e.printStackTrace();
        }
        return courses;
    }
}


