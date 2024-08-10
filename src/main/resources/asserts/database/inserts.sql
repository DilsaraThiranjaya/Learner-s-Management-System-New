
-- Insert records into employee table
INSERT INTO employee (employeeId, firstName, lastName, dateOfBirth, gender, joinDate, NIC, address, contactNo, email, role) VALUES
                                                                                                                                        ('E001', 'Kamal', 'Perera', '1985-06-12', 'Male', '2015-03-01', '850612456789V', '16, Main Street, Colombo 03', '0771234567', 'kamal@email.com', 'Instructor'),
                                                                                                                                        ('E002', 'Nimal', 'Kumara', '1990-11-25', 'Male', '2018-07-15', '901125678901V', '29/A, Galle Road, Kalutara', '0772345678', 'nimal@email.com', 'Admin'),
                                                                                                                                        ('E003', 'Sunil', 'Rathnayake', '1978-03-04', 'Male', '2010-01-01', '780304567891V', '51, Kandy Road, Kurunegala', '0773456789', 'sunil@email.com', 'Admin'),
                                                                                                                                        ('E004', 'Priyantha', 'Dissanayake', '1982-08-20', 'Male', '2012-05-01', '820820456789V', '27/B, Galle Road, Panadura', '0774567890', 'priyantha@email.com', 'Instructor'),
                                                                                                                                        ('E005', 'Dilani', 'Fernando', '1988-12-15', 'Female', '2016-09-01', '881215678901V', '54, Kandy Road, Matale', '0775678901', 'dilani@email.com', 'Instructor');

-- Insert records into user table
INSERT INTO user (userId, userName, password, employeeId) VALUES
                                                                ('Dilsara076', 'Dilsara', '07614', 'E001'),
                                                                ('Supun123', 'Supun', '07614', 'E002');

-- Insert records into salary table
INSERT INTO salary (monthOfPay, date, basicPayment, OTPayment, employeeId) VALUES
                                                                                         ('January 2023', '2023-01-31', 50000.00, 5000.00, 'E001'),
                                                                                         ('February 2023', '2023-02-28', 45000.00, 3000.00, 'E002'),
                                                                                         ('March 2023', '2023-03-31', 60000.00, 7500.00, 'E003');

-- Insert records into student table
INSERT INTO student (studentId, firstName, lastName, dateOfBirth, gender, admissionDate, NIC, address, contactNo, email) VALUES
                                                                                                                             ('ST001', 'Amila', 'Kumari', '2000-09-15','Female', '2015-05-12', '200015600123', '5A, Hantana Road, Kandy', '0771234567', 'amila@email.com'),
                                                                                                                             ('ST002', 'Rasika', 'Perera', '1998-03-22','Male', '2018-09-25', '980322456789', '12, Negombo Road, Wattala', '0772345678', 'rasika@email.com'),
                                                                                                                             ('ST003', 'Saman', 'Jayasinghe', '2002-11-05','Male', '2022-07-06', '200211056789', '27, Main Street, Matara', '0773456789', 'saman@email.com');

-- Insert records into studentDetails table
INSERT INTO studentDetails (employeeId, studentId) VALUES
                                                       ('E002', 'ST001'),
                                                       ('E002', 'ST002'),
                                                       ('E003', 'ST003');

-- Insert records into attendance table
INSERT INTO attendance (date, inTime, outTime, status, employeeId, studentId) VALUES
                                                                               ('2023-04-01', '10:00:00', '12:00:00', 'Present', NULL, 'ST001'),
                                                                               ('2023-04-02', '08:30:00', '17:00:00', 'Absent', 'E002', NULL),
                                                                               ('2023-04-03', '09:00:00', '17:00:00', 'Present', 'E001', NULL);

-- Insert records into vehicle table
INSERT INTO vehicle (vehicleId, type, model, fuelType, transmission, registrationNumber, status) VALUES
                                                                                             ('V001', 'Car', 'Toyota Axio', 'Petrol', 'Automatic', 'WP KM 2345', 'Active'),
                                                                                             ('V002', 'Van', 'Nissan Caravan', 'Diesel', 'Manual', 'GF 6789', 'Active'),
                                                                                             ('V003', 'Bike', 'Honda CB500X', 'Petrol', 'Manual', '130-4567', 'Inactive');

-- Insert records into vehicleDetails table
INSERT INTO vehicleDetails (vehicleId, employeeId) VALUES
                                                               ('V001', 'E001'),
                                                               ('V002', 'E004'),
                                                               ('V003', 'E005');

-- Insert records into vehicleMaintenance table
INSERT INTO vehicleMaintenance (maintenanceId, description, date, cost, vehicleId) VALUES
                                                                                       ('M001', 'Oil Change', '2023-03-01', 5000.00, 'V001'),
                                                                                       ('M002', 'Tyre Replacement', '2023-02-15', 25000.00, 'V002'),
                                                                                       ('M003', 'Brake Repair', '2023-04-01', 12000.00, 'V003');

-- Insert records into exam table
INSERT INTO exam (examId, date, type, description) VALUES
                                          ('EX001', '2023-06-01', 'Theory', 'Training Exam'),
                                          ('EX002', '2023-06-15', 'Theory', 'Final Exam'),
                                          ('EX003', '2023-07-01', 'Practical', '''Final Trial''');

-- Insert records into examDetails table
INSERT INTO examDetails (examId, result, studentId) VALUES
                                                        ('EX001', 'Pass', 'ST001'),
                                                        ('EX002', 'Fail', 'ST002'),
                                                        ('EX002', 'Pass', 'ST003'),
                                                        ('EX003', 'Fail', 'ST003'),
                                                        ('EX003', 'Pass', 'ST001');

-- Insert records into course table
INSERT INTO course (courseId, name, description, duration, price) VALUES
                                                                      ('C001', 'Car Driving Lessons', 'Learn to drive a car (manual/automatic transmission)', '3 months', 25000.00),
                                                                      ('C002', 'Bike Driving Lessons', 'Learn to ride a motorcycle/scooter', '2 months', 8000.00),
                                                                      ('C003', 'Defensive Driving Course', 'Techniques for safe and defensive driving', '1 month', 10000.00),
                                                                      ('C004', 'Refresher Driving Course', 'Brush up on driving skills and road rules', '2 weeks', 5000.00),
                                                                      ('C005', 'Treewheel Driving Course', 'Learn to drive a treewheel', '3 month', 12000.00);



-- Insert records into lessonSchedule table
INSERT INTO lessonSchedule (lessonId, date, time, employeeId, courseId) VALUES
                                                                            ('L001', '2023-04-10', '09:00:00', 'E001', 'C001'),
                                                                            ('L002', '2023-04-15', '14:00:00', 'E002', 'C002'),
                                                                            ('L003', '2023-04-20', '16:30:00', 'E003', 'C003');

-- Insert records into lessonDetails table
INSERT INTO lessonDetails (lessonId, studentId) VALUES
                                                    ('L001', 'ST001'),
                                                    ('L002', 'ST002'),
                                                    ('L003', 'ST003');

-- Insert records into lessonDetails table
INSERT INTO note (description, userId) VALUES
                                                    ('Note 1', 'Dilsara076'),
                                                    ('Note 2', 'Supun123');