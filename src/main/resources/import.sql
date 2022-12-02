INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (1, 'Jan', 'Wawrzyniak', 'j.wawrzyniak@gmail.com', 22, 'ROBOTICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (2, 'Maciej', 'Komaranczuk', 'm.komaranczuk@gmail.com', 25, 'INFORMATICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (3, 'Weronika', 'Romanski', 'w.romanski@gmail.com', 20, 'ELECTRICAL');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (4, 'Robert', 'Kaczuk', 'r.kaczuk@gmail.com', 21, 'ELECTRONICS');

INSERT INTO teacher (id, first_name, last_name, email, age, `subject`) VALUES (1, 'Grzegorz', 'Kostro', 'g.kostro@gmail.com', 48, 'MATHS');
INSERT INTO teacher (id, first_name, last_name, email, age, `subject`) VALUES (2, 'Jagoda', 'Kowalska', 'j.kowalska@gmail.com', 33, 'SCIENCE');
INSERT INTO teacher (id, first_name, last_name, email, age, `subject`) VALUES (3, 'Adam', 'Batista', 'a.batista@gmail.com', 40, 'ART');
INSERT INTO teacher (id, first_name, last_name, email, age, `subject`) VALUES (4, 'Jarosław', 'Adamczuk', 'j.adamski@gmail.com', 55, 'IT');

INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 3);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 3);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 4);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (3, 4);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (4, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (4, 3);