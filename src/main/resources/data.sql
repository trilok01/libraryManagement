INSERT INTO Book (Title, ISBN, Published_Year, Availability_Status) VALUES
('Head First Java', '978-0596009205', 2024, 'AVAILABLE'),
('Clean Code: A Handbook of Agile Software Craftsmanship', '9780132350884', 2008, 'AVAILABLE'),
('The Pragmatic Programmer: Your Journey to Mastery', '9780135957059', 2019, 'AVAILABLE'),
('Introduction to Algorithms', '9780262046305', 2022, 'AVAILABLE'),
('Design Patterns: Elements of Reusable Object-Oriented Software', '9780201633610', 1994, 'AVAILABLE');

INSERT INTO Author (Name) VALUES
('Sierra'),
('Robert C. Martin'),
('Andrew Hunt'),
('David Thomas'),
('Thomas H. Cormen');

INSERT INTO Book_Author (Book_Id, Author_Id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);