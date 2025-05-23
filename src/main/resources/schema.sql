DROP TABLE IF EXISTS Book_Author;
DROP TABLE IF EXISTS Author;
DROP TABLE IF EXISTS Book;

CREATE TABLE Book (
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Title VARCHAR(255) NOT NULL,
	ISBN VARCHAR(255) NOT NULL UNIQUE,
	Published_Year YEAR,
	Availability_Status ENUM('AVAILABLE', 'BORROWED') DEFAULT 'AVAILABLE'
);

CREATE TABLE Author (
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE Book_Author (
	Id INT AUTO_INCREMENT PRIMARY KEY,
	Book_Id INT NOT NULL,
	Author_Id INT NOT NULL,
	FOREIGN KEY (Book_Id) REFERENCES Book(Id),
	FOREIGN KEY (Author_Id) REFERENCES Author(Id)
);