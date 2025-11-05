// Improved Student-Course-College Database Management System using JDBC

// Key Improvements:
// 1. Switched to PreparedStatements to prevent SQL injection and improve security.
// 2. Added resource management with try-with-resources where possible.
// 3. Improved error handling with more informative messages and transaction management.
// 4. Modularized the code: Created separate methods for each operation, improved menu structure.
// 5. Added input validation: Check if IDs exist before operations to avoid invalid actions.
// 6. Fixed bugs: Removed duplicated choice==12, fixed redundant connection attempts, corrected initial data issues in comments.
// 7. Added new functionalities:
//    - Added StudentCourses table for direct student-course enrollment (many-to-many).
//    - New options: Enroll student in course, remove student from course, print student's enrolled courses.
//    - Update student details (name, age).
//    - Update course details (name, duration).
//    - Print all students, all colleges, all courses.
//    - Generate simple reports: Number of students per college, average age per college.
//    - Search students by name.
//    - Remove course from college.
// 8. Enhanced menu with more options and better user experience.
// 9. Set auto-commit to false at start, with explicit commit/rollback.
// 10. Used OOP lightly: Could add entity classes, but kept simple for now.
// 11. Assumed mysql-connector-java.jar is in classpath.

// Updated SQL Schema (add this to your SQL file):
// ADD TO EXISTING SCHEMA:
// CREATE TABLE StudentCourses (
//     student_id INT,
//     course_id INT,
//     PRIMARY KEY (student_id, course_id),
//     FOREIGN KEY (student_id) REFERENCES Student(student_id) ON DELETE CASCADE,
//     FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE
// );

// Note: Fix initial data - college_id_choice should reference valid colleges (1-4 in sample).
// In sample updates, change invalid 6 to valid IDs, e.g., 3.

// Import required packages
import java.sql.*;
import java.util.Scanner;

public class JdbcDemo {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/companydb?allowPublicKeyRetrieval=true&useSSL=false";

    // Database credentials
    static final String USER = "daksh"; // add your user
    static final String PASSWORD = "root"; // add password

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Manage transactions manually
            stmt = conn.createStatement();

            // Menu loop
            Scanner sc = new Scanner(System.in);
            while (true) {
                displayMenu();
                System.out.print("Enter your choice: ");
                int choice = getIntInput(sc);

                switch (choice) {
                    case 1 -> addStudent(conn);
                    case 2 -> addCollege(conn);
                    case 3 -> addCourse(conn);
                    case 4 -> addStudentToCollege(conn);
                    case 5 -> updateCollegeChoice(conn);
                    case 6 -> deleteStudentFromCollege(conn);
                    case 7 -> deleteCourse(conn);
                    case 8 -> deleteStudent(conn);
                    case 9 -> deleteCollege(conn);
                    case 10 -> updateCollegeFee(conn);
                    case 11 -> printAllStudentsOfCollege(conn);
                    case 12 -> printAllCoursesOfCollege(conn);
                    case 13 -> addNewCollegeAndUpdateStudentChoice(conn);
                    case 14 -> addCourseToCollege(conn);
                    case 15 -> enrollStudentInCourse(conn);
                    case 16 -> removeStudentFromCourse(conn);
                    case 17 -> printStudentEnrolledCourses(conn);
                    case 18 -> updateStudentDetails(conn);
                    case 19 -> updateCourseDetails(conn);
                    case 20 -> printAllStudents(conn);
                    case 21 -> printAllColleges(conn);
                    case 22 -> printAllCourses(conn);
                    case 23 -> printStudentsPerCollegeReport(conn);
                    case 24 -> printAverageAgePerCollegeReport(conn);
                    case 25 -> searchStudentsByName(conn);
                    case 26 -> removeCourseFromCollege(conn);
                    case 27 -> {
                        System.out.println("Committing changes...");
                        conn.commit();
                    }
                    case 28 -> {
                        System.out.println("Rolling back to last commit...");
                        conn.rollback();
                    }
                    default -> {
                        System.out.println("Exiting program...");
                        return;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Clean-up environment
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("End of Code");
    }

    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add a Student");
        System.out.println("2. Add a College");
        System.out.println("3. Add a Course");
        System.out.println("4. Add Student to College");
        System.out.println("5. Update College Choice for Student");
        System.out.println("6. Delete Student from College");
        System.out.println("7. Delete Course");
        System.out.println("8. Delete Student");
        System.out.println("9. Delete College");
        System.out.println("10. Update College Fee");
        System.out.println("11. Print All Students of a College");
        System.out.println("12. Print All Courses of a College");
        System.out.println("13. Add New College and Update Student Choice");
        System.out.println("14. Add Course to College");
        System.out.println("15. Enroll Student in Course");
        System.out.println("16. Remove Student from Course");
        System.out.println("17. Print Student's Enrolled Courses");
        System.out.println("18. Update Student Details");
        System.out.println("19. Update Course Details");
        System.out.println("20. Print All Students");
        System.out.println("21. Print All Colleges");
        System.out.println("22. Print All Courses");
        System.out.println("23. Report: Students per College");
        System.out.println("24. Report: Average Age per College");
        System.out.println("25. Search Students by Name");
        System.out.println("26. Remove Course from College");
        System.out.println("27. Commit Changes");
        System.out.println("28. Rollback to Last Commit");
        System.out.println("Any other number: Exit");
    }

    // Helper to get integer input with validation
    private static int getIntInput(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Enter a number:");
            sc.next();
        }
        return sc.nextInt();
    }

