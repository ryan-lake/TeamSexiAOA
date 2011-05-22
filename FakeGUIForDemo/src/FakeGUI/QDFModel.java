package FakeGUI;


import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class QDFModel {

	Timer poll = new Timer();
	Connection conn;
	
	public QDFModel() throws Exception{
		Class.forName("org.sqlite.JDBC");//Exception
	    //unix
		//conn = DriverManager.getConnection("jdbc:sqlite:/data/data/act.QDF/databases/QDFDatabase");
		//createDatabase()		
	}
	public void createDatabase() throws SQLException{
		pullConnection(); 
		Statement state = conn.createStatement();
		//tables
		 state.execute("create table settings (" +
		 		"tstamp date primary key, " +
		 		"dwelltime integer not null, " +
		 		"centerfreq integer not null, " +
		 		"read integer not null);");
		 state.execute("create table data (" +
		 			"tstamp date primary key, " +
			 		"location integer not null, " +
			 		"powerlevel integer not null);");
		 //triggers
		 state.execute("create trigger settings_tstamp after insert on settings" +
			 		" BEGIN" +
			 		" UPDATE settings set tstamp = strftime('%s','now') WHERE rowid = new.rowid;" +
			 		" END;");
		 state.execute("create trigger data_tstamp after insert on data" +
			 		" BEGIN" +
			 		" UPDATE settings set tstamp = strftime('%s','now') WHERE rowid = new.rowid;" +
			 		" END;");
		 conn.commit();//Necisary?
		 conn.close();
	}
	
	private void pullConnection() throws SQLException{
		conn = DriverManager.getConnection("jdbc:sqlite:QDFDatabase");
	}
	
	//public ActionListener getActLis(){
public void query(int freq, int dwell){//
				try {
					pullConnection();
					Statement state = conn.createStatement();
					 PreparedStatement prep = conn.prepareStatement(
					      "insert into settings values (?,?,?);");						
					
					 prep.setInt(1, 0);//dwell
					 prep.setInt(2, 0);//center
					 prep.setInt(3, 0);//read
					//state.executeQuery("insert data_tstamp ");
					
				} catch (SQLException e) {
					e.printStackTrace();
				}				
	}
	
	
	public void killTimer(){
		poll.cancel();
		poll = null;
	}
	
}//Model
/*
import java.sql.*;

public class Test {
  public static void main(String[] args) throws Exception {
    Class.forName("org.sqlite.JDBC");
    Connection conn =
      DriverManager.getConnection("jdbc:sqlite:test.db");
    Statement stat = conn.createStatement();
    stat.executeUpdate("drop table if exists people;");
    stat.executeUpdate("create table people (name, occupation);");
    PreparedStatement prep = conn.prepareStatement(
      "insert into people values (?, ?);");

    prep.setString(1, "Gandhi");
    prep.setString(2, "politics");
    prep.addBatch();
    prep.setString(1, "Turing");
    prep.setString(2, "computers");
    prep.addBatch();
    prep.setString(1, "Wittgenstein");
    prep.setString(2, "smartypants");
    prep.addBatch();

    conn.setAutoCommit(false);
    prep.executeBatch();
    conn.setAutoCommit(true);

    ResultSet rs = stat.executeQuery("select * from people;");
    while (rs.next()) {
      System.out.println("name = " + rs.getString("name"));
      System.out.println("job = " + rs.getString("occupation"));
    }
    rs.close();
    conn.close();
  }
}
   //////////////////   
      have found following examples of using JDBC with SQLite:

    For memory access:

     jdbc:sqlite::memory:

    For relative paths:

     jdbc:sqlite:relative/path.db

    For absolute paths:

     jdbc:sqlite:/absolute/path.db
      */
