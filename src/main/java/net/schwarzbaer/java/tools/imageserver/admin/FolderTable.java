package net.schwarzbaer.java.tools.imageserver.admin;

import net.schwarzbaer.java.tools.imageserver.data.Folder;
import net.schwarzbaer.java.tools.imageserver.data.FolderRepository;
import org.springframework.lang.NonNull;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FolderTable extends JTable {

	private final FolderTableModel tableModel;

	FolderTable(FolderRepository repo) {
		tableModel = new FolderTableModel(repo);
		setModel(tableModel);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnWidths();
	}

	private void setColumnWidths() {
		forEachColumn((col,colV)->{
			int colM = convertColumnIndexToModel(colV);
			FolderTableModel.ColumnID columnID = tableModel.getColumnID(colM);
			if (col!=null && columnID!=null) {
				col.setMinWidth(20);
				col.setWidth(columnID.width);
				col.setPreferredWidth(columnID.width);
			}
		});
	}

	private void forEachColumn(@NonNull BiConsumer<TableColumn, Integer> action) {
		if (columnModel!=null)
			for (int i=0; i<columnModel.getColumnCount(); i++) {
				TableColumn column = columnModel.getColumn(i);
				action.accept(column, i);
			}
	}

	public void updateTable() {
		tableModel.updateData();
		tableModel.fireTableDataChanged();
	}

	public void updateColumn( FolderTableModel.ColumnID columnID ) {
		tableModel.fireTableColumnUpdated(columnID);
	}

	public void addSelectionListener(ListSelectionListener listener) {
		selectionModel.addListSelectionListener(listener);
	}

	public Folder getSelectedRowItem() {
		int rowV = getSelectedRow();
		int rowM = rowV<0 ? -1 : convertRowIndexToModel(rowV);
		return rowM<0 ? null : tableModel.getRow(rowM);
	}

	public static class FolderTableModel extends AbstractTableModel {

		public enum ColumnID {
			Label( "Label"            , String.class, 150, Folder::getKey ),
			Path ( "Path"             , String.class, 400, Folder::getPath),
			Meta ( "Meta Data Folder" , String.class, 400, Folder::getMetaDataFolderPath),
			;
			private final String title;
			private final Class<?> columnClass;
			private final int width;
			private final Function<Folder, ?> getValue;

			@SuppressWarnings("SameParameterValue")
			<Type> ColumnID(String title, Class<Type> columnClass, int width, Function<Folder, Type> getValue ) {
				this.title = title;
				this.columnClass = columnClass;
				this.width = width;
				this.getValue = getValue;
			}
		}

		private final FolderRepository repo;
		private final ColumnID[] columns;
		private final List<Folder> data;

		FolderTableModel(FolderRepository repo) {
			this.repo = repo;
			columns = ColumnID.values();
			data = new ArrayList<>();
			updateData();
		}

		private void updateData() {
			data.clear();
			data.addAll(repo.getAllFolders(true));
		}

		public void fireTableColumnUpdated(ColumnID columnID) {
			int columnV = this.getColumn(columnID);
			if (columnV >= 0)
				fireTableChanged(new TableModelEvent(this, 0, getRowCount()-1, columnV));
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		public Folder getRow(int rowIndex) {
			if (0 <= rowIndex && rowIndex < data.size())
				return data.get(rowIndex);
			return null;
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@SuppressWarnings("SameParameterValue")
		private <Type> Type getValueFromColumn(int columnIndex, Function<ColumnID, Type> getValue, Type defaultValue) {
			ColumnID columnID = getColumnID(columnIndex);
			if (getValue!=null && columnID!=null)
				return getValue.apply(columnID);
			return defaultValue;
		}

		private int getColumn(ColumnID columnID) {
			for (int i=0; i<columns.length; i++)
				if (columns[i] == columnID)
					return i;
			return -1;
		}

		private ColumnID getColumnID(int columnIndex) {
			if (0 <= columnIndex && columnIndex < columns.length)
				return columns[columnIndex];
			return null;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return getValueFromColumn(columnIndex, columnID->columnID.title, null);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueFromColumn(columnIndex, columnID->columnID.columnClass, null);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ColumnID columnID = getColumnID(columnIndex);
			Folder row = getRow(rowIndex);

			if (row==null || columnID==null || columnID.getValue==null)
				return null;

			return columnID.getValue.apply(row);
		}
	}
}
