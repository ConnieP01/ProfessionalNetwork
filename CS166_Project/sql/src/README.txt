---Indexes---
We used indexes to help with how long it takes for all the data to be compiled because of the large data files. With the indexes, it would help with the USR, MESSAGE, and CONNECTION data files. Only those three were used because the other tables were not necessary or used within this program. 

---Triggers---
We created a trigger to help with keeping track of each new message that is created. Whenever a user sends a new message, the message requires it's own message ID, however because of the already long message data csv file, it was difficult to add a new ID to each new message. For this problem, the trigger helped by starting off the initial sequence with 50000. This way for each new message, it would go on to make a incrementing ID(NEW.msgID = nextval('MESSAGE_SEQ')).