CREATE DATABASE lms;

USE lms;

CREATE TABLE employee (
                          employeeId VARCHAR(10) PRIMARY KEY,
                          firstName VARCHAR(30),
                          lastName VARCHAR(30),
                          dateOfBirth DATE,
                          gender ENUM('Male', 'Female'),
                          joinDate DATE,
                          NIC VARCHAR(15),
                          address VARCHAR(50),
                          contactNo VARCHAR(10),
                          email VARCHAR(50),
                          role VARCHAR(20)
);

CREATE TABLE user (
                      userId VARCHAR(50) PRIMARY KEY,
                      userName VARCHAR(30),
                      password VARCHAR(50),
                      employeeId VARCHAR(10),
                      FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE salary (
                        salaryId INT PRIMARY KEY AUTO_INCREMENT,
                        monthOfPay VARCHAR(15),
                        date DATE,
                        basicPayment DECIMAL(10,2),
                        OTPayment DECIMAL(10,2),
                        employeeId VARCHAR(10),
                        FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE student (
                         studentId VARCHAR(10) PRIMARY KEY,
                         firstName VARCHAR(30),
                         lastName VARCHAR(30),
                         dateOfBirth DATE,
                         gender ENUM('Male', 'Female'),
                         admissionDate DATE,
                         NIC VARCHAR(15),
                         address VARCHAR(50),
                         contactNo VARCHAR(10),
                         email VARCHAR(50)
);

CREATE TABLE studentDetails (
                                employeeId VARCHAR(10),
                                FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE,
                                studentId VARCHAR(10),
                                FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE attendance (
                            attendanceId INT PRIMARY KEY AUTO_INCREMENT,
                            date DATE,
                            inTime TIME,
                            outTime TIME,
                            status ENUM('Absent', 'Present'),
                            employeeId VARCHAR(10),
                            FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE,
                            studentId VARCHAR(10),
                            FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE vehicle (
                         vehicleId VARCHAR(10) PRIMARY KEY,
                         type VARCHAR(30),
                         model VARCHAR(30),
                         fuelType VARCHAR(30),
                         transmission VARCHAR(30),
                         registrationNumber VARCHAR(30),
                         status ENUM('Active', 'Inactive')
);

CREATE TABLE vehicleDetails (
                                vehicleId VARCHAR(10),
                                FOREIGN KEY (vehicleId) REFERENCES vehicle (vehicleId) ON UPDATE CASCADE ON DELETE CASCADE,
                                employeeId VARCHAR(10),
                                FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE vehicleMaintenance (
                                    maintenanceId VARCHAR(10) PRIMARY KEY,
                                    description VARCHAR(50),
                                    date DATE,
                                    cost DECIMAL(10,2),
                                    vehicleId VARCHAR(10),
                                    FOREIGN KEY (vehicleId) REFERENCES vehicle (vehicleId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE exam (
                      examId VARCHAR(10) PRIMARY KEY,
                      date DATE,
                      type VARCHAR(30),
                      description VARCHAR(60)
);

CREATE TABLE examDetails (
                             examId VARCHAR(10),
                             FOREIGN KEY (examId) REFERENCES exam (examId) ON UPDATE CASCADE ON DELETE CASCADE,
                             result ENUM('Pass', 'Fail'),
                             studentId VARCHAR(10),
                             FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE course (
                        courseId VARCHAR(10) PRIMARY KEY,
                        name VARCHAR(30),
                        description VARCHAR(100),
                        duration VARCHAR(10),
                        price DECIMAL(10,2)
);

CREATE TABLE courseDetails (
                               studentId VARCHAR(10),
                               FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE,
                               courseId VARCHAR(10),
                               FOREIGN KEY (courseId) REFERENCES course (courseId) ON UPDATE CASCADE ON DELETE CASCADE,
                               status ENUM('Ongoing', 'Completed')
);

CREATE TABLE payment (
                                paymentId VARCHAR(10) PRIMARY KEY,
                                description VARCHAR(50),
                                date DATE,
                                method VARCHAR(30),
                                amount DECIMAL(10,2),
                                studentId VARCHAR(10),
                                FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE paymentDetails(
                               paymentId VARCHAR(10),
                               FOREIGN KEY (paymentId) REFERENCES payment (paymentId) ON UPDATE CASCADE ON DELETE CASCADE,
                               courseId VARCHAR(10),
                               FOREIGN KEY (courseId) REFERENCES course (courseId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE lessonSchedule (
                                lessonId VARCHAR(10) PRIMARY KEY,
                                date DATE,
                                time TIME,
                                employeeId VARCHAR(10),
                                FOREIGN KEY (employeeId) REFERENCES employee (employeeId) ON UPDATE CASCADE ON DELETE CASCADE,
                                courseId VARCHAR(10),
                                FOREIGN KEY (courseId) REFERENCES course (courseId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE lessonDetails (
                               lessonId VARCHAR(10),
                               FOREIGN KEY (lessonId) REFERENCES lessonSchedule (lessonId) ON UPDATE CASCADE ON DELETE CASCADE,
                               studentId VARCHAR(10),
                               FOREIGN KEY (studentId) REFERENCES student (studentId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE note (
    noteId INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100),
    userId VARCHAR(50),
    FOREIGN KEY (userId) REFERENCES user (userId) ON UPDATE CASCADE ON DELETE CASCADE
);


