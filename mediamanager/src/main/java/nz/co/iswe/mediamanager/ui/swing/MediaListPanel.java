package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.MediaFileListener;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class MediaListPanel extends JPanel {

	private static final long serialVersionUID = -8406394072419126992L;

	private JTable table;

	private MediaTableDataModel model = new MediaTableDataModel();
	private JProgressBar itemStatusBar;
	private JProgressBar overralStatusBar;
	
	private MediaListPanelListener listener;
	
	/**
	 * Create the panel.
	 */
	public MediaListPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				tableRowSelected(e);
			}
		});
		table.setModel(model);
		
		scrollPane.setViewportView(table);
		
		JPanel statusBarPanel = new JPanel();
		add(statusBarPanel, BorderLayout.SOUTH);
		statusBarPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		itemStatusBar = new JProgressBar();
		statusBarPanel.add(itemStatusBar);
		
		overralStatusBar = new JProgressBar();
		statusBarPanel.add(overralStatusBar);

		
		configTableColumns();
	}
	
	protected void tableRowSelected(ListSelectionEvent e) {
		
		if(listener != null){
			int row = table.getSelectedRow();
			if(row >= 0){
				MediaDetail mediaFileDefinition = model.getMediaFileDefinition(row);
				listener.notifyMediaDefinitionSelected(mediaFileDefinition);
			}
		}
		
	}

	private void configTableColumns() {
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(45);
		table.getColumnModel().getColumn(1).setPreferredWidth(25);
	}

	class MediaTableDataModel extends AbstractTableModel{

		private static final long serialVersionUID = 6895701470928126018L;


		private String[] columnNames = {"Name",
	            "Status",
	            "Media Type"};
		
		
		private List<MediaDetail> definitions = new ArrayList<MediaDetail>();
		
		@Override
		public int getRowCount() {
			return definitions.size();
		}

		public MediaDetail getMediaFileDefinition(int row) {
			return definitions.get(row);
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			MediaDetail mediaFileDefinition = definitions.get(rowIndex);
			
			if(columnIndex == 0){
				return mediaFileDefinition.getTitle();
			}
			if(columnIndex == 1){
				if(MediaStatus.CANDIDATE_DETAILS_FOUND.equals(mediaFileDefinition.getStatus()) || MediaStatus.CANDIDATE_LIST_FOUND.equals(mediaFileDefinition.getStatus())){
					return "REVIEW";
				}
				else if(MediaStatus.MEDIA_DETAILS_FOUND.equals(mediaFileDefinition.getStatus())){
					return "Ok";
				}
				else if(MediaStatus.MEDIA_DETAILS_NOT_FOUND.equals(mediaFileDefinition.getStatus())){
					return "Nothing Found";
				}
				else if(MediaStatus.ERROR.equals(mediaFileDefinition.getStatus())){
					return "Error !";
				}
				else {
					return "Brand New";
				}
			}
			if(columnIndex == 2){
				if( mediaFileDefinition.getMediaType() != null ){
					return mediaFileDefinition.getMediaType().asString();
				}
				else{
					return "";
				}
			}
			
			
			return null;
		}

		public void add(MediaDetail mediaDetail) {
			definitions.add(mediaDetail);
			final int idx = definitions.size() - 1;
			fireTableRowsInserted(idx, idx);
			
			mediaDetail.addListener(new MediaFileListener() {
				@Override
				public void notifyChange(IMediaDetail mediaDefinition) {
					fireTableRowsUpdated(idx, idx);
				}

				@Override
				public void notifyMediaFileRenamed(MediaDetail mediaFile) throws MediaFileException {
					//ignore
				}
			});
		}

		public boolean contains(MediaDetail mediaDetail) {
			return definitions.contains(mediaDetail);
		}
		
		
	}

	public void addMediaFile(MediaDetail mediaFileDefinition) {
		model.add(mediaFileDefinition);
	}

	public int getRowCount() {
		return model.getRowCount();
	}

	public void refresh() {
		model.fireTableDataChanged();
	}

	public JProgressBar getItemStatusBar() {
		return itemStatusBar;
	}

	public JProgressBar getOverralStatusBar() {
		return overralStatusBar;
	}
	
	public void setListener(MediaListPanelListener listener) {
		this.listener = listener;
	}

	public boolean contains(MediaDetail mediaDetail) {
		return model.contains(mediaDetail);
	}
	
}
