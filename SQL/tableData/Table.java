package SQL.tableData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import android.util.Log;

public class Table {
	public final String name;
	public ColumnDataList columns = new ColumnDataList();
	private ArrayList<String[]> data;
	
	private Table(String name) {
		this.name = name;
	}
	public static Table from(ResultSet res) throws SQLException {
	
		
		ResultSetMetaData meta =  res.getMetaData();
		ColumnDataList columns = new ColumnDataList();
		for(int i = 0; i<meta.getColumnCount(); i++) {
			columns.add(meta.getColumnName(i+1), meta.getColumnTypeName(i+1));
		}
		Builder b = new Builder(columns);
		
		int row = 0;
		while (res.next()) {
            for(int i = 0;  i<columns.size(); i++) {
            	b.addItem(row, i, res.getString(i+1));
            }
            row++;
        }
		return b.build("query_results");
	
	}
	
	public String getValueAt(int row, int column) {
		return data.get(row)[column];
	}
	public int getColumnIndexByName(String name) {
		for(int i = 0; i<columns.size(); i++) {
			if(columns.get(i).name.equals(name)) {
				return i;
			}
		}
		throw new IndexOutOfBoundsException("unable to find column by name:"+name);
	}
	public int rowLength() { return data.size(); }
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i< columns.size(); i++) {
			if(i > 0 ) { sb.append(", "); }
			sb.append(columns.get(i).name);
		}
		sb.append("\n");
		for(int r = 0 ; r<data.size(); r++) {
			String[] row = data.get(r);
			for(int c = 0; c<row.length; c++) {
				if(c>0) { sb.append(", "); }
				sb.append(row[c]);
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	public static class Builder{
		private ColumnDataList columns;
		ArrayList<String[]> data = new ArrayList<String[]>();
		
		public Builder(ColumnDataList columns) {
			this.columns = columns;
		}
		public void addItem(int row, int column,  String val) {
			 getRow(row)[column] = val;
		}
		private String[] getRow(int row) {
			while(row>=data.size()) { data.add(new String[columns.size()]); }
			return data.get(row);
		}
		public Table build(String tableName) {
			Table t =  new Table(tableName);
			t.columns = columns;
			t.data = data;
			return t;
		}
	}
}
