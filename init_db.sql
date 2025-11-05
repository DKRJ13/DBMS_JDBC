
DROP TABLE IF EXISTS StudentCourses;
DROP TABLE IF EXISTS CollegeCourses;
DROP TABLE IF EXISTS CoursePrerequisites;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS College;
DROP TABLE IF EXISTS Courses;

-- Create College table
CREATE TABLE College (
    college_id INT PRIMARY KEY,
    college_name VARCHAR(255),
    college_fees INT
);

-- Create Courses table
CREATE TABLE Courses (
    course_id INT PRIMARY KEY,
    course_name VARCHAR(255),
    course_duration INT
);

-- Create Student table (with added GPA column for enhancements)
CREATE TABLE Student (
    student_id INT PRIMARY KEY,
    student_name VARCHAR(255),
    student_age INT,
    college_id_choice INT,
    gpa FLOAT DEFAULT NULL,
    FOREIGN KEY (college_id_choice) REFERENCES College(college_id) ON DELETE SET NULL
);

-- Create CollegeCourses table (many-to-many between College and Courses)
CREATE TABLE CollegeCourses (
    college_id INT,   
    course_id INT,
    PRIMARY KEY (college_id, course_id),
    FOREIGN KEY (college_id) REFERENCES College(college_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE
);

-- Create StudentCourses table (many-to-many between Student and Courses, for direct enrollment)
CREATE TABLE StudentCourses (
    student_id INT,
    course_id INT,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES Student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE
);

-- Create CoursePrerequisites table (for new functionality: course prerequisites)
CREATE TABLE CoursePrerequisites (
    course_id INT,
    prerequisite_id INT,
    PRIMARY KEY (course_id, prerequisite_id),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_id) REFERENCES Courses(course_id) ON DELETE CASCADE
);

-- Insert Sample Data into College
INSERT INTO College VALUES (1, 'IITD', 1000000);
INSERT INTO College VALUES (2, 'IIITD', 3000000);
INSERT INTO College VALUES (3, 'NIFTB', 1500000);
INSERT INTO College VALUES (4, 'JNU', 800000);

-- Insert Sample Data into Courses
INSERT INTO Courses VALUES (1, 'CSAI', 5);
INSERT INTO Courses VALUES (2, 'MECH', 4);
INSERT INTO Courses VALUES (3, 'ELEC', 4);
INSERT INTO Courses VALUES (4, 'EVLSI', 5);
INSERT INTO Courses VALUES (5, 'DESIGN', 3);
INSERT INTO Courses VALUES (6, 'POLSCIE', 3);
INSERT INTO Courses VALUES (7, 'CSBS', 4);

-- Insert Sample Data into Student (with NULL GPA for now)
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (1, 'Raman', 18, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (2, 'Ayush', 18, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (3, 'Aditya', 19, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (4, 'Sarthak', 19, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (5, 'Rashmi', 18, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (6, 'Arjun', 18, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (7, 'Kunal', 19, NULL, NULL);
INSERT INTO Student (student_id, student_name, student_age, college_id_choice, gpa) VALUES (8, 'Yash', 19, NULL, NULL);

-- Update Student college_id_choice with valid values (fixed invalid 6 to 3)
UPDATE Student SET college_id_choice = 1 WHERE student_id = 1;
UPDATE Student SET college_id_choice = 4 WHERE student_id = 2;
UPDATE Student SET college_id_choice = 3 WHERE student_id = 3;  -- Fixed from 6
UPDATE Student SET college_id_choice = 2 WHERE student_id = 4;
UPDATE Student SET college_id_choice = 2 WHERE student_id = 5;
UPDATE Student SET college_id_choice = 1 WHERE student_id = 6;
UPDATE Student SET college_id_choice = 3 WHERE student_id = 7;  -- Fixed from 6
UPDATE Student SET college_id_choice = 4 WHERE student_id = 8;

-- Insert Sample Data into CollegeCourses
INSERT INTO CollegeCourses VALUES (1, 1);
INSERT INTO CollegeCourses VALUES (2, 4);
INSERT INTO CollegeCourses VALUES (1, 2);
INSERT INTO CollegeCourses VALUES (4, 6);
INSERT INTO CollegeCourses VALUES (2, 1);
INSERT INTO CollegeCourses VALUES (2, 7);
INSERT INTO CollegeCourses VALUES (1, 3);
INSERT INTO CollegeCourses VALUES (3, 5);

