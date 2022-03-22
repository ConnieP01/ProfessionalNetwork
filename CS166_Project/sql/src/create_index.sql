--Michael He, 862151198
--Connie Pak, 862128598

CREATE INDEX userIdent
ON USR (userId, password, email, name, dateOfBirth);

CREATE INDEX messageIdent
ON MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status);

CREATE INDEX connecUser
ON CONNECTION_USR (userId, connectionId, status);