/*
 *  $Id
 *
 *  Creates and populates database objects with sample data.
 *  This file was created by grabbing the commands made by creating
 *  sample data with the DatabaseManager utility.
 */


DROP TABLE Item IF EXISTS;;
DROP TABLE Invoice IF EXISTS;;
DROP TABLE Product IF EXISTS;;
DROP TABLE Customer IF EXISTS;;
CREATE TABLE Customer(ID INTEGER PRIMARY KEY,FirstName VARCHAR,LastName VARCHAR,Street VARCHAR,City VARCHAR);;
CREATE TABLE Product(ID INTEGER PRIMARY KEY,Name VARCHAR,Price DECIMAL);;
CREATE TABLE Invoice(ID INTEGER PRIMARY KEY,CustomerID INTEGER,Total DECIMAL, FOREIGN KEY (CustomerId) REFERENCES Customer(ID) ON DELETE CASCADE);;
CREATE TABLE Item(InvoiceID INTEGER,Item INTEGER,ProductID INTEGER,Quantity INTEGER,Cost DECIMAL,PRIMARY KEY(InvoiceID,Item), FOREIGN KEY (InvoiceId) REFERENCES Invoice (ID) ON DELETE CASCADE, FOREIGN KEY (ProductId) REFERENCES Product(ID) ON DELETE CASCADE);;
SET REFERENTIAL_INTEGRITY FALSE;
INSERT INTO Customer VALUES(0,'Laura','Steel','429 Seventh Av.','Dallas');
INSERT INTO Product VALUES(0,'Iron Iron',54);
INSERT INTO Invoice VALUES(0,0,0.0);
INSERT INTO Item VALUES(0,12,1,11,1.5);
INSERT INTO Item VALUES(0,11,12,4,1.5);
INSERT INTO Item VALUES(0,10,4,8,1.5);
INSERT INTO Item VALUES(0,9,35,4,1.5);
INSERT INTO Item VALUES(0,8,0,23,1.5);
INSERT INTO Item VALUES(0,7,7,10,1.5);
INSERT INTO Item VALUES(0,6,16,9,1.5);
INSERT INTO Item VALUES(0,5,12,15,1.5);
INSERT INTO Item VALUES(0,4,47,1,1.5);
INSERT INTO Item VALUES(0,3,1,9,1.5);
INSERT INTO Item VALUES(0,2,47,3,1.5);
INSERT INTO Item VALUES(0,1,14,19,1.5);
INSERT INTO Item VALUES(0,0,7,12,1.5);
INSERT INTO Customer VALUES(1,'Susanne','King','366 - 20th Ave.','Olten');
INSERT INTO Product VALUES(1,'Chair Shoe',248);
INSERT INTO Invoice VALUES(1,33,0.0);
INSERT INTO Item VALUES(1,6,25,19,1.5);
INSERT INTO Item VALUES(1,5,25,9,1.5);
INSERT INTO Item VALUES(1,4,16,16,1.5);
INSERT INTO Item VALUES(1,3,38,8,1.5);
INSERT INTO Item VALUES(1,2,19,6,1.5);
INSERT INTO Item VALUES(1,1,0,9,1.5);
INSERT INTO Item VALUES(1,0,40,8,1.5);
INSERT INTO Customer VALUES(2,'Anne','Miller','20 Upland Pl.','Lyon');
INSERT INTO Product VALUES(2,'Telephone Clock',248);
INSERT INTO Invoice VALUES(2,23,0.0);
INSERT INTO Item VALUES(2,16,36,24,1.5);
INSERT INTO Item VALUES(2,15,0,18,1.5);
INSERT INTO Item VALUES(2,14,48,19,1.5);
INSERT INTO Item VALUES(2,13,12,1,1.5);
INSERT INTO Item VALUES(2,12,21,15,1.5);
INSERT INTO Item VALUES(2,11,42,21,1.5);
INSERT INTO Item VALUES(2,10,49,11,1.5);
INSERT INTO Item VALUES(2,9,18,7,1.5);
INSERT INTO Item VALUES(2,8,39,2,1.5);
INSERT INTO Item VALUES(2,7,30,5,1.5);
INSERT INTO Item VALUES(2,6,43,8,1.5);
INSERT INTO Item VALUES(2,5,30,4,1.5);
INSERT INTO Item VALUES(2,4,38,18,1.5);
INSERT INTO Item VALUES(2,3,19,13,1.5);
INSERT INTO Item VALUES(2,2,11,9,1.5);
INSERT INTO Item VALUES(2,1,25,3,1.5);
INSERT INTO Item VALUES(2,0,4,18,1.5);
INSERT INTO Customer VALUES(3,'Michael','Clancy','542 Upland Pl.','San Francisco');
INSERT INTO Product VALUES(3,'Chair Chair',254);
INSERT INTO Invoice VALUES(3,21,0.0);
INSERT INTO Item VALUES(3,17,30,17,1.5);
INSERT INTO Item VALUES(3,16,19,11,1.5);
INSERT INTO Item VALUES(3,15,23,18,1.5);
INSERT INTO Item VALUES(3,14,17,22,1.5);
INSERT INTO Item VALUES(3,13,41,2,1.5);
INSERT INTO Item VALUES(3,12,41,22,1.5);
INSERT INTO Item VALUES(3,11,7,11,1.5);
INSERT INTO Item VALUES(3,10,10,17,1.5);
INSERT INTO Item VALUES(3,9,29,17,1.5);
INSERT INTO Item VALUES(3,8,49,9,1.5);
INSERT INTO Item VALUES(3,7,26,4,1.5);
INSERT INTO Item VALUES(3,6,13,18,1.5);
INSERT INTO Item VALUES(3,5,30,10,1.5);
INSERT INTO Item VALUES(3,4,20,12,1.5);
INSERT INTO Item VALUES(3,3,0,22,1.5);
INSERT INTO Item VALUES(3,2,49,3,1.5);
INSERT INTO Item VALUES(3,1,1,20,1.5);
INSERT INTO Item VALUES(3,0,11,21,1.5);
INSERT INTO Customer VALUES(4,'Sylvia','Ringer','365 College Av.','Dallas');
INSERT INTO Product VALUES(4,'Ice Tea Shoe',128);
INSERT INTO Invoice VALUES(4,30,0.0);
INSERT INTO Item VALUES(4,5,37,24,1.5);
INSERT INTO Item VALUES(4,4,9,18,1.5);
INSERT INTO Item VALUES(4,3,23,20,1.5);
INSERT INTO Item VALUES(4,2,41,23,1.5);
INSERT INTO Item VALUES(4,1,35,15,1.5);
INSERT INTO Item VALUES(4,0,28,9,1.5);
INSERT INTO Customer VALUES(5,'Laura','Miller','294 Seventh Av.','Paris');
INSERT INTO Product VALUES(5,'Clock Clock',236);
INSERT INTO Invoice VALUES(5,34,0.0);
INSERT INTO Item VALUES(5,13,18,17,1.5);
INSERT INTO Item VALUES(5,12,8,15,1.5);
INSERT INTO Item VALUES(5,11,38,23,1.5);
INSERT INTO Item VALUES(5,10,28,18,1.5);
INSERT INTO Item VALUES(5,9,37,9,1.5);
INSERT INTO Item VALUES(5,8,20,3,1.5);
INSERT INTO Item VALUES(5,7,2,4,1.5);
INSERT INTO Item VALUES(5,6,7,9,1.5);
INSERT INTO Item VALUES(5,5,46,15,1.5);
INSERT INTO Item VALUES(5,4,32,14,1.5);
INSERT INTO Item VALUES(5,3,24,12,1.5);
INSERT INTO Item VALUES(5,2,20,18,1.5);
INSERT INTO Item VALUES(5,1,9,23,1.5);
INSERT INTO Item VALUES(5,0,9,5,1.5);
INSERT INTO Customer VALUES(6,'Laura','White','506 Upland Pl.','Palo Alto');
INSERT INTO Product VALUES(6,'Ice Tea Chair',98);
INSERT INTO Invoice VALUES(6,19,0.0);
INSERT INTO Item VALUES(6,11,44,1,1.5);
INSERT INTO Item VALUES(6,10,16,13,1.5);
INSERT INTO Item VALUES(6,9,19,2,1.5);
INSERT INTO Item VALUES(6,8,41,19,1.5);
INSERT INTO Item VALUES(6,7,26,10,1.5);
INSERT INTO Item VALUES(6,6,37,22,1.5);
INSERT INTO Item VALUES(6,5,14,20,1.5);
INSERT INTO Item VALUES(6,4,31,20,1.5);
INSERT INTO Item VALUES(6,3,30,2,1.5);
INSERT INTO Item VALUES(6,2,23,8,1.5);
INSERT INTO Item VALUES(6,1,38,21,1.5);
INSERT INTO Item VALUES(6,0,15,20,1.5);
INSERT INTO Customer VALUES(7,'James','Peterson','231 Upland Pl.','San Francisco');
INSERT INTO Product VALUES(7,'Telephone Shoe',84);
INSERT INTO Invoice VALUES(7,26,0.0);
INSERT INTO Item VALUES(7,18,23,5,1.5);
INSERT INTO Item VALUES(7,17,40,1,1.5);
INSERT INTO Item VALUES(7,16,7,12,1.5);
INSERT INTO Item VALUES(7,15,24,17,1.5);
INSERT INTO Item VALUES(7,14,47,14,1.5);
INSERT INTO Item VALUES(7,13,32,23,1.5);
INSERT INTO Item VALUES(7,12,40,16,1.5);
INSERT INTO Item VALUES(7,11,19,13,1.5);
INSERT INTO Item VALUES(7,10,7,1,1.5);
INSERT INTO Item VALUES(7,9,9,21,1.5);
INSERT INTO Item VALUES(7,8,42,19,1.5);
INSERT INTO Item VALUES(7,7,2,22,1.5);
INSERT INTO Item VALUES(7,6,14,4,1.5);
INSERT INTO Item VALUES(7,5,24,10,1.5);
INSERT INTO Item VALUES(7,4,2,13,1.5);
INSERT INTO Item VALUES(7,3,30,2,1.5);
INSERT INTO Item VALUES(7,2,27,17,1.5);
INSERT INTO Item VALUES(7,1,23,12,1.5);
INSERT INTO Item VALUES(7,0,43,16,1.5);
INSERT INTO Customer VALUES(8,'Andrew','Miller','288 - 20th Ave.','Seattle');
INSERT INTO Product VALUES(8,'Ice Tea Clock',226);
INSERT INTO Invoice VALUES(8,29,0.0);
INSERT INTO Item VALUES(8,9,21,23,1.5);
INSERT INTO Item VALUES(8,8,38,7,1.5);
INSERT INTO Item VALUES(8,7,6,5,1.5);
INSERT INTO Item VALUES(8,6,15,19,1.5);
INSERT INTO Item VALUES(8,5,24,18,1.5);
INSERT INTO Item VALUES(8,4,15,8,1.5);
INSERT INTO Item VALUES(8,3,41,16,1.5);
INSERT INTO Item VALUES(8,2,11,8,1.5);
INSERT INTO Item VALUES(8,1,44,16,1.5);
INSERT INTO Item VALUES(8,0,34,15,1.5);
INSERT INTO Customer VALUES(9,'James','Schneider','277 Seventh Av.','Berne');
INSERT INTO Product VALUES(9,'Clock Telephone',172);
INSERT INTO Invoice VALUES(9,38,0.0);
INSERT INTO Item VALUES(9,19,48,23,1.5);
INSERT INTO Item VALUES(9,18,3,12,1.5);
INSERT INTO Item VALUES(9,17,13,17,1.5);
INSERT INTO Item VALUES(9,16,24,10,1.5);
INSERT INTO Item VALUES(9,15,48,23,1.5);
INSERT INTO Item VALUES(9,14,15,8,1.5);
INSERT INTO Item VALUES(9,13,42,13,1.5);
INSERT INTO Item VALUES(9,12,25,23,1.5);
INSERT INTO Item VALUES(9,11,12,16,1.5);
INSERT INTO Item VALUES(9,10,38,11,1.5);
INSERT INTO Item VALUES(9,9,13,6,1.5);
INSERT INTO Item VALUES(9,8,24,11,1.5);
INSERT INTO Item VALUES(9,7,2,22,1.5);
INSERT INTO Item VALUES(9,6,18,10,1.5);
INSERT INTO Item VALUES(9,5,6,2,1.5);
INSERT INTO Item VALUES(9,4,36,16,1.5);
INSERT INTO Item VALUES(9,3,4,14,1.5);
INSERT INTO Item VALUES(9,2,29,12,1.5);
INSERT INTO Item VALUES(9,1,18,21,1.5);
INSERT INTO Item VALUES(9,0,45,8,1.5);
INSERT INTO Customer VALUES(10,'Anne','Fuller','135 Upland Pl.','Dallas');
INSERT INTO Product VALUES(10,'Telephone Ice Tea',204);
INSERT INTO Invoice VALUES(10,24,0.0);
INSERT INTO Item VALUES(10,9,5,10,1.5);
INSERT INTO Item VALUES(10,8,22,11,1.5);
INSERT INTO Item VALUES(10,7,4,13,1.5);
INSERT INTO Item VALUES(10,6,18,14,1.5);
INSERT INTO Item VALUES(10,5,5,24,1.5);
INSERT INTO Item VALUES(10,4,10,24,1.5);
INSERT INTO Item VALUES(10,3,46,1,1.5);
INSERT INTO Item VALUES(10,2,7,9,1.5);
INSERT INTO Item VALUES(10,1,33,17,1.5);
INSERT INTO Item VALUES(10,0,20,1,1.5);
INSERT INTO Customer VALUES(11,'Julia','White','412 Upland Pl.','Chicago');
INSERT INTO Product VALUES(11,'Telephone Iron',88);
INSERT INTO Invoice VALUES(11,24,0.0);
INSERT INTO Item VALUES(11,8,20,1,1.5);
INSERT INTO Item VALUES(11,7,48,22,1.5);
INSERT INTO Item VALUES(11,6,0,12,1.5);
INSERT INTO Item VALUES(11,5,19,2,1.5);
INSERT INTO Item VALUES(11,4,47,16,1.5);
INSERT INTO Item VALUES(11,3,32,21,1.5);
INSERT INTO Item VALUES(11,2,0,3,1.5);
INSERT INTO Item VALUES(11,1,21,21,1.5);
INSERT INTO Item VALUES(11,0,45,10,1.5);
INSERT INTO Customer VALUES(12,'George','Ott','381 Upland Pl.','Palo Alto');
INSERT INTO Product VALUES(12,'Clock Ice Tea',168);
INSERT INTO Invoice VALUES(12,23,0.0);
INSERT INTO Item VALUES(12,18,9,4,1.5);
INSERT INTO Item VALUES(12,17,31,15,1.5);
INSERT INTO Item VALUES(12,16,0,9,1.5);
INSERT INTO Item VALUES(12,15,22,16,1.5);
INSERT INTO Item VALUES(12,14,25,11,1.5);
INSERT INTO Item VALUES(12,13,36,21,1.5);
INSERT INTO Item VALUES(12,12,13,12,1.5);
INSERT INTO Item VALUES(12,11,28,16,1.5);
INSERT INTO Item VALUES(12,10,46,19,1.5);
INSERT INTO Item VALUES(12,9,25,22,1.5);
INSERT INTO Item VALUES(12,8,48,2,1.5);
INSERT INTO Item VALUES(12,7,48,7,1.5);
INSERT INTO Item VALUES(12,6,31,15,1.5);
INSERT INTO Item VALUES(12,5,37,17,1.5);
INSERT INTO Item VALUES(12,4,20,11,1.5);
INSERT INTO Item VALUES(12,3,0,18,1.5);
INSERT INTO Item VALUES(12,2,6,5,1.5);
INSERT INTO Item VALUES(12,1,41,19,1.5);
INSERT INTO Item VALUES(12,0,1,24,1.5);
INSERT INTO Customer VALUES(13,'Laura','Ringer','38 College Av.','New York');
INSERT INTO Product VALUES(13,'Telephone Clock',180);
INSERT INTO Invoice VALUES(13,39,0.0);
INSERT INTO Item VALUES(13,21,40,1,1.5);
INSERT INTO Item VALUES(13,20,5,19,1.5);
INSERT INTO Item VALUES(13,19,42,18,1.5);
INSERT INTO Item VALUES(13,18,0,16,1.5);
INSERT INTO Item VALUES(13,17,32,18,1.5);
INSERT INTO Item VALUES(13,16,22,23,1.5);
INSERT INTO Item VALUES(13,15,0,20,1.5);
INSERT INTO Item VALUES(13,14,1,12,1.5);
INSERT INTO Item VALUES(13,13,10,20,1.5);
INSERT INTO Item VALUES(13,12,17,3,1.5);
INSERT INTO Item VALUES(13,11,14,3,1.5);
INSERT INTO Item VALUES(13,10,45,24,1.5);
INSERT INTO Item VALUES(13,9,24,10,1.5);
INSERT INTO Item VALUES(13,8,48,11,1.5);
INSERT INTO Item VALUES(13,7,29,24,1.5);
INSERT INTO Item VALUES(13,6,19,8,1.5);
INSERT INTO Item VALUES(13,5,22,19,1.5);
INSERT INTO Item VALUES(13,4,26,21,1.5);
INSERT INTO Item VALUES(13,3,32,2,1.5);
INSERT INTO Item VALUES(13,2,13,20,1.5);
INSERT INTO Item VALUES(13,1,1,1,1.5);
INSERT INTO Item VALUES(13,0,16,10,1.5);
INSERT INTO Customer VALUES(14,'Bill','Karsen','53 College Av.','Oslo');
INSERT INTO Product VALUES(14,'Telephone Iron',124);
INSERT INTO Invoice VALUES(14,35,0.0);
INSERT INTO Item VALUES(14,13,11,23,1.5);
INSERT INTO Item VALUES(14,12,4,20,1.5);
INSERT INTO Item VALUES(14,11,25,15,1.5);
INSERT INTO Item VALUES(14,10,44,16,1.5);
INSERT INTO Item VALUES(14,9,13,16,1.5);
INSERT INTO Item VALUES(14,8,23,7,1.5);
INSERT INTO Item VALUES(14,7,43,4,1.5);
INSERT INTO Item VALUES(14,6,26,18,1.5);
INSERT INTO Item VALUES(14,5,11,8,1.5);
INSERT INTO Item VALUES(14,4,41,17,1.5);
INSERT INTO Item VALUES(14,3,34,11,1.5);
INSERT INTO Item VALUES(14,2,15,18,1.5);
INSERT INTO Item VALUES(14,1,9,22,1.5);
INSERT INTO Item VALUES(14,0,42,18,1.5);
INSERT INTO Customer VALUES(15,'Bill','Clancy','319 Upland Pl.','Seattle');
INSERT INTO Product VALUES(15,'Ice Tea Chair',94);
INSERT INTO Invoice VALUES(15,39,0.0);
INSERT INTO Item VALUES(15,2,24,6,1.5);
INSERT INTO Item VALUES(15,1,13,21,1.5);
INSERT INTO Item VALUES(15,0,17,12,1.5);
INSERT INTO Customer VALUES(16,'John','Fuller','195 Seventh Av.','New York');
INSERT INTO Product VALUES(16,'Ice Tea Shoe',194);
INSERT INTO Invoice VALUES(16,45,0.0);
INSERT INTO Item VALUES(16,15,12,3,1.5);
INSERT INTO Item VALUES(16,14,0,19,1.5);
INSERT INTO Item VALUES(16,13,20,1,1.5);
INSERT INTO Item VALUES(16,12,18,2,1.5);
INSERT INTO Item VALUES(16,11,24,7,1.5);
INSERT INTO Item VALUES(16,10,43,8,1.5);
INSERT INTO Item VALUES(16,9,11,10,1.5);
INSERT INTO Item VALUES(16,8,13,17,1.5);
INSERT INTO Item VALUES(16,7,8,17,1.5);
INSERT INTO Item VALUES(16,6,44,7,1.5);
INSERT INTO Item VALUES(16,5,11,15,1.5);
INSERT INTO Item VALUES(16,4,10,24,1.5);
INSERT INTO Item VALUES(16,3,0,3,1.5);
INSERT INTO Item VALUES(16,2,20,15,1.5);
INSERT INTO Item VALUES(16,1,36,20,1.5);
INSERT INTO Item VALUES(16,0,18,15,1.5);
INSERT INTO Customer VALUES(17,'Laura','Ott','443 Seventh Av.','Lyon');
INSERT INTO Product VALUES(17,'Clock Ice Tea',220);
INSERT INTO Invoice VALUES(17,46,0.0);
INSERT INTO Item VALUES(17,19,46,12,1.5);
INSERT INTO Item VALUES(17,18,5,9,1.5);
INSERT INTO Item VALUES(17,17,7,5,1.5);
INSERT INTO Item VALUES(17,16,8,16,1.5);
INSERT INTO Item VALUES(17,15,35,10,1.5);
INSERT INTO Item VALUES(17,14,18,2,1.5);
INSERT INTO Item VALUES(17,13,41,5,1.5);
INSERT INTO Item VALUES(17,12,22,16,1.5);
INSERT INTO Item VALUES(17,11,45,10,1.5);
INSERT INTO Item VALUES(17,10,10,12,1.5);
INSERT INTO Item VALUES(17,9,8,15,1.5);
INSERT INTO Item VALUES(17,8,49,8,1.5);
INSERT INTO Item VALUES(17,7,6,15,1.5);
INSERT INTO Item VALUES(17,6,43,6,1.5);
INSERT INTO Item VALUES(17,5,44,1,1.5);
INSERT INTO Item VALUES(17,4,23,2,1.5);
INSERT INTO Item VALUES(17,3,24,4,1.5);
INSERT INTO Item VALUES(17,2,44,11,1.5);
INSERT INTO Item VALUES(17,1,19,19,1.5);
INSERT INTO Item VALUES(17,0,16,8,1.5);
INSERT INTO Customer VALUES(18,'Sylvia','Fuller','158 - 20th Ave.','Paris');
INSERT INTO Product VALUES(18,'Chair Clock',172);
INSERT INTO Invoice VALUES(18,4,0.0);
INSERT INTO Item VALUES(18,18,10,1,1.5);
INSERT INTO Item VALUES(18,17,8,1,1.5);
INSERT INTO Item VALUES(18,16,31,12,1.5);
INSERT INTO Item VALUES(18,15,44,20,1.5);
INSERT INTO Item VALUES(18,14,28,20,1.5);
INSERT INTO Item VALUES(18,13,14,12,1.5);
INSERT INTO Item VALUES(18,12,37,12,1.5);
INSERT INTO Item VALUES(18,11,30,8,1.5);
INSERT INTO Item VALUES(18,10,34,18,1.5);
INSERT INTO Item VALUES(18,9,2,2,1.5);
INSERT INTO Item VALUES(18,8,1,24,1.5);
INSERT INTO Item VALUES(18,7,15,14,1.5);
INSERT INTO Item VALUES(18,6,29,4,1.5);
INSERT INTO Item VALUES(18,5,15,6,1.5);
INSERT INTO Item VALUES(18,4,28,6,1.5);
INSERT INTO Item VALUES(18,3,19,8,1.5);
INSERT INTO Item VALUES(18,2,40,12,1.5);
INSERT INTO Item VALUES(18,1,33,12,1.5);
INSERT INTO Item VALUES(18,0,32,1,1.5);
INSERT INTO Customer VALUES(19,'Susanne','Heiniger','86 - 20th Ave.','Dallas');
INSERT INTO Product VALUES(19,'Ice Tea Ice Tea',110);
INSERT INTO Invoice VALUES(19,9,0.0);
INSERT INTO Item VALUES(19,4,36,24,1.5);
INSERT INTO Item VALUES(19,3,49,23,1.5);
INSERT INTO Item VALUES(19,2,4,22,1.5);
INSERT INTO Item VALUES(19,1,31,2,1.5);
INSERT INTO Item VALUES(19,0,12,7,1.5);
INSERT INTO Customer VALUES(20,'Janet','Schneider','309 - 20th Ave.','Oslo');
INSERT INTO Product VALUES(20,'Ice Tea Telephone',200);
INSERT INTO Invoice VALUES(20,19,0.0);
INSERT INTO Item VALUES(20,11,15,8,1.5);
INSERT INTO Item VALUES(20,10,25,11,1.5);
INSERT INTO Item VALUES(20,9,12,8,1.5);
INSERT INTO Item VALUES(20,8,44,18,1.5);
INSERT INTO Item VALUES(20,7,9,9,1.5);
INSERT INTO Item VALUES(20,6,20,2,1.5);
INSERT INTO Item VALUES(20,5,8,14,1.5);
INSERT INTO Item VALUES(20,4,30,13,1.5);
INSERT INTO Item VALUES(20,3,25,14,1.5);
INSERT INTO Item VALUES(20,2,24,22,1.5);
INSERT INTO Item VALUES(20,1,29,6,1.5);
INSERT INTO Item VALUES(20,0,47,15,1.5);
INSERT INTO Customer VALUES(21,'Julia','Clancy','18 Seventh Av.','Seattle');
INSERT INTO Product VALUES(21,'Chair Chair',114);
INSERT INTO Invoice VALUES(21,8,0.0);
INSERT INTO Item VALUES(21,11,20,11,1.5);
INSERT INTO Item VALUES(21,10,19,14,1.5);
INSERT INTO Item VALUES(21,9,35,17,1.5);
INSERT INTO Item VALUES(21,8,44,19,1.5);
INSERT INTO Item VALUES(21,7,8,9,1.5);
INSERT INTO Item VALUES(21,6,26,7,1.5);
INSERT INTO Item VALUES(21,5,27,18,1.5);
INSERT INTO Item VALUES(21,4,49,22,1.5);
INSERT INTO Item VALUES(21,3,30,13,1.5);
INSERT INTO Item VALUES(21,2,31,17,1.5);
INSERT INTO Item VALUES(21,1,38,19,1.5);
INSERT INTO Item VALUES(21,0,9,10,1.5);
INSERT INTO Customer VALUES(22,'Bill','Ott','250 - 20th Ave.','Berne');
INSERT INTO Product VALUES(22,'Iron Iron',66);
INSERT INTO Invoice VALUES(22,40,0.0);
INSERT INTO Item VALUES(22,9,23,1,1.5);
INSERT INTO Item VALUES(22,8,3,2,1.5);
INSERT INTO Item VALUES(22,7,21,6,1.5);
INSERT INTO Item VALUES(22,6,4,11,1.5);
INSERT INTO Item VALUES(22,5,24,5,1.5);
INSERT INTO Item VALUES(22,4,5,21,1.5);
INSERT INTO Item VALUES(22,3,22,5,1.5);
INSERT INTO Item VALUES(22,2,12,20,1.5);
INSERT INTO Item VALUES(22,1,30,11,1.5);
INSERT INTO Item VALUES(22,0,9,6,1.5);
INSERT INTO Customer VALUES(23,'Julia','Heiniger','358 College Av.','Boston');
INSERT INTO Product VALUES(23,'Shoe Chair',76);
INSERT INTO Invoice VALUES(23,36,0.0);
INSERT INTO Item VALUES(23,16,8,11,1.5);
INSERT INTO Item VALUES(23,15,13,17,1.5);
INSERT INTO Item VALUES(23,14,44,2,1.5);
INSERT INTO Item VALUES(23,13,14,17,1.5);
INSERT INTO Item VALUES(23,12,4,17,1.5);
INSERT INTO Item VALUES(23,11,41,8,1.5);
INSERT INTO Item VALUES(23,10,4,18,1.5);
INSERT INTO Item VALUES(23,9,20,18,1.5);
INSERT INTO Item VALUES(23,8,6,17,1.5);
INSERT INTO Item VALUES(23,7,39,3,1.5);
INSERT INTO Item VALUES(23,6,16,1,1.5);
INSERT INTO Item VALUES(23,5,32,14,1.5);
INSERT INTO Item VALUES(23,4,23,19,1.5);
INSERT INTO Item VALUES(23,3,40,19,1.5);
INSERT INTO Item VALUES(23,2,33,18,1.5);
INSERT INTO Item VALUES(23,1,26,8,1.5);
INSERT INTO Item VALUES(23,0,48,22,1.5);
INSERT INTO Customer VALUES(24,'James','Sommer','333 Upland Pl.','Olten');
INSERT INTO Product VALUES(24,'Chair Shoe',72);
INSERT INTO Invoice VALUES(24,15,0.0);
INSERT INTO Item VALUES(24,15,39,17,1.5);
INSERT INTO Item VALUES(24,14,1,13,1.5);
INSERT INTO Item VALUES(24,13,15,21,1.5);
INSERT INTO Item VALUES(24,12,0,8,1.5);
INSERT INTO Item VALUES(24,11,1,4,1.5);
INSERT INTO Item VALUES(24,10,27,4,1.5);
INSERT INTO Item VALUES(24,9,21,8,1.5);
INSERT INTO Item VALUES(24,8,5,18,1.5);
INSERT INTO Item VALUES(24,7,7,13,1.5);
INSERT INTO Item VALUES(24,6,40,3,1.5);
INSERT INTO Item VALUES(24,5,35,16,1.5);
INSERT INTO Item VALUES(24,4,15,17,1.5);
INSERT INTO Item VALUES(24,3,17,23,1.5);
INSERT INTO Item VALUES(24,2,38,10,1.5);
INSERT INTO Item VALUES(24,1,46,18,1.5);
INSERT INTO Item VALUES(24,0,43,14,1.5);
INSERT INTO Customer VALUES(25,'Sylvia','Steel','269 College Av.','Paris');
INSERT INTO Product VALUES(25,'Shoe Shoe',162);
INSERT INTO Invoice VALUES(25,31,0.0);
INSERT INTO Item VALUES(25,8,38,3,1.5);
INSERT INTO Item VALUES(25,7,16,8,1.5);
INSERT INTO Item VALUES(25,6,21,18,1.5);
INSERT INTO Item VALUES(25,5,10,5,1.5);
INSERT INTO Item VALUES(25,4,47,10,1.5);
INSERT INTO Item VALUES(25,3,19,4,1.5);
INSERT INTO Item VALUES(25,2,13,8,1.5);
INSERT INTO Item VALUES(25,1,43,13,1.5);
INSERT INTO Item VALUES(25,0,5,15,1.5);
INSERT INTO Customer VALUES(26,'James','Clancy','195 Upland Pl.','Oslo');
INSERT INTO Product VALUES(26,'Shoe Shoe',252);
INSERT INTO Invoice VALUES(26,27,0.0);
INSERT INTO Item VALUES(26,16,30,4,1.5);
INSERT INTO Item VALUES(26,15,8,6,1.5);
INSERT INTO Item VALUES(26,14,26,6,1.5);
INSERT INTO Item VALUES(26,13,13,10,1.5);
INSERT INTO Item VALUES(26,12,27,20,1.5);
INSERT INTO Item VALUES(26,11,18,3,1.5);
INSERT INTO Item VALUES(26,10,34,16,1.5);
INSERT INTO Item VALUES(26,9,1,23,1.5);
INSERT INTO Item VALUES(26,8,40,13,1.5);
INSERT INTO Item VALUES(26,7,4,16,1.5);
INSERT INTO Item VALUES(26,6,7,23,1.5);
INSERT INTO Item VALUES(26,5,38,4,1.5);
INSERT INTO Item VALUES(26,4,46,7,1.5);
INSERT INTO Item VALUES(26,3,16,3,1.5);
INSERT INTO Item VALUES(26,2,33,7,1.5);
INSERT INTO Item VALUES(26,1,43,21,1.5);
INSERT INTO Item VALUES(26,0,42,16,1.5);
INSERT INTO Customer VALUES(27,'Bob','Sommer','509 College Av.','Seattle');
INSERT INTO Product VALUES(27,'Telephone Iron',230);
INSERT INTO Invoice VALUES(27,24,0.0);
INSERT INTO Item VALUES(27,2,19,1,1.5);
INSERT INTO Item VALUES(27,1,45,15,1.5);
INSERT INTO Item VALUES(27,0,24,15,1.5);
INSERT INTO Customer VALUES(28,'Susanne','White','74 - 20th Ave.','Lyon');
INSERT INTO Product VALUES(28,'Clock Iron',30);
INSERT INTO Invoice VALUES(28,35,0.0);
INSERT INTO Item VALUES(28,8,28,6,1.5);
INSERT INTO Item VALUES(28,7,28,8,1.5);
INSERT INTO Item VALUES(28,6,33,16,1.5);
INSERT INTO Item VALUES(28,5,49,4,1.5);
INSERT INTO Item VALUES(28,4,45,17,1.5);
INSERT INTO Item VALUES(28,3,6,3,1.5);
INSERT INTO Item VALUES(28,2,44,22,1.5);
INSERT INTO Item VALUES(28,1,15,13,1.5);
INSERT INTO Item VALUES(28,0,35,13,1.5);
INSERT INTO Customer VALUES(29,'Andrew','Smith','254 College Av.','New York');
INSERT INTO Product VALUES(29,'Chair Telephone',112);
INSERT INTO Invoice VALUES(29,46,0.0);
INSERT INTO Item VALUES(29,8,35,6,1.5);
INSERT INTO Item VALUES(29,7,5,1,1.5);
INSERT INTO Item VALUES(29,6,4,16,1.5);
INSERT INTO Item VALUES(29,5,31,13,1.5);
INSERT INTO Item VALUES(29,4,4,7,1.5);
INSERT INTO Item VALUES(29,3,7,21,1.5);
INSERT INTO Item VALUES(29,2,17,23,1.5);
INSERT INTO Item VALUES(29,1,38,12,1.5);
INSERT INTO Item VALUES(29,0,33,17,1.5);
INSERT INTO Customer VALUES(30,'Bill','Sommer','362 - 20th Ave.','Olten');
INSERT INTO Product VALUES(30,'Shoe Iron',232);
INSERT INTO Invoice VALUES(30,13,0.0);
INSERT INTO Item VALUES(30,6,14,23,1.5);
INSERT INTO Item VALUES(30,5,43,23,1.5);
INSERT INTO Item VALUES(30,4,34,2,1.5);
INSERT INTO Item VALUES(30,3,33,2,1.5);
INSERT INTO Item VALUES(30,2,10,18,1.5);
INSERT INTO Item VALUES(30,1,16,19,1.5);
INSERT INTO Item VALUES(30,0,14,7,1.5);
INSERT INTO Customer VALUES(31,'Bob','Ringer','371 College Av.','Olten');
INSERT INTO Product VALUES(31,'Ice Tea Telephone',48);
INSERT INTO Invoice VALUES(31,22,0.0);
INSERT INTO Item VALUES(31,10,0,3,1.5);
INSERT INTO Item VALUES(31,9,14,15,1.5);
INSERT INTO Item VALUES(31,8,7,5,1.5);
INSERT INTO Item VALUES(31,7,38,3,1.5);
INSERT INTO Item VALUES(31,6,26,16,1.5);
INSERT INTO Item VALUES(31,5,1,4,1.5);
INSERT INTO Item VALUES(31,4,8,14,1.5);
INSERT INTO Item VALUES(31,3,12,10,1.5);
INSERT INTO Item VALUES(31,2,4,3,1.5);
INSERT INTO Item VALUES(31,1,4,23,1.5);
INSERT INTO Item VALUES(31,0,33,10,1.5);
INSERT INTO Customer VALUES(32,'Michael','Ott','339 College Av.','Boston');
INSERT INTO Product VALUES(32,'Clock Iron',190);
INSERT INTO Invoice VALUES(32,20,0.0);
INSERT INTO Item VALUES(32,2,1,14,1.5);
INSERT INTO Item VALUES(32,1,30,13,1.5);
INSERT INTO Item VALUES(32,0,35,11,1.5);
INSERT INTO Customer VALUES(33,'Mary','King','491 College Av.','Oslo');
INSERT INTO Product VALUES(33,'Iron Chair',182);
INSERT INTO Invoice VALUES(33,40,0.0);
INSERT INTO Item VALUES(33,15,38,7,1.5);
INSERT INTO Item VALUES(33,14,44,13,1.5);
INSERT INTO Item VALUES(33,13,25,16,1.5);
INSERT INTO Item VALUES(33,12,16,23,1.5);
INSERT INTO Item VALUES(33,11,5,7,1.5);
INSERT INTO Item VALUES(33,10,24,9,1.5);
INSERT INTO Item VALUES(33,9,29,5,1.5);
INSERT INTO Item VALUES(33,8,3,15,1.5);
INSERT INTO Item VALUES(33,7,43,10,1.5);
INSERT INTO Item VALUES(33,6,17,16,1.5);
INSERT INTO Item VALUES(33,5,8,11,1.5);
INSERT INTO Item VALUES(33,4,24,1,1.5);
INSERT INTO Item VALUES(33,3,48,1,1.5);
INSERT INTO Item VALUES(33,2,36,16,1.5);
INSERT INTO Item VALUES(33,1,10,21,1.5);
INSERT INTO Item VALUES(33,0,36,5,1.5);
INSERT INTO Customer VALUES(34,'Julia','May','33 Upland Pl.','Seattle');
INSERT INTO Product VALUES(34,'Chair Iron',256);
INSERT INTO Invoice VALUES(34,33,0.0);
INSERT INTO Item VALUES(34,14,46,7,1.5);
INSERT INTO Item VALUES(34,13,30,14,1.5);
INSERT INTO Item VALUES(34,12,43,21,1.5);
INSERT INTO Item VALUES(34,11,4,17,1.5);
INSERT INTO Item VALUES(34,10,41,16,1.5);
INSERT INTO Item VALUES(34,9,8,17,1.5);
INSERT INTO Item VALUES(34,8,3,1,1.5);
INSERT INTO Item VALUES(34,7,21,22,1.5);
INSERT INTO Item VALUES(34,6,32,7,1.5);
INSERT INTO Item VALUES(34,5,45,13,1.5);
INSERT INTO Item VALUES(34,4,27,1,1.5);
INSERT INTO Item VALUES(34,3,44,15,1.5);
INSERT INTO Item VALUES(34,2,28,22,1.5);
INSERT INTO Item VALUES(34,1,4,3,1.5);
INSERT INTO Item VALUES(34,0,10,22,1.5);
INSERT INTO Customer VALUES(35,'George','Karsen','412 College Av.','Chicago');
INSERT INTO Product VALUES(35,'Telephone Shoe',76);
INSERT INTO Invoice VALUES(35,4,0.0);
INSERT INTO Item VALUES(35,13,19,17,1.5);
INSERT INTO Item VALUES(35,12,7,23,1.5);
INSERT INTO Item VALUES(35,11,44,9,1.5);
INSERT INTO Item VALUES(35,10,17,11,1.5);
INSERT INTO Item VALUES(35,9,19,1,1.5);
INSERT INTO Item VALUES(35,8,0,1,1.5);
INSERT INTO Item VALUES(35,7,22,15,1.5);
INSERT INTO Item VALUES(35,6,5,4,1.5);
INSERT INTO Item VALUES(35,5,33,5,1.5);
INSERT INTO Item VALUES(35,4,14,17,1.5);
INSERT INTO Item VALUES(35,3,27,10,1.5);
INSERT INTO Item VALUES(35,2,14,4,1.5);
INSERT INTO Item VALUES(35,1,3,9,1.5);
INSERT INTO Item VALUES(35,0,20,17,1.5);
INSERT INTO Customer VALUES(36,'John','Steel','276 Upland Pl.','Dallas');
INSERT INTO Product VALUES(36,'Ice Tea Iron',32);
INSERT INTO Invoice VALUES(36,42,0.0);
INSERT INTO Item VALUES(36,11,44,9,1.5);
INSERT INTO Item VALUES(36,10,47,11,1.5);
INSERT INTO Item VALUES(36,9,31,18,1.5);
INSERT INTO Item VALUES(36,8,4,21,1.5);
INSERT INTO Item VALUES(36,7,39,19,1.5);
INSERT INTO Item VALUES(36,6,39,20,1.5);
INSERT INTO Item VALUES(36,5,25,8,1.5);
INSERT INTO Item VALUES(36,4,40,5,1.5);
INSERT INTO Item VALUES(36,3,10,8,1.5);
INSERT INTO Item VALUES(36,2,1,6,1.5);
INSERT INTO Item VALUES(36,1,15,23,1.5);
INSERT INTO Item VALUES(36,0,18,13,1.5);
INSERT INTO Customer VALUES(37,'Michael','Clancy','19 Seventh Av.','Dallas');
INSERT INTO Product VALUES(37,'Clock Shoe',94);
INSERT INTO Invoice VALUES(37,39,0.0);
INSERT INTO Item VALUES(37,21,6,9,1.5);
INSERT INTO Item VALUES(37,20,14,1,1.5);
INSERT INTO Item VALUES(37,19,19,20,1.5);
INSERT INTO Item VALUES(37,18,26,22,1.5);
INSERT INTO Item VALUES(37,17,38,18,1.5);
INSERT INTO Item VALUES(37,16,27,8,1.5);
INSERT INTO Item VALUES(37,15,32,12,1.5);
INSERT INTO Item VALUES(37,14,12,3,1.5);
INSERT INTO Item VALUES(37,13,32,3,1.5);
INSERT INTO Item VALUES(37,12,24,23,1.5);
INSERT INTO Item VALUES(37,11,30,5,1.5);
INSERT INTO Item VALUES(37,10,1,18,1.5);
INSERT INTO Item VALUES(37,9,47,16,1.5);
INSERT INTO Item VALUES(37,8,46,9,1.5);
INSERT INTO Item VALUES(37,7,24,19,1.5);
INSERT INTO Item VALUES(37,6,34,12,1.5);
INSERT INTO Item VALUES(37,5,1,14,1.5);
INSERT INTO Item VALUES(37,4,13,20,1.5);
INSERT INTO Item VALUES(37,3,26,7,1.5);
INSERT INTO Item VALUES(37,2,36,8,1.5);
INSERT INTO Item VALUES(37,1,15,20,1.5);
INSERT INTO Item VALUES(37,0,41,24,1.5);
INSERT INTO Customer VALUES(38,'Andrew','Heiniger','347 College Av.','Lyon');
INSERT INTO Product VALUES(38,'Clock Ice Tea',216);
INSERT INTO Invoice VALUES(38,46,0.0);
INSERT INTO Item VALUES(38,19,4,7,1.5);
INSERT INTO Item VALUES(38,18,28,20,1.5);
INSERT INTO Item VALUES(38,17,32,4,1.5);
INSERT INTO Item VALUES(38,16,40,18,1.5);
INSERT INTO Item VALUES(38,15,47,10,1.5);
INSERT INTO Item VALUES(38,14,20,7,1.5);
INSERT INTO Item VALUES(38,13,8,7,1.5);
INSERT INTO Item VALUES(38,12,1,18,1.5);
INSERT INTO Item VALUES(38,11,19,18,1.5);
INSERT INTO Item VALUES(38,10,4,18,1.5);
INSERT INTO Item VALUES(38,9,27,20,1.5);
INSERT INTO Item VALUES(38,8,40,10,1.5);
INSERT INTO Item VALUES(38,7,15,1,1.5);
INSERT INTO Item VALUES(38,6,5,19,1.5);
INSERT INTO Item VALUES(38,5,48,17,1.5);
INSERT INTO Item VALUES(38,4,45,14,1.5);
INSERT INTO Item VALUES(38,3,27,19,1.5);
INSERT INTO Item VALUES(38,2,4,8,1.5);
INSERT INTO Item VALUES(38,1,45,13,1.5);
INSERT INTO Item VALUES(38,0,48,14,1.5);
INSERT INTO Customer VALUES(39,'Mary','Karsen','202 College Av.','Chicago');
INSERT INTO Product VALUES(39,'Ice Tea Shoe',154);
INSERT INTO Invoice VALUES(39,5,0.0);
INSERT INTO Item VALUES(39,3,20,17,1.5);
INSERT INTO Item VALUES(39,2,39,16,1.5);
INSERT INTO Item VALUES(39,1,24,6,1.5);
INSERT INTO Item VALUES(39,0,10,12,1.5);
INSERT INTO Customer VALUES(40,'Susanne','Miller','440 - 20th Ave.','Dallas');
INSERT INTO Product VALUES(40,'Shoe Clock',28);
INSERT INTO Invoice VALUES(40,4,0.0);
INSERT INTO Item VALUES(40,20,4,16,1.5);
INSERT INTO Item VALUES(40,19,7,23,1.5);
INSERT INTO Item VALUES(40,18,33,11,1.5);
INSERT INTO Item VALUES(40,17,4,20,1.5);
INSERT INTO Item VALUES(40,16,27,16,1.5);
INSERT INTO Item VALUES(40,15,22,12,1.5);
INSERT INTO Item VALUES(40,14,4,24,1.5);
INSERT INTO Item VALUES(40,13,6,8,1.5);
INSERT INTO Item VALUES(40,12,35,13,1.5);
INSERT INTO Item VALUES(40,11,27,2,1.5);
INSERT INTO Item VALUES(40,10,6,11,1.5);
INSERT INTO Item VALUES(40,9,40,17,1.5);
INSERT INTO Item VALUES(40,8,11,4,1.5);
INSERT INTO Item VALUES(40,7,31,1,1.5);
INSERT INTO Item VALUES(40,6,28,12,1.5);
INSERT INTO Item VALUES(40,5,32,18,1.5);
INSERT INTO Item VALUES(40,4,18,13,1.5);
INSERT INTO Item VALUES(40,3,26,10,1.5);
INSERT INTO Item VALUES(40,2,4,5,1.5);
INSERT INTO Item VALUES(40,1,45,24,1.5);
INSERT INTO Item VALUES(40,0,46,24,1.5);
INSERT INTO Customer VALUES(41,'Bill','King','546 College Av.','New York');
INSERT INTO Product VALUES(41,'Clock Ice Tea',206);
INSERT INTO Invoice VALUES(41,19,0.0);
INSERT INTO Item VALUES(41,11,48,15,1.5);
INSERT INTO Item VALUES(41,10,24,20,1.5);
INSERT INTO Item VALUES(41,9,26,21,1.5);
INSERT INTO Item VALUES(41,8,9,22,1.5);
INSERT INTO Item VALUES(41,7,22,18,1.5);
INSERT INTO Item VALUES(41,6,17,11,1.5);
INSERT INTO Item VALUES(41,5,9,21,1.5);
INSERT INTO Item VALUES(41,4,16,22,1.5);
INSERT INTO Item VALUES(41,3,29,20,1.5);
INSERT INTO Item VALUES(41,2,36,2,1.5);
INSERT INTO Item VALUES(41,1,47,19,1.5);
INSERT INTO Item VALUES(41,0,5,24,1.5);
INSERT INTO Customer VALUES(42,'Robert','Ott','503 Seventh Av.','Oslo');
INSERT INTO Product VALUES(42,'Iron Chair',198);
INSERT INTO Invoice VALUES(42,38,0.0);
INSERT INTO Item VALUES(42,4,48,15,1.5);
INSERT INTO Item VALUES(42,3,40,14,1.5);
INSERT INTO Item VALUES(42,2,40,19,1.5);
INSERT INTO Item VALUES(42,1,18,21,1.5);
INSERT INTO Item VALUES(42,0,48,9,1.5);
INSERT INTO Customer VALUES(43,'Susanne','Smith','2 Upland Pl.','Dallas');
INSERT INTO Product VALUES(43,'Telephone Clock',94);
INSERT INTO Invoice VALUES(43,13,0.0);
INSERT INTO Item VALUES(43,16,38,12,1.5);
INSERT INTO Item VALUES(43,15,48,7,1.5);
INSERT INTO Item VALUES(43,14,3,18,1.5);
INSERT INTO Item VALUES(43,13,44,22,1.5);
INSERT INTO Item VALUES(43,12,40,24,1.5);
INSERT INTO Item VALUES(43,11,49,23,1.5);
INSERT INTO Item VALUES(43,10,35,1,1.5);
INSERT INTO Item VALUES(43,9,7,23,1.5);
INSERT INTO Item VALUES(43,8,44,8,1.5);
INSERT INTO Item VALUES(43,7,11,15,1.5);
INSERT INTO Item VALUES(43,6,24,1,1.5);
INSERT INTO Item VALUES(43,5,33,6,1.5);
INSERT INTO Item VALUES(43,4,32,22,1.5);
INSERT INTO Item VALUES(43,3,6,18,1.5);
INSERT INTO Item VALUES(43,2,2,15,1.5);
INSERT INTO Item VALUES(43,1,18,19,1.5);
INSERT INTO Item VALUES(43,0,15,22,1.5);
INSERT INTO Customer VALUES(44,'Sylvia','Ott','361 College Av.','New York');
INSERT INTO Product VALUES(44,'Ice Tea Ice Tea',96);
INSERT INTO Invoice VALUES(44,32,0.0);
INSERT INTO Item VALUES(44,8,28,23,1.5);
INSERT INTO Item VALUES(44,7,49,17,1.5);
INSERT INTO Item VALUES(44,6,14,15,1.5);
INSERT INTO Item VALUES(44,5,41,22,1.5);
INSERT INTO Item VALUES(44,4,12,3,1.5);
INSERT INTO Item VALUES(44,3,3,14,1.5);
INSERT INTO Item VALUES(44,2,17,14,1.5);
INSERT INTO Item VALUES(44,1,34,17,1.5);
INSERT INTO Item VALUES(44,0,33,20,1.5);
INSERT INTO Customer VALUES(45,'Janet','May','396 Seventh Av.','Oslo');
INSERT INTO Product VALUES(45,'Iron Ice Tea',180);
INSERT INTO Invoice VALUES(45,42,0.0);
INSERT INTO Item VALUES(45,14,3,16,1.5);
INSERT INTO Item VALUES(45,13,47,8,1.5);
INSERT INTO Item VALUES(45,12,32,13,1.5);
INSERT INTO Item VALUES(45,11,31,22,1.5);
INSERT INTO Item VALUES(45,10,41,24,1.5);
INSERT INTO Item VALUES(45,9,26,18,1.5);
INSERT INTO Item VALUES(45,8,9,2,1.5);
INSERT INTO Item VALUES(45,7,6,24,1.5);
INSERT INTO Item VALUES(45,6,39,5,1.5);
INSERT INTO Item VALUES(45,5,45,17,1.5);
INSERT INTO Item VALUES(45,4,3,14,1.5);
INSERT INTO Item VALUES(45,3,14,11,1.5);
INSERT INTO Item VALUES(45,2,46,8,1.5);
INSERT INTO Item VALUES(45,1,11,6,1.5);
INSERT INTO Item VALUES(45,0,44,6,1.5);
INSERT INTO Customer VALUES(46,'Andrew','May','172 Seventh Av.','New York');
INSERT INTO Product VALUES(46,'Ice Tea Clock',62);
INSERT INTO Invoice VALUES(46,24,0.0);
INSERT INTO Item VALUES(46,17,12,23,1.5);
INSERT INTO Item VALUES(46,16,46,21,1.5);
INSERT INTO Item VALUES(46,15,40,11,1.5);
INSERT INTO Item VALUES(46,14,24,10,1.5);
INSERT INTO Item VALUES(46,13,36,20,1.5);
INSERT INTO Item VALUES(46,12,21,24,1.5);
INSERT INTO Item VALUES(46,11,1,4,1.5);
INSERT INTO Item VALUES(46,10,11,24,1.5);
INSERT INTO Item VALUES(46,9,7,4,1.5);
INSERT INTO Item VALUES(46,8,8,22,1.5);
INSERT INTO Item VALUES(46,7,49,9,1.5);
INSERT INTO Item VALUES(46,6,41,18,1.5);
INSERT INTO Item VALUES(46,5,25,9,1.5);
INSERT INTO Item VALUES(46,4,17,5,1.5);
INSERT INTO Item VALUES(46,3,21,19,1.5);
INSERT INTO Item VALUES(46,2,30,14,1.5);
INSERT INTO Item VALUES(46,1,12,24,1.5);
INSERT INTO Item VALUES(46,0,5,21,1.5);
INSERT INTO Customer VALUES(47,'Janet','Fuller','445 Upland Pl.','Dallas');
INSERT INTO Product VALUES(47,'Ice Tea Iron',178);
INSERT INTO Invoice VALUES(47,45,0.0);
INSERT INTO Item VALUES(47,13,33,8,1.5);
INSERT INTO Item VALUES(47,12,12,20,1.5);
INSERT INTO Item VALUES(47,11,35,10,1.5);
INSERT INTO Item VALUES(47,10,45,2,1.5);
INSERT INTO Item VALUES(47,9,32,9,1.5);
INSERT INTO Item VALUES(47,8,16,2,1.5);
INSERT INTO Item VALUES(47,7,28,14,1.5);
INSERT INTO Item VALUES(47,6,8,10,1.5);
INSERT INTO Item VALUES(47,5,40,8,1.5);
INSERT INTO Item VALUES(47,4,15,1,1.5);
INSERT INTO Item VALUES(47,3,1,4,1.5);
INSERT INTO Item VALUES(47,2,17,6,1.5);
INSERT INTO Item VALUES(47,1,23,13,1.5);
INSERT INTO Item VALUES(47,0,23,15,1.5);
INSERT INTO Customer VALUES(48,'Robert','White','549 Seventh Av.','San Francisco');
INSERT INTO Product VALUES(48,'Clock Clock',210);
INSERT INTO Invoice VALUES(48,22,0.0);
INSERT INTO Item VALUES(48,10,41,10,1.5);
INSERT INTO Item VALUES(48,9,35,17,1.5);
INSERT INTO Item VALUES(48,8,5,12,1.5);
INSERT INTO Item VALUES(48,7,30,19,1.5);
INSERT INTO Item VALUES(48,6,11,17,1.5);
INSERT INTO Item VALUES(48,5,24,16,1.5);
INSERT INTO Item VALUES(48,4,48,4,1.5);
INSERT INTO Item VALUES(48,3,10,2,1.5);
INSERT INTO Item VALUES(48,2,23,10,1.5);
INSERT INTO Item VALUES(48,1,26,23,1.5);
INSERT INTO Item VALUES(48,0,6,23,1.5);
INSERT INTO Customer VALUES(49,'George','Fuller','534 - 20th Ave.','Olten');
INSERT INTO Product VALUES(49,'Iron Iron',22);
INSERT INTO Invoice VALUES(49,32,0.0);
INSERT INTO Item VALUES(49,16,24,18,1.5);
INSERT INTO Item VALUES(49,15,19,24,1.5);
INSERT INTO Item VALUES(49,14,23,5,1.5);
INSERT INTO Item VALUES(49,13,6,22,1.5);
INSERT INTO Item VALUES(49,12,21,17,1.5);
INSERT INTO Item VALUES(49,11,40,15,1.5);
INSERT INTO Item VALUES(49,10,30,16,1.5);
INSERT INTO Item VALUES(49,9,7,24,1.5);
INSERT INTO Item VALUES(49,8,48,24,1.5);
INSERT INTO Item VALUES(49,7,6,21,1.5);
INSERT INTO Item VALUES(49,6,29,15,1.5);
INSERT INTO Item VALUES(49,5,16,1,1.5);
INSERT INTO Item VALUES(49,4,47,14,1.5);
INSERT INTO Item VALUES(49,3,17,19,1.5);
INSERT INTO Item VALUES(49,2,29,6,1.5);
INSERT INTO Item VALUES(49,1,22,16,1.5);
INSERT INTO Item VALUES(49,0,18,6,1.5);
SET REFERENTIAL_INTEGRITY TRUE;
UPDATE Product SET Price=ROUND(Price*.1,2);
UPDATE Item SET Cost=Cost*SELECT Price FROM Product prod WHERE ProductID=prod.ID;
UPDATE Invoice SET Total=SELECT SUM(Cost*Quantity) FROM Item WHERE InvoiceID=Invoice.ID;

COMMIT;
