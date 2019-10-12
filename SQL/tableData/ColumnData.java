package SQL.tableData;

public class ColumnData {
	public final String name;
	public final String type;
	public final String[] modifiers;

	public static class DataTypes{
		public static String String(int length){ return "varchar("+length+")";}
		/** http://www.mysqltutorial.org/mysql-int/ **/ 
		public static String INTEGER = "INT", SHORT = "SMALLINT", BYTE = "TINYINT";
		
	}
	public static class Modifiers{
		/** https://www.w3schools.com/sql/sql_primarykey.asp*/public static final String primary_key = "PRIMARY KEY";
		/**https://www.w3schools.com/sql/sql_notnull.asp**/ public static final String not_null = "NOT NULL";
	}
	public ColumnData(String name,String type, String... modifiers) {
		this.name = name;
		this.type = type;
		this.modifiers = modifiers;
	}
	public boolean isPrimaryKey() {
		for(String s : modifiers) {
			if(s.equals(Modifiers.primary_key)) { return true; }
		}
		return false;
	}
}
