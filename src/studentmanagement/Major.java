package studentmanagement;

public class Major {

    private int majorId;
    private String majorName;

    public Major() {}

    public Major(int majorId, String majorName) {
        this.majorId = majorId;
        this.majorName = majorName;
    }

    public int getMajorId() { return majorId; }
    public void setMajorId(int majorId) { this.majorId = majorId; }

    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }

    @Override
    public String toString() {
        return majorName;
    }
}
