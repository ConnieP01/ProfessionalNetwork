--The order of each load data matters, as the foreign keys in the tables call on previous loads first (such as calling on USR for userID)
--Michael He, 862151198
--Connie Pak, 862128598

COPY USR(userID, password, email, name, dateOfBirth)
FROM 'USR.csv'
DELIMITER ',' CSV HEADER;

COPY WORK_EXPR(userId, company, role, location, startDate, endDate)
FROM 'Work_Ex.csv'
DELIMITER ',' CSV HEADER;

COPY MESSAGE(msgId, senderId, receiverId, contents, sendTime, deleteStatus, status)
FROM 'Message.csv'
DELIMITER ',' CSV HEADER;

COPY EDUCATIONAL_DETAILS (userId, institutionName, major, degree, startdate, enddate)
FROM 'Edu_Det.csv'
DELIMITER ',' CSV HEADER;

COPY CONNECTION_USR(userId, connectionId, status)
FROM 'Connection.csv'
DELIMITER ',' CSV HEADER;