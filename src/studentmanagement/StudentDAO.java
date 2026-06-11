package studentmanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import studentmanagement.DatabaseConnection;
import studentmanagement.Major;
import studentmanagement.Major;
import studentmanagement.Student;
import studentmanagement.Student;

public class StudentDAO {

    // ==================== MAJOR OPERATIONS ====================

    public List<Major> getAllMajors() {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT * FROM major ORDER BY major_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getInt("major_id"));
                m.setMajorName(rs.getString("major_name"));
                majors.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching majors: " + e.getMessage());
        }
        return majors;
    }

    public boolean addMajor(String majorName) {
        String sql = "INSERT INTO major (major_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, majorName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding major: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMajor(int majorId) {
        String sql = "DELETE FROM major WHERE major_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, majorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting major: " + e.getMessage());
            return false;
        }
    }

    // ==================== STUDENT OPERATIONS ====================

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, m.major_name FROM students s "
                   + "LEFT JOIN major m ON s.major_id = m.major_id "
                   + "ORDER BY s.student_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return students;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, m.major_name FROM students s "
                   + "LEFT JOIN major m ON s.major_id = m.major_id "
                   + "WHERE s.name LIKE ? OR CAST(s.student_id AS CHAR) LIKE ? "
                   + "ORDER BY s.student_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        return students;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT s.*, m.major_name FROM students s "
                   + "LEFT JOIN major m ON s.major_id = m.major_id "
                   + "WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (name, gpa, enrollment_date, major_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setDouble(2, student.getGpa());
            ps.setDate(3, new java.sql.Date(student.getEnrollmentDate().getTime()));
            ps.setInt(4, student.getMajorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name=?, gpa=?, enrollment_date=?, major_id=? WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setDouble(2, student.getGpa());
            ps.setDate(3, new java.sql.Date(student.getEnrollmentDate().getTime()));
            ps.setInt(4, student.getMajorId());
            ps.setInt(5, student.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    private Student mapResultSet(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setGpa(rs.getDouble("gpa"));
        s.setEnrollmentDate(rs.getDate("enrollment_date"));
        s.setMajorId(rs.getInt("major_id"));
        s.setMajorName(rs.getString("major_name"));
        return s;
    }
}
