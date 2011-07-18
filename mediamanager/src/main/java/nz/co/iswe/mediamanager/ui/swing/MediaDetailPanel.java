package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.file.CandidateMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class MediaDetailPanel extends JPanel {
	private static Logger log = Logger.getLogger(MediaDetailPanel.class.getName());
	
	
	private static final long serialVersionUID = -2257578511106370097L;
	private JLabel fileNameValueLabel;
	private JLabel yearValueLabel;
	private JPanel imageContainer;
	private ImagePanel imagePanel;
	private JButton scrapButton;
	
	private MediaDetail mediaDetail;
	private JLabel typeValueLabel;

	private MediaDetailPanelListener listener;
	private JLabel lblNewLabel_2;
	private JLabel exclusiveFolderValueLabel;
	private JButton moveToExclusiveFolderButton;
	private JLabel nfoFileValueLabel;
	private JLabel dateLastCrapValueLabel;
	
	private String nfoFileName = null;
	private JButton browserButton;
	
	/**
	 * Create the panel.
	 */
	public MediaDetailPanel() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[350.00px:350.00px:400.00px,grow][16.00][67][:100px:100px][][grow]", "[28.00px][28.00][28.00][][][][][grow][]"));
		
		imageContainer = new JPanel();
		imageContainer.setBackground(Color.WHITE);
		imageContainer.setBorder(new LineBorder(Color.LIGHT_GRAY));
		add(imageContainer, "cell 0 0 1 9,grow");
		imageContainer.setLayout(new BorderLayout(0, 0));
		
		imagePanel = new ImagePanel();
		imagePanel.setBackground(Color.WHITE);
		imageContainer.add(imagePanel, BorderLayout.CENTER);
		imagePanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Title : ");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblNewLabel, "cell 2 0");
		
		fileNameValueLabel = new JLabel("[File Name]");
		fileNameValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(fileNameValueLabel, "cell 3 0 3 1");
		
		JLabel lblNewLabel_1 = new JLabel("Type : ");
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblNewLabel_1, "cell 2 1");
		
		typeValueLabel = new JLabel("[Media Type]");
		typeValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(typeValueLabel, "cell 3 1 2 1");
		
		JLabel lblYear = new JLabel("Year : ");
		lblYear.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblYear, "cell 2 2");
		
		yearValueLabel = new JLabel("[Year]");
		yearValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(yearValueLabel, "cell 3 2");
		
		JLabel exclusiveFolderLabel = new JLabel("Exclusive Folder : ");
		exclusiveFolderLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		add(exclusiveFolderLabel, "cell 2 3,aligny center");
		
		exclusiveFolderValueLabel = new JLabel("");
		exclusiveFolderValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(exclusiveFolderValueLabel, "flowx,cell 3 3,alignx left,aligny center");
		
		moveToExclusiveFolderButton = new JButton(" Move");
		moveToExclusiveFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveToExclusiveFolder();
			}
		});
		moveToExclusiveFolderButton.setHorizontalAlignment(SwingConstants.RIGHT);
		moveToExclusiveFolderButton.setIcon(new ImageIcon(MediaDetailPanel.class.getResource("/nz/co/iswe/mediamanager/ui/img/move_folder_ico.png")));
		moveToExclusiveFolderButton.setToolTipText("Move to Exclusive Folder");
		add(moveToExclusiveFolderButton, "cell 4 3,alignx left");
		
		JLabel nfoFileLabel = new JLabel("NFO File :");
		nfoFileLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		add(nfoFileLabel, "cell 2 4");
		
		nfoFileValueLabel = new JLabel(" - ");
		nfoFileValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(nfoFileValueLabel, "cell 3 4 3 1,growx");
		
		dateLastCrapValueLabel = new JLabel(" ");
		dateLastCrapValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(dateLastCrapValueLabel, "cell 3 5 3 1");
		
		lblNewLabel_2 = new JLabel("Wrong Info ?");
		lblNewLabel_2.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblNewLabel_2, "cell 2 8,aligny center");
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(15);
		flowLayout.setVgap(0);
		panel.setBackground(Color.WHITE);
		add(panel, "cell 3 8 3 1,alignx left,aligny center");
		
		browserButton = new JButton("  Browser ");
		browserButton.setIcon(new ImageIcon(MediaDetailPanel.class.getResource("/nz/co/iswe/mediamanager/ui/img/browse_ico.png")));
		browserButton.setToolTipText("Open Browser");
		panel.add(browserButton);
		
		scrapButton = new JButton(" Scrape Again");
		scrapButton.setToolTipText("Scrape Media Detail");
		scrapButton.setIcon(new ImageIcon(MediaDetailPanel.class.getResource("/nz/co/iswe/mediamanager/ui/img/scrap_again_ico.png")));
		panel.add(scrapButton);
		scrapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrapAgain();
			}
		});
		browserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openBrowser();
			}
		});
		
		addComponentListener(new ComponentListener() 
		{  
		        // This method is called after the component's size changes
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            notifyResized(c.getSize());
		        }

				@Override
				public void componentHidden(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void componentShown(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}
		});

	}

	protected void moveToExclusiveFolder() {
		try {
			//dispose the image panel
			
			mediaDetail.moveToExclusiveFolder();
			
			if(mediaDetail.isInExclusiveFolder()){
				exclusiveFolderValueLabel.setText("Yes");
				moveToExclusiveFolderButton.setEnabled(false);
			}
		}
		catch (MediaFileException e) {
			log.log(Level.SEVERE, "Error moving media to exclusive folder : " + mediaDetail, e);
			JOptionPane.showMessageDialog(this,
					"The following error occured: " + e.getMessage(),
					"Error trying to move media to exclusive folder!",
					JOptionPane.CLOSED_OPTION);
		}
		
	}

	protected void notifyResized(Dimension size) {
		if(nfoFileName == null){
			return;
		}
		double width = size.getWidth() - 250;
		nfoFileValueLabel.setText(Util.shortString(nfoFileName, width, 0.070));
	}

	protected void openBrowser() {
		//
		if(listener != null){
			listener.showBrowser(mediaDetail);
		}
	}

	protected void scrapAgain() {
		if(isCandidate(mediaDetail)){
			
			//candidate info is correct ;-)
			CandidateMediaDetail candidateMediaDefinition = (CandidateMediaDetail)mediaDetail;
			try {
				candidateMediaDefinition.confirmCandidate();
			} catch (MediaFileException e) {
				log.log(Level.SEVERE, "Error confieming candidate : " + candidateMediaDefinition, e);
				JOptionPane.showMessageDialog(this,
						"The following error occured: " + e.getMessage(), 
						"Error confirming candidate!",
						JOptionPane.CLOSED_OPTION);
			}
			if(listener != null){
				listener.notifyCandidateConfirmed();
			}
		}
		else{
			//Locate the right scraper component
			JMainWindow.getInstance().scrape(mediaDetail);
		}
	}

	/**
	 * Populate the details on screen
	 * */
	public void showMediaDefinition(MediaDetail mediaDetail) {
		this.mediaDetail = mediaDetail;
		
		fileNameValueLabel.setText(mediaDetail.getTitle());
		yearValueLabel.setText("" + mediaDetail.getMediaNFO().getYear());
		
		
		nfoFileName = mediaDetail.getMediaNFO().getFile().getPath();
		nfoFileValueLabel.setToolTipText(nfoFileName);
		double width = this.getSize().getWidth();
		if(width < 300){
			width = 300;
		}
		nfoFileValueLabel.setText(Util.shortString(nfoFileName, width, 0.088));
		
		//default
		exclusiveFolderValueLabel.setText("No");
		moveToExclusiveFolderButton.setEnabled(true);
		
		try {
			if(mediaDetail.isInExclusiveFolder()){
				exclusiveFolderValueLabel.setText("Yes");
				moveToExclusiveFolderButton.setEnabled(false);
			}
		}
		catch (MediaFileException e) {
			//ignore
		}
		
		//Media Type
		if(mediaDetail.getMediaType() != null){
			typeValueLabel.setText(mediaDetail.getMediaType().asString());
		}
		
		//render the image
		renderImage(mediaDetail);
		
		if(isCandidate(mediaDetail)){
			scrapButton.setText("Awesome!");
			scrapButton.setToolTipText("Confirm that this is right media details.");
			scrapButton.setIcon(new ImageIcon(MediaDetailPanel.class.getResource("/nz/co/iswe/mediamanager/ui/img/good_ico.png")));
		}
	}

	private boolean isCandidate(MediaDetail mediaFileDefinition) {
		return mediaFileDefinition instanceof CandidateMediaDetail;
	}

	private void renderImage(MediaDetail mediaFileDefinition) {
		ImageInfo imageInfo = mediaFileDefinition.getPosterImage();
		
		if(imageInfo != null){
		
			BufferedImage image = imageInfo.getBufferedImage();
			if(image == null){
				try {
					File imageFile = imageInfo.getImageFile();
					try {
			        	image = ImageIO.read(imageFile);
						imageInfo.setBufferedImage(image);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Error loading Media Poster Image: " + imageFile.getPath(), e);
						throw new RuntimeException("Error loading Media Poster Image!", e);
					}
				} catch (MediaFileException e) {
					log.log(Level.SEVERE, "Error rendering image", e);
					JOptionPane.showMessageDialog(this, "The following error occured: " + e.getMessage(), "Error rendering image!",
							JOptionPane.CLOSED_OPTION);
				}
			}
			if(image != null){
				imagePanel.setImage(image);
			}
		}
	}
	
	public void setListener(MediaDetailPanelListener listener) {
		this.listener = listener;
	}
}
