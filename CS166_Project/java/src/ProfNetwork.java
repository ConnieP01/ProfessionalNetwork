//Michael He, 862151198
//Connie Pak, 862128598

/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               //DEBUG - ADD MORE OPTIONS
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
				System.out.println("4. View messages");
                System.out.println("5. Send Friend Request");
				System.out.println("6. Manage Incoming Request");
				System.out.println("7. Search Profile");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4: ViewMessages(esql, authorisedUser); break;
				   case 5: SendRequest(esql, authorisedUser); break;
				   case 6: ManageIncomingRequest(esql, authorisedUser); break;
				   case 7: SearchProfile(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login if exists or null if the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here ---------------------------------------------------------------------------------------------------------------------
// Rest of the functions definition go in here ---------------------------------------------------------------------------------------------------------------------

   public static void ViewProfile(ProfNetwork esql, String currentUserID, String targetUserID){
      try{
        System.out.println("\tViewing profile of " + targetUserID);
        String query = String.format("SELECT userId, name, email, dateOfBirth FROM USR WHERE userId = '%s'", targetUserID);
        esql.executeQueryAndPrintResult(query);
        boolean ProfileMenu = true;
        while(ProfileMenu) {
            query = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userId='%s' AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE connectionId='%s' AND status = 'Accept'", currentUserID, currentUserID);
            esql.executeQueryAndPrintResult(query);
            System.out.println(""+targetUserID+"'s Profile");
            System.out.println("---------");
            System.out.println("1. View friends of "+targetUserID);
            System.out.println("2. Send a connection request");
            System.out.println("3. Send a message");
            System.out.println("9. < EXIT");
            switch (readChoice()){
                case 1: ProfileFriendsList(esql, currentUserID, targetUserID); break;
                case 2: SendRequestTarget(esql, currentUserID, targetUserID); break;
                case 3: SendMessage(esql, currentUserID, targetUserID); break;
                case 9: ProfileMenu = false; break;
                default : System.out.println("Unrecognized choice!"); break;
            }
        }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

    public static void ProfileFriendsList(ProfNetwork esql, String currentUserID, String targetUserID)
    {
         try{
         System.out.print("\t" + targetUserID+"'s Friends List: ");

         //find all users that currentuser sent an accepted request to
         boolean FriendMenu = true;
         while(FriendMenu) {
            String query = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userId='%s' AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE connectionId='%s' AND status = 'Accept'", targetUserID, targetUserID);
               esql.executeQueryAndPrintResult(query);
            System.out.println("View Friends");
            System.out.println("---------");
            System.out.println("1. View a friend's profile");
            System.out.println("9. < EXIT");
            switch (readChoice()){
               case 1: 
                  System.out.print("\tEnter a User ID to view profile: ");
                  String friendID = in.readLine();
                  String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", friendID);
                  if (esql.executeQuery(findUserQuery)>0)
                     ViewProfile(esql, currentUserID, friendID);
                  else
                     System.out.print("\tNo User found");
                  break;
               case 9: FriendMenu = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }
         }

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
    }//end
    

   public static void FriendList(ProfNetwork esql, String currentUserID){
      try{
         System.out.print("\tFriend List: ");

         //find all users that currentuser sent an accepted request to
         boolean FriendMenu = true;
         while(FriendMenu) {
            String query = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userId='%s' AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE connectionId='%s' AND status = 'Accept'", currentUserID, currentUserID);
               esql.executeQueryAndPrintResult(query);
            System.out.println("View Friends");
            System.out.println("---------");
            System.out.println("1. View a friend's profile");
            System.out.println("9. < EXIT");
            switch (readChoice()){
               case 1: 
                  System.out.print("\tEnter a friend's User ID to view profile: ");
                  String friendID = in.readLine();
                  String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", friendID);
                  if (esql.executeQuery(findUserQuery)>0)
                     ViewProfile(esql, currentUserID, friendID);
                  else
                     System.out.print("\tNo User found");
                  break;
               case 9: FriendMenu = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }
         }

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void UpdateProfile(ProfNetwork esql, String currentUserID){
      try{
         System.out.print("\tEnter New Password: ");
         String input = in.readLine();
         String query = String.format("UPDATE USR SET Password = '%s' WHERE userId='%s'", input, currentUserID);

         esql.executeUpdate(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static boolean CheckConnectionLevels(ProfNetwork esql, String currentUserID, String targetUserID){
        String query = String.format("SELECT DISTINCT C1.userId, C3.userId, C4.userId FROM CONNECTION_USR C1 inner join CONNECTION_USR C2 on (C1.userId=C2.connectionId and C1.status = 'Accept' and C2.status = 'Accept') or (C2.userId=C1.connectionId and C1.status = 'Accept' and C2.status = 'Accept')inner join CONNECTION_USR C3 on (C2.userId=C3.connectionId and C2.status = 'Accept' and C3.status = 'Accept') or (C3.userId=C2.connectionId and C2.status = 'Accept' and C3.status = 'Accept')inner join CONNECTION_USR C4 on (C3.userId=C4.connectionId and C3.status = 'Accept' and C4.status = 'Accept') or (C4.userId=C3.connectionId and C3.status = 'Accept' and C4.status = 'Accept') WHERE C1.userId<>C3.userId and C2.userId <> C4.userId and C1.userId = '%s' and (C3.userId = '%s' or C4.userId = '%s')", currentUserID, targetUserID, targetUserID); 
        try{
        if (CheckConnectionNumLT5(esql, currentUserID))
        {
            return true;
        }
        else if (esql.executeQuery(query)>0)
        {   
            return true; //return true if a connection is possible
        }
        else return false;
         }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
      }
   }

    public static boolean CheckConnectionNumLT5(ProfNetwork esql, String currentUserID){
        String query = String.format("SELECT COUNT(status) from CONNECTION_USR where status = 'Accept' and (userId = '%s' or connectionId = '%s') group by status having count(status)<5", currentUserID, currentUserID); 
        try{
		if (esql.executeQuery(query)>0)
        {   
            return true; //return true if connections is less than 5
        }
        else return false;
		}catch(Exception e){
			System.err.println(e.getMessage ());
			return false;
		}
   }

	public static void SendRequest(ProfNetwork esql, String currentUserID){
      try{
         System.out.print("\tEnter Recipient ID: ");
         String recipientID = in.readLine();
         String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", recipientID);
         String friendsAlready = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' and connectionId = '%s'", currentUserID, recipientID);
         String friendsAlready2 = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' and connectionId = '%s'", recipientID, currentUserID);
         if (esql.executeQuery(findUserQuery)>0)
            //check if user is within 3 connection levels
            if (CheckConnectionLevels(esql, currentUserID, recipientID) && !(esql.executeQuery(friendsAlready)>0 || esql.executeQuery(friendsAlready2)>0))
            {
                String query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", currentUserID, recipientID);
                esql.executeUpdate(query);
                System.out.print("\tConnection Request Sent");
            }
            else System.out.print("\tRequest not sent, connection level requirement not met");
         else
            System.out.print("\tNo User found");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void SendRequestTarget(ProfNetwork esql, String currentUserID, String targetUserID){
      try{
         String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", targetUserID);
         String friendsAlready = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' and connectionId = '%s'", currentUserID, targetUserID);
              String friendsAlready2 = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' and connectionId = '%s'", targetUserID, currentUserID);
         if (esql.executeQuery(findUserQuery)>0)
            //check if user is within 3 connection levels
            if (CheckConnectionLevels(esql, currentUserID,targetUserID) && !(esql.executeQuery(friendsAlready)>0 || esql.executeQuery(friendsAlready2)>0))
            {
                String query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", currentUserID, targetUserID);
                esql.executeUpdate(query);
                System.out.print("\tConnection Request Sent");
            }
            else System.out.print("\tRequest not sent, connection level requirement not met");
         else
            System.out.print("\tNo User found");
         System.out.print("\tMessage Sent");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
	public static void ManageIncomingRequest(ProfNetwork esql, String currentUserID){
      try{
         String PrintConnectionRequestsQuery = String.format("SELECT userId, status FROM CONNECTION_USR WHERE connectionId='%s' AND status = 'Request'", currentUserID);
         esql.executeQueryAndPrintResult(PrintConnectionRequestsQuery);
         boolean ConnectionMenu = true;
         while(ConnectionMenu) {
            System.out.println("MANAGE CONNECTION REQUESTS MENU");
            System.out.println("---------");
            System.out.println("1. Accept a connection");
            System.out.println("2. Reject a connection");
            System.out.println("9. < EXIT");
            switch (readChoice()){
               case 1: 
                  System.out.print("\tEnter a User ID to accept: ");
                  String requesterID = in.readLine();
                  String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", requesterID);
                  if (esql.executeQuery(findUserQuery)>0){
                     String query = String.format("UPDATE CONNECTION_USR SET status = 'Accept' WHERE userId='%s' and connectionId = '%s'", requesterID,currentUserID);
                     esql.executeUpdate(query);
                     System.out.print("\t"+requesterID+"'s connection request accepted");
                  }
                  else
                     System.out.print("\tNo User found");
                  break;
               case 2: 
                  System.out.print("\tEnter a User ID to reject: ");
                  String requesterID2 = in.readLine();
                  String findUserQuery2 = String.format("SELECT * FROM USR WHERE userId = '%s'", requesterID2);
                  if (esql.executeQuery(findUserQuery2)>0){
                     String query = String.format("UPDATE CONNECTION_USR SET status = 'Reject' WHERE userId='%s' and connectionId = '%s'",  requesterID2,currentUserID);
                     esql.executeUpdate(query);
                     System.out.print("\t"+requesterID2+"'s connection request rejected");
                  }
                  else
                     System.out.print("\tNo User found");
                  break;
               case 9: ConnectionMenu = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

	public static void SearchProfile(ProfNetwork esql, String currentUserID){
	  try{
		 System.out.print("\tEnter Profile ID to search: ");
		 String recipientID = in.readLine();
		 String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", recipientID);
		 if (esql.executeQuery(findUserQuery)>0)
			//check if user is within 3 connection levels
			ViewProfile(esql, currentUserID, recipientID);
		 else
			System.out.print("\tNo User found");
	  }catch(Exception e){
		 System.err.println (e.getMessage ());
	  }
   }//end
   
   public static void NewMessage(ProfNetwork esql, String currentUserID){
      try{
         System.out.print("\tEnter Recipient ID: ");
         String recipientID = in.readLine();
         System.out.print("\tEnter Message: ");
         String msg = in.readLine();
         String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", recipientID);
         if(esql.executeQuery(findUserQuery) > 0){
	         String query = String.format("INSERT INTO MESSAGE (senderId, receiverId, contents, deleteStatus, status) VALUES ('%s','%s','%s', '%s', '%s')", currentUserID, recipientID, msg, '0', "Delivered");
	         esql.executeQuery(query);
			 String query2 = String.format("UPDATE MESSAGE SET status = 'Accept' WHERE userId='%s' and connectionId = '%s'", recipientID, currentUserID);
             esql.executeUpdate(query2);
	         System.out.println("\tMessage Delivered");
         }
         else{
         	 System.out.println("\tFailed to deliver");
         }
	  }catch(Exception e){
         	System.err.println(e.getMessage());
         }
	}//end
	
	public static void SendMessage(ProfNetwork esql, String currentUserID, String targetUserID){
     try{
         System.out.print("\tEnter Message: ");
         String msg = in.readLine();
         String findUserQuery = String.format("SELECT * FROM USR WHERE userId = '%s'", targetUserID);
         if(esql.executeQuery(findUserQuery) > 0){
	         String query = String.format("INSERT INTO MESSAGE (senderId, receiverId, contents, deleteStatus, status) VALUES ('%s','%s','%s', '%s', '%s')", currentUserID, targetUserID, msg, '0', "Delivered");
	         esql.executeQuery(query);
	         System.out.println("\tMessage Delivered");
         }
         else{
         	 System.out.println("\tFailed to deliver");
         }
	  }catch(Exception e){
         	System.err.println(e.getMessage());
         }
	}//end
	
	public static void ViewMessages(ProfNetwork esql, String currentUserID) {
		try{
			System.out.print("\tViewing messages:");
			//if sender
			String query1 = String.format("SELECT DISTINCT contents, msgId FROM MESSAGE, USR WHERE senderId = '%s' and (deleteStatus = '0' or deleteStatus = '2')", currentUserID);
			esql.executeQueryAndPrintResult(query1);
			//if receiver
			String query = String.format("SELECT DISTINCT contents, msgId FROM MESSAGE, USR WHERE receiverId = '%s' and (deleteStatus = '0' or deleteStatus = '1')", currentUserID);
			esql.executeQueryAndPrintResult(query);
		
			boolean CheckMessage = true;
			while(CheckMessage == true){
				System.out.println("Delete a message?");
				System.out.println("----------------");
				System.out.println("1. Yes");
				System.out.println("2. < EXIT");
				switch(readChoice()){
					case 1: DeleteMessages(esql, currentUserID);  break;
					case 2: CheckMessage = false; break;
					default: System.out.println("Unrecognized choice!"); break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static void DeleteMessages(ProfNetwork esql, String currentUserId){
		try{
			System.out.print("\tEnter Message ID: ");
			String deleteID = in.readLine();
			
			//if sender
			String delStat = String.format("SELECT contents FROM MESSAGE WHERE senderId = '%s' and msgID = '%s' and deleteStatus = '0'", currentUserId, deleteID);
			if(esql.executeQuery(delStat) > 0){
				String updateDel = String.format("UPDATE MESSAGE SET deleteStatus = '1' WHERE senderId = '%s' and msgID = '%s'", currentUserId, deleteID);
				esql.executeUpdate(updateDel);
				String delMessage = String.format("DELETE FROM MESSAGE WHERE msgID = '%s' and senderId = '%'", deleteID, currentUserId);
				esql.executeQuery(delMessage);
				System.out.println("Message deleted!");
			}
			else{
				delStat = String.format("SELECT contents FROM MESSAGE WHERE senderId = '%s' and msgID = '%s' and deleteStatus = '2'", currentUserId, deleteID);
				if(esql.executeQuery(delStat) > 0){
					String updateDel1 = String.format("UPDATE MESSAGE SET deleteStatus = '3' WHERE senderId = '%s' and msgID = '%s'", currentUserId, deleteID);
					esql.executeUpdate(updateDel1);
					String delMessage1 = String.format("DELETE FROM MESSAGE WHERE msgID = '%s' and senderId = '%'", deleteID, currentUserId);
					esql.executeQuery(delMessage1);
					System.out.println("Message deleted!");
				}
			}
			
			//if receiver
			String delStat1 = String.format("SELECT contents FROM MESSAGE WHERE receiverId = '%s' and msgID = '%s' and deleteStatus = '0'", currentUserId, deleteID);
			if(esql.executeQuery(delStat1) > 0){
				String updateDel2 = String.format("UPDATE MESSAGE SET deleteStatus = '2' WHERE receiverId = '%s' and msgID = '%s'", currentUserId, deleteID);
				esql.executeUpdate(updateDel2);
				String delMessage3 = String.format("DELETE FROM MESSAGE WHERE msgID = '%s' and receiverId = '%'", deleteID, currentUserId);
				esql.executeQuery(delMessage3);
				System.out.println("Message deleted!");
			}
			else{
				delStat1 = String.format("SELECT contents FROM MESSAGE WHERE receiverId = '%s' and msgID = '%s' and deleteStatus = '1'", currentUserId, deleteID);
				if(esql.executeQuery(delStat1) > 0){
					String updateDel3 = String.format("UPDATE MESSAGE SET deleteStatus = '3' WHERE receiverId = '%s' and msgID = '%s'", currentUserId, deleteID);
					esql.executeUpdate(updateDel3);
					String delMessage4 = String.format("DELETE FROM MESSAGE WHERE msgID = '%s' and receiverId = '%'", deleteID, currentUserId);
					esql.executeQuery(delMessage4);
					System.out.println("Message deleted!");
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end
}//end ProfNetwork