package com.worden.db;


public class Row {

	private Cell[] cellList = null ;
	
	public void setCellList(Cell[] cellList ) {
		this.cellList = cellList ;
	}
	
	public Cell[] getCellList() {
		return this.cellList ;
	}
	
}
