package SQL.tableData;

import java.util.ArrayList;

public class ColumnDataList extends ArrayList<ColumnData> {
	
	/**
	 * not used
	 */
	private static final long serialVersionUID = 1L;

	public void add(String name, String type, String... modifiers) {
		add(new ColumnData(name, type, modifiers));
	}

}
