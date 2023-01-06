INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (1, 'Jan', 'Wawrzyniak', 'j.wawrzyniak@gmail.com', 22, 'ROBOTICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (2, 'Maciej', 'Komaranczuk', 'm.komaranczuk@gmail.com', 25, 'INFORMATICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (3, 'Weronika', 'Romanski', 'w.romanski@gmail.com', 20, 'ELECTRICAL');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (4, 'Robert', 'Kaczuk', 'r.kaczuk@gmail.com', 21, 'ELECTRONICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (5, 'Alojzy', 'Adach', 'a.adach@gmail.com', 22, 'ROBOTICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (6, 'Bogdan', 'Agaciński', 'b.agaciński@gmail.com', 25, 'INFORMATICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (7, 'Cyryl', 'Badach', 'c.badach@gmail.com', 20, 'ELECTRICAL');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (8, 'Emanuel', 'Żyra', 'e.żyra@gmail.com', 21, 'ELECTRONICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (9, 'Gniewomir', 'Żygłowicz', 'g.żygłowicz@gmail.com', 22, 'ROBOTICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (10, 'Jacenty', 'Zwierzyński', 'j.zwierzyński@gmail.com', 25, 'INFORMATICS');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (11, 'Karol', 'Zimerman', 'k.zimerman@gmail.com', 20, 'ELECTRICAL');
INSERT INTO student (id, first_name, last_name, email, age, `field_of_study`) VALUES (12, 'Kosma', 'Wyrwicz', 'k.wyrwicz@gmail.com', 21, 'ELECTRONICS');

INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (1, 'Grzegorz', 'Wrzyszcz', 'g.wrzyszcz@gmail.com', 48);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (2, 'Jagoda', 'Wosz', 'j.wosz@gmail.com', 33);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (3, 'Adam', 'Wojtyna', 'a.wojtyna@gmail.com', 40);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (4, 'Jarosław', 'Widera', 'j.widera@gmail.com', 55);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (5, 'Ludwik', 'Kostro', 'l.kostro@gmail.com', 48);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (6, 'Marian', 'Kowalska', 'm.kowalska@gmail.com', 33);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (7, 'Maximilian', 'Batista', 'm.batista@gmail.com', 40);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (8, 'Nikolas', 'Adamczuk', 'n.adamczuk@gmail.com', 55);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (9, 'Orest', 'Szafarczyk', 'o.szafarczyk@gmail.com', 48);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (10, 'Oskar', 'Trzaskoma', 'o.trzaskoma@gmail.com', 33);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (11, 'Petroniusz', 'Tarczewski', 'p.tarczewski@gmail.com', 40);
INSERT INTO teacher (id, first_name, last_name, email, age) VALUES (12, 'Remigiusz', 'Śliwa', 'r.śliwa@gmail.com', 55);

INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(1, 'COMPUTER SCIENCE', 'Learning Java and Spring', 45);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(2, 'SYSTEMY INŻYNIERII WIEDZY', 'Learning Java and Spring', 30);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(3, 'SYSTEMY STEROWANIA I WSPOMAGANIA DECYZJI', 'Learning Java and Spring', 45);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(4, 'Technika wysokich napięć', 'Learning Java and Spring', 60);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(5, 'Mechanika', 'Learning Java and Spring', 30);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(6, 'Inżynieria materiałowa', 'Learning Java and Spring', 15);
INSERT INTO subjects (id, name, description, hours_in_semester) VALUES(6, 'Podstawy programowania', 'Learning Java and Spring', 15);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 3);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (1, 9);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 3);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (2, 4);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (3, 4);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (4, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (4, 3);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (5, 5);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (5, 6);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (5, 7);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (5, 9);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (5, 12);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (6, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (6, 5);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (7, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (7, 11);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (8, 5);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (8, 10);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (8, 11);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (8, 12);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (9, 5);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (9, 6);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (9, 7);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (9, 9);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (10, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (10, 9);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (10, 11);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (10, 12);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (10, 6);

INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 1);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 2);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 3);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 4);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 5);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 6);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 7);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 8);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 9);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 10);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 11);
INSERT INTO teacher_students (teacher_id, student_id) VALUES (11, 12);