    // Helper to check if ID exists
    private static boolean idExists(Connection conn, String table, String idColumn, int id) throws SQLException {
        String query = "SELECT 1 FROM " + table + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    static void addStudent(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student ID already exists.");
            return;
        }
        System.out.print("Enter student name: ");
        String studentName = sc.next();
        System.out.print("Enter student age: ");
        int studentAge = getIntInput(sc);
        System.out.print("Enter college choice ID (or 0 for none): ");
        int collegeIdChoice = getIntInput(sc);
        if (collegeIdChoice != 0 && !idExists(conn, "College", "college_id", collegeIdChoice)) {
            System.out.println("Invalid college ID.");
            return;
        }

        String query = "INSERT INTO Student (student_id, student_name, student_age, college_id_choice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, studentName);
            pstmt.setInt(3, studentAge);
            pstmt.setObject(4, collegeIdChoice == 0 ? null : collegeIdChoice);
            pstmt.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    static void addCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College ID already exists.");
            return;
        }
        System.out.print("Enter college name: ");
        String collegeName = sc.next();
        System.out.print("Enter college fees: ");
        int collegeFees = getIntInput(sc);

        String query = "INSERT INTO College (college_id, college_name, college_fees) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setString(2, collegeName);
            pstmt.setInt(3, collegeFees);
            pstmt.executeUpdate();
            System.out.println("College added successfully!");
        }
    }

    static void addCourse(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course ID already exists.");
            return;
        }
        System.out.print("Enter course name: ");
        String courseName = sc.next();
        System.out.print("Enter course duration: ");
        int courseDuration = getIntInput(sc);

        String query = "INSERT INTO Courses (course_id, course_name, course_duration) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, courseName);
            pstmt.setInt(3, courseDuration);
            pstmt.executeUpdate();
            System.out.println("Course added successfully!");
        }
    }

    static void addStudentToCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }

        String query = "UPDATE Student SET college_id_choice = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setInt(2, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student added to college successfully!");
            }
        }
    }

    static void updateCollegeChoice(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter new college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }

        String query = "UPDATE Student SET college_id_choice = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setInt(2, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("College choice updated successfully!");
            }
        }
    }

    static void deleteStudentFromCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }

        String query = "UPDATE Student SET college_id_choice = NULL WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student removed from college successfully!");
            }
        }
    }

    static void deleteCourse(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }

        String query = "DELETE FROM Courses WHERE course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Course deleted successfully!");
            }
        }
    }

    static void deleteStudent(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }

        String query = "DELETE FROM Student WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            }
        }
    }

    static void deleteCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }

        String query = "DELETE FROM College WHERE college_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("College deleted successfully!");
            }
        }
    }

    static void updateCollegeFee(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }
        System.out.print("Enter new fees: ");
        int newFees = getIntInput(sc);

        String query = "UPDATE College SET college_fees = ? WHERE college_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newFees);
            pstmt.setInt(2, collegeId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("College fee updated successfully!");
            }
        }
    }

    static void printAllStudentsOfCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }

        String query = "SELECT * FROM Student WHERE college_id_choice = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Students in College " + collegeId + ":");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + ", Name: " + rs.getString("student_name") + ", Age: " + rs.getInt("student_age"));
            }
        }
    }

    static void printAllCoursesOfCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }

        String query = "SELECT c.course_id, c.course_name, c.course_duration FROM CollegeCourses cc JOIN Courses c ON cc.course_id = c.course_id WHERE cc.college_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Courses in College " + collegeId + ":");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("course_id") + ", Name: " + rs.getString("course_name") + ", Duration: " + rs.getInt("course_duration"));
            }
        }
    }

    static void addNewCollegeAndUpdateStudentChoice(Connection conn) throws SQLException {
        addCollege(conn); // Add new college first
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the new college ID to assign to student: ");
        int collegeId = getIntInput(sc);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }

        String query = "UPDATE Student SET college_id_choice = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setInt(2, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student's college choice updated to new college!");
            }
        }
    }

    static void addCourseToCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }

        // Check if already added
        String checkQuery = "SELECT 1 FROM CollegeCourses WHERE college_id = ? AND course_id = ?";
        try (PreparedStatement checkPstmt = conn.prepareStatement(checkQuery)) {
            checkPstmt.setInt(1, collegeId);
            checkPstmt.setInt(2, courseId);
            if (checkPstmt.executeQuery().next()) {
                System.out.println("Course already added to college.");
                return;
            }
        }

        String query = "INSERT INTO CollegeCourses (college_id, course_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
            System.out.println("Course added to college successfully!");
        }
    }

    static void enrollStudentInCourse(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }

        // Optional: Check if course is offered by student's college
        String checkCollegeQuery = "SELECT 1 FROM Student s JOIN CollegeCourses cc ON s.college_id_choice = cc.college_id WHERE s.student_id = ? AND cc.course_id = ?";
        try (PreparedStatement checkPstmt = conn.prepareStatement(checkCollegeQuery)) {
            checkPstmt.setInt(1, studentId);
            checkPstmt.setInt(2, courseId);
            if (!checkPstmt.executeQuery().next()) {
                System.out.println("Warning: Course may not be offered by student's college.");
            }
        }

        // Check if already enrolled
        String checkQuery = "SELECT 1 FROM StudentCourses WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement checkPstmt = conn.prepareStatement(checkQuery)) {
            checkPstmt.setInt(1, studentId);
            checkPstmt.setInt(2, courseId);
            if (checkPstmt.executeQuery().next()) {
                System.out.println("Student already enrolled in course.");
                return;
            }
        }

        String query = "INSERT INTO StudentCourses (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
            System.out.println("Student enrolled in course successfully!");
        }
    }

    static void removeStudentFromCourse(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }

        String query = "DELETE FROM StudentCourses WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student removed from course successfully!");
            } else {
                System.out.println("Enrollment not found.");
            }
        }
    }

    static void printStudentEnrolledCourses(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }

        String query = "SELECT c.course_id, c.course_name, c.course_duration FROM StudentCourses sc JOIN Courses c ON sc.course_id = c.course_id WHERE sc.student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Enrolled Courses for Student " + studentId + ":");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("course_id") + ", Name: " + rs.getString("course_name") + ", Duration: " + rs.getInt("course_duration"));
            }
        }
    }

    static void updateStudentDetails(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = getIntInput(sc);
        if (!idExists(conn, "Student", "student_id", studentId)) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter new name (or press enter to skip): ");
        sc.nextLine(); // Consume newline
        String newName = sc.nextLine();
        System.out.print("Enter new age (or 0 to skip): ");
        int newAge = getIntInput(sc);

        StringBuilder queryBuilder = new StringBuilder("UPDATE Student SET ");
        boolean hasUpdate = false;
        if (!newName.isEmpty()) {
            queryBuilder.append("student_name = ?, ");
            hasUpdate = true;
        }
        if (newAge > 0) {
            queryBuilder.append("student_age = ?, ");
            hasUpdate = true;
        }
        if (!hasUpdate) {
            System.out.println("No updates provided.");
            return;
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // Remove last comma
        queryBuilder.append(" WHERE student_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            if (!newName.isEmpty()) pstmt.setString(paramIndex++, newName);
            if (newAge > 0) pstmt.setInt(paramIndex++, newAge);
            pstmt.setInt(paramIndex, studentId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student details updated successfully!");
            }
        }
    }

    static void updateCourseDetails(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }
        System.out.print("Enter new name (or press enter to skip): ");
        sc.nextLine(); // Consume newline
        String newName = sc.nextLine();
        System.out.print("Enter new duration (or 0 to skip): ");
        int newDuration = getIntInput(sc);

        StringBuilder queryBuilder = new StringBuilder("UPDATE Courses SET ");
        boolean hasUpdate = false;
        if (!newName.isEmpty()) {
            queryBuilder.append("course_name = ?, ");
            hasUpdate = true;
        }
        if (newDuration > 0) {
            queryBuilder.append("course_duration = ?, ");
            hasUpdate = true;
        }
        if (!hasUpdate) {
            System.out.println("No updates provided.");
            return;
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // Remove last comma
        queryBuilder.append(" WHERE course_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            if (!newName.isEmpty()) pstmt.setString(paramIndex++, newName);
            if (newDuration > 0) pstmt.setInt(paramIndex++, newDuration);
            pstmt.setInt(paramIndex, courseId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Course details updated successfully!");
            }
        }
    }

    static void printAllStudents(Connection conn) throws SQLException {
        String query = "SELECT * FROM Student";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("All Students:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + ", Name: " + rs.getString("student_name") + ", Age: " + rs.getInt("student_age") + ", College: " + rs.getObject("college_id_choice"));
            }
        }
    }

    static void printAllColleges(Connection conn) throws SQLException {
        String query = "SELECT * FROM College";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("All Colleges:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("college_id") + ", Name: " + rs.getString("college_name") + ", Fees: " + rs.getInt("college_fees"));
            }
        }
    }

    static void printAllCourses(Connection conn) throws SQLException {
        String query = "SELECT * FROM Courses";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("All Courses:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("course_id") + ", Name: " + rs.getString("course_name") + ", Duration: " + rs.getInt("course_duration"));
            }
        }
    }

    static void printStudentsPerCollegeReport(Connection conn) throws SQLException {
        String query = "SELECT college_id_choice, COUNT(*) AS num_students FROM Student WHERE college_id_choice IS NOT NULL GROUP BY college_id_choice";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Students per College Report:");
            while (rs.next()) {
                System.out.println("College ID: " + rs.getInt("college_id_choice") + ", Students: " + rs.getInt("num_students"));
            }
        }
    }

    static void printAverageAgePerCollegeReport(Connection conn) throws SQLException {
        String query = "SELECT college_id_choice, AVG(student_age) AS avg_age FROM Student WHERE college_id_choice IS NOT NULL GROUP BY college_id_choice";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Average Age per College Report:");
            while (rs.next()) {
                System.out.println("College ID: " + rs.getInt("college_id_choice") + ", Avg Age: " + rs.getDouble("avg_age"));
            }
        }
    }

    static void searchStudentsByName(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter student name (partial match): ");
        String name = sc.nextLine();

        String query = "SELECT * FROM Student WHERE student_name LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Search Results:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + ", Name: " + rs.getString("student_name") + ", Age: " + rs.getInt("student_age") + ", College: " + rs.getObject("college_id_choice"));
            }
        }
    }

    static void removeCourseFromCollege(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter college ID: ");
        int collegeId = getIntInput(sc);
        if (!idExists(conn, "College", "college_id", collegeId)) {
            System.out.println("College not found.");
            return;
        }
        System.out.print("Enter course ID: ");
        int courseId = getIntInput(sc);
        if (!idExists(conn, "Courses", "course_id", courseId)) {
            System.out.println("Course not found.");
            return;
        }

        String query = "DELETE FROM CollegeCourses WHERE college_id = ? AND course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, collegeId);
            pstmt.setInt(2, courseId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Course removed from college successfully!");
            } else {
                System.out.println("Association not found.");
            }
        }
    }
}