package SQL;

import SQL.tableData.ColumnData;
import SQL.tableData.Table;

public class QueryBuilder {
	public static String listAllAvailableTables = "show tables;";
	
	/**
	 * 
	 * @returnSELECT * FROM [table name] WHERE [field name] = "whatever";
	 */
	public static String queryTable(Table table, String... filters) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for(int i = 0; i<table.columns.size(); i++) {
			if(i<0) { sb.append(", "); }
			sb.append(table.columns.get(i).name);
		}
		sb.append(" FROM ");
		sb.append(table.name);
		if(filters.length>0) {
			sb.append(" WHERE ");
			for(int i = 0; i<filters.length; i++) {
				if(i>0) { sb.append(", "); }
				sb.append(filters[i]);
			}
		}
		sb.append(";");
		return sb.toString();
	}
	
	public static String deleteTable(String tableName) {	return "drop table ["+tableName+"];";}
	
	/**
	 * CREATE TABLE [table name] 
	 * (
	 * firstname VARCHAR(20),
	 *  datestamp DATE,
	 *  timestamp time,
	 *  );
	 * @param table
	 * @return
	 */
	public static String createTable(Table table) {
		StringBuilder sb = new StringBuilder("CREATE TABLE "+table.name+" (");
		String primaryKey = "";
		for(int i = 0; i<table.columns.size(); i++) {
			ColumnData dat = table.columns.get(i);
			if(i>0) { sb.append(", "); }
			if(dat.isPrimaryKey()) {
				sb.append( ColumnData.Modifiers.primary_key+" ("+dat.name+"), ");
			}
			sb.append(dat.name+" "+dat.type);
			for( String mod : dat.modifiers) {
				if(mod.equals(ColumnData.Modifiers.primary_key)) { continue; }
				sb.append(" "+mod);
			}
		}
		sb.append(");");
		return sb.toString();
	}
	
	
}
