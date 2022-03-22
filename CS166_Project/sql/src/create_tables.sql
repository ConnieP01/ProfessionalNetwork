--Michael He, 862151198
--Connie Pak, 862128598
--Added in the foreign key constraints where necessary, updated varchar to 40 as the size was too low for some of the inputs, and changed instituitionName to institutionName in EDUCATIONAL_DETAILS


DROP TABLE WORK_EXPR;
DROP TABLE EDUCATIONAL_DETAILS;
DROP TABLE MESSAGE;
DROP TABLE CONNECTION_USR;
DROP TABLE USR;


CREATE TABLE USR(
	userId varchar(40) UNIQUE NOT NULL, 
	password varchar(40) NOT NULL,
	email text NOT NULL,
	name char(50),
	dateOfBirth date,
	Primary Key(userId));

CREATE TABLE WORK_EXPR(
	userId char(40) NOT NULL, 
	company char(40) NOT NULL, 
	role char(50) NOT NULL,
	location char(40),
	startDate date,
	endDate date,
	PRIMARY KEY(userId,company,role,startDate),
	FOREIGN KEY(userId) REFERENCES USR(userId));

CREATE TABLE EDUCATIONAL_DETAILS(
	userId char(40) NOT NULL, 
	institutionName char(50) NOT NULL, 
	major char(50) NOT NULL,
	degree char(50) NOT NULL,
	startdate date,
	enddate date,
	PRIMARY KEY(userId,major,degree),
	FOREIGN KEY(userId) REFERENCES USR(userId));

CREATE TABLE MESSAGE(
	msgId integer UNIQUE NOT NULL, 
	senderId char(40) NOT NULL,
	receiverId char(40) NOT NULL,
	contents char(500) NOT NULL,
	sendTime timestamp,
	deleteStatus integer,
	status char(30) NOT NULL,
	PRIMARY KEY(msgId),
	FOREIGN KEY(senderId) REFERENCES USR(userId));

CREATE TABLE CONNECTION_USR(
	userId char(40) NOT NULL, 
	connectionId char(40) NOT NULL, 
	status char(30) NOT NULL,
	PRIMARY KEY(userId,connectionId),
	FOREIGN KEY(userId) REFERENCES USR(userId));