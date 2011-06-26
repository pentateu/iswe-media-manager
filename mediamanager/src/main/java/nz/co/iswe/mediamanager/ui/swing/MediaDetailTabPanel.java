package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.CandidateMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.nfo.MovieFileNFO;

public class MediaDetailTabPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1454557244976992038L;
	private JLabel statusValueLabel;
	private JLabel filenameValueLabel;
	private JTabbedPane tabbedPane;
	
		
	/**
	 * Create the panel.
	 */
	public MediaDetailTabPanel() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		add(topPanel, BorderLayout.NORTH);
		
		JLabel filenameLabel = new JLabel("File : ");
		filenameLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		
		filenameValueLabel = new JLabel("[No Media Selected]");
		filenameValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		topPanel.setLayout(new MigLayout("", "[30px][379.00px][76.00][105.00]", "[14px][]"));
		topPanel.add(filenameLabel, "cell 0 0,alignx left,aligny top");
		topPanel.add(filenameValueLabel, "cell 1 0,growx,aligny top");
		
		JLabel statusLabel = new JLabel("Status : ");
		statusLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		topPanel.add(statusLabel, "cell 0 1");
		
		statusValueLabel = new JLabel("[Status]");
		statusValueLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
		topPanel.add(statusValueLabel, "cell 1 1 3 1");
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		add(tabbedPane, BorderLayout.CENTER);
		
		
	}

	private MediaDetail mediaFileDefinitionBeingDisplayed;
	
	/**
	 * Open a browser tab for the current media file
	 * @param mediaDetail 
	 */
	protected void showBrowserTab(MediaDetail mediaDetail) {
		BrowserPanel browserPanel = new BrowserPanel();
		
		//browser not yet in the tabs
		tabbedPane.addTab("Browser", null, browserPanel, null);
		tabbedPane.setSelectedComponent(browserPanel);
		
		String url = null;
		
		//get the browser URL
		
		//1: Try the Blog post
		if(null == null && mediaDetail.getBlogPostURL() != null ){
			url = mediaDetail.getBlogPostURL();
		}
		
		//2 : try the IMDB
		if(null == null && mediaDetail.getMediaNFO() != null ){
			MovieFileNFO movieFileNFO = (MovieFileNFO)mediaDetail.getMediaNFO();
			if(movieFileNFO.getImdbId() != null){
				url = Util.getImdbTitleURL(movieFileNFO.getImdbId());
			}
			else{
				url = Util.getImdbSearchByTitleURL(mediaDetail.getTitle());
			}
		}
		browserPanel.navigateTo(url);
	}
	
	public void showMediaDefinition(final MediaDetail mediaDetail) {
		
		if(mediaFileDefinitionBeingDisplayed != null && mediaFileDefinitionBeingDisplayed == mediaDetail){
			//void doing it twice
			return;
		}
		
		mediaFileDefinitionBeingDisplayed = mediaDetail;
		
		clear();
		
		//populate
		filenameValueLabel.setText(mediaDetail.getFileName());
		
		//status
		if(MediaStatus.CANDIDATE_DETAILS_FOUND.equals(mediaDetail.getStatus())){
			statusValueLabel.setText( "Candidate results found. Select a candidate below to review!" );
			
			int idx = 1;
			for(CandidateMediaDetail candidateMediaDefinition : mediaDetail.getCandidates()){
				MediaDetailPanel mediaDetailPanel = new MediaDetailPanel();
				mediaDetailPanel.setListener(new MediaDetailPanelListener() {
					@Override
					public void notifyCandidateConfirmed() {
						mediaFileDefinitionBeingDisplayed = null;
						showMediaDefinition(mediaDetail);
					}
					
					@Override
					public void showBrowser(MediaDetail mediaDetail) {
						showBrowserTab(mediaDetail);
					}
				});
				mediaDetailPanel.showMediaDefinition(candidateMediaDefinition);
				tabbedPane.addTab("Candidate: " + idx + " Details", null, mediaDetailPanel, null);
				idx++;
			}
			
		}
		else if(MediaStatus.CANDIDATE_LIST_FOUND.equals(mediaDetail.getStatus())){
			statusValueLabel.setText( "Candidate URLs found. Click on candidated tab below to review!" );
			
			
		}
		else if(MediaStatus.MEDIA_DETAILS_FOUND.equals(mediaDetail.getStatus())){
			statusValueLabel.setText( "Media Details Found" );
			
			//add the media details tab
			MediaDetailPanel mediaDetailPanel = new MediaDetailPanel();
			mediaDetailPanel.setListener(new MediaDetailPanelListener() {
				@Override
				public void notifyCandidateConfirmed() {
					//do nothing
				}
				
				@Override
				public void showBrowser(MediaDetail mediaDetail) {
					showBrowserTab(mediaDetail);
				}
			});
			mediaDetailPanel.showMediaDefinition(mediaDetail);
			tabbedPane.addTab("Media Details", null, mediaDetailPanel, null);
			
			//add the browser tab
			
		}
		else if(MediaStatus.MEDIA_DETAILS_NOT_FOUND.equals(mediaDetail.getStatus())){
			statusValueLabel.setText( "NO Details Found" );
			showBrowserTab(mediaDetail);
		}
		else {
			statusValueLabel.setText( "New! Click on the scrap button above to get the media details" );
		}
		
		
	}

	private void clear() {
		statusValueLabel.setText("[Status]");
		filenameValueLabel.setText("[No Media Selected]");
		
		tabbedPane.removeAll();
	}
	
}
