package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import SQL.tableData.Table;
import android.util.Log;
/**
 * https://www.w3schools.com/sql/sql_ref_keywords.asp
 * @author Joshu
 *
 */
public class SqlConnection {
	private Connection conn;
	
	public SqlConnection(String databaseName, String username, String password) throws Exception {
		try
		{
		   //Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e)
		{
			Log.e("error","it appears that your have not installed the driver for mysql connector");
		   throw e;
		}
		
		conn = DriverManager.getConnection (
				"jdbc:mysql://localhost/"+databaseName+
				"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				username, password
			);
	}

	public boolean tableExists(String tableName) throws SQLException {
		Table tableList = executeQuery(QueryBuilder.listAllAvailableTables);
		Log.i("table", tableList.toString());
		for(int i = 0; i<tableList.rowLength(); i++) {
			if(tableList.getValueAt(i, 0).equals(tableName)) { return true; }
		}
		return false;
	}
	public Table executeQuery(String command) throws SQLException {
		Statement s = conn.createStatement();
		try {
			return Table.from(s.executeQuery (command));
		}catch(Exception e) {
			Log.i("failed to execute", command);
			throw e;
		}
	}
	public void execute(String command) throws SQLException {
		Statement s = conn.createStatement();
		try {
			s.execute(command);
		}catch(Exception e) {
			Log.i("failed to execute", command);
			throw e;
		}
	}
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
