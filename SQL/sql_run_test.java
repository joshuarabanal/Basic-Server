package SQL;

import SQL.tableData.ColumnData;
import SQL.tableData.ColumnData.DataTypes;
import SQL.tableData.ColumnData.Modifiers;
import SQL.tableData.ColumnDataList;
import SQL.tableData.Table;
import android.util.Log;

public class sql_run_test {
	public static void main(String[] args) {
		try {
			SqlConnection sql = new SqlConnection("servicefromhome", "root", "SU0798ni");
			Log.i("table", sql.executeQuery(QueryBuilder.listAllAvailableTables).toString());
			if(!sql.tableExists("customer_info")) {
				ColumnDataList columns = new ColumnDataList();
				columns.add("first_name" , DataTypes.String(100),  Modifiers.not_null);
				columns.add("last_name" , DataTypes.String(100),  Modifiers.not_null);
				columns.add("phone_number" , DataTypes.String(100),  Modifiers.not_null);
				columns.add("email_address" , DataTypes.String(100),  Modifiers.not_null);
				columns.add("user_name" , DataTypes.String(100),  Modifiers.not_null);
				columns.add("password" , DataTypes.String(100), Modifiers.not_null);
				columns.add("employee_id" , DataTypes.INTEGER, Modifiers.primary_key );
				Table b = new Table.Builder(columns).build("customer_info");
				sql.execute(QueryBuilder.createTable(b));
			}
			else {
				Log.i("table customerInfo", "already exists");
			}
			
			sql.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
