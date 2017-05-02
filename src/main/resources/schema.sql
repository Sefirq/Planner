CREATE TABLE meetings
 (
   id int(10) NOT NULL AUTO_INCREMENT,
   name VARCHAR2(100) NOT NULL,
   description VARCHAR2(250) DEFAULT NULL,
   duration INT(3) NOT NULL,
   date VARCHAR2(10) NOT NULL,
   time VARCHAR2(5) NOT NULL,
   meetingRoomID int(10) NOT NULL,
   PRIMARY KEY (id),
--    FOREIGN KEY (meetingRoomID) REFERENCES meetingRooms(id),
 );