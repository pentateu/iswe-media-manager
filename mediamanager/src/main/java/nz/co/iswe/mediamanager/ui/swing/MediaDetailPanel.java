package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
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
	private JButton actionButton;
	
	private MediaDetail mediaFileDefinition;
	private JLabel typeValueLabel;

	private MediaDetailPanelListener listener;
	
	/**
	 * Create the panel.
	 */
	public MediaDetailPanel() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[393.00px][16.00][67.00][235.00]", "[28.00px][28.00][28.00][][][381.00][]"));
		
		imageContainer = new JPanel();
		imageContainer.setBackground(Color.WHITE);
		imageContainer.setBorder(new LineBorder(Color.LIGHT_GRAY));
		add(imageContainer, "cell 0 0 1 7,grow");
		imageContainer.setLayout(new BorderLayout(0, 0));
		
		imagePanel = new ImagePanel();
		imagePanel.setBackground(Color.WHITE);
		imageContainer.add(imagePanel, BorderLayout.CENTER);
		imagePanel.setLayout(null);
		
		actionButton = new JButton("Wrong Info :-(   Scrape Again");
		actionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performAction();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Title : ");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblNewLabel, "cell 2 0");
		
		fileNameValueLabel = new JLabel("[File Name]");
		fileNameValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(fileNameValueLabel, "cell 3 0");
		
		JLabel lblNewLabel_1 = new JLabel("Type : ");
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblNewLabel_1, "cell 2 1");
		
		typeValueLabel = new JLabel("[Media Type]");
		typeValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(typeValueLabel, "cell 3 1");
		
		JLabel lblYear = new JLabel("Year : ");
		lblYear.setFont(new Font("Verdana", Font.BOLD, 14));
		add(lblYear, "cell 2 2");
		
		yearValueLabel = new JLabel("[Year]");
		yearValueLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
		add(yearValueLabel, "cell 3 2");
		add(actionButton, "cell 3 6,alignx right");

	}

	protected void performAction() {
		if(isCandidate(mediaFileDefinition)){
			//candidate info is correct ;-)
			CandidateMediaDetail candidateMediaDefinition = (CandidateMediaDetail)mediaFileDefinition;
			try {
				candidateMediaDefinition.confirmCandidate();
			} catch (MediaFileException e) {
				log.log(Level.SEVERE, "Error confieming candidate : " + candidateMediaDefinition, e);
				JOptionPane.showMessageDialog(this, "Error confirming candidate!",
						"The following error occured: " + e.getMessage(), JOptionPane.CLOSED_OPTION);
			}
			if(listener != null){
				listener.notifyCandidateConfirmed();
			}
		}
		else{
			//TODO: Media details is wrong, scrap again
			
		}
	}

	public void showMediaDefinition(MediaDetail mediaFileDefinition) {
		this.mediaFileDefinition = mediaFileDefinition;
		
		fileNameValueLabel.setText(mediaFileDefinition.getTitle());
		
		yearValueLabel.setText("" + mediaFileDefinition.getMediaNFO().getYear());
		
		//Media Type
		if(mediaFileDefinition.getMediaType() != null){
			typeValueLabel.setText(mediaFileDefinition.getMediaType().asString());
		}
		
		//render the image
		renderImage(mediaFileDefinition);
		
		if(isCandidate(mediaFileDefinition)){
			actionButton.setText("Awesome! That is the right info.");
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
					JOptionPane.showMessageDialog(this, "Error rendering image!",
							"The following error occured: " + e.getMessage(), JOptionPane.CLOSED_OPTION);
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
