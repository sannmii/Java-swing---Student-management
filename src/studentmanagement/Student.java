package studentmanagement;

import java.util.Date;

public class Student {

    private int studentId;
    private String name;
    private double gpa;
    private Date enrollmentDate;
    private int majorId;
    private String majorName;

    public Student() {}

    public Student(int studentId, String name, double gpa, Date enrollmentDate, int majorId, String majorName) {
        this.studentId = studentId;
        this.name = name;
        this.gpa = gpa;
        this.enrollmentDate = enrollmentDate;
        this.majorId = majorId;
        this.majorName = majorName;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public int getMajorId() { return majorId; }
    public void setMajorId(int majorId) { this.majorId = majorId; }

    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }

    @Override
    public String toString() {
        return name;
    }
}
