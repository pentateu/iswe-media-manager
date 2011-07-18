package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileContext;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.IScraper;
import nz.co.iswe.mediamanager.scraper.IScrapingStatusObserver;
import nz.co.iswe.mediamanager.scraper.SearchResult;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class JMainWindow {
	private static final String MEDIA_FOLDER = "Media Folder";
	private static Logger log = Logger.getLogger(JMainWindow.class
			.getName());
	
	private JFrame frame;
	private JTextField mediaFolder;
	private JButton browseButton;
	private JButton scanNowButton;
	private JButton scrapingButton;
	private MediaListPanel mediaListPanel;
	private MediaDetailTabPanel mediaDetailTabPanel;
	
	protected String defaultScrapingMediaFolder = "";

	private MediaListPanelListener mediaListPanelListener = new MediaListPanelListener() {
		@Override
		public void notifyMediaDefinitionSelected(MediaDetail mediaFileDefinition) {
			//Show on the MediaDetailTabPanel
			mediaDetailTabPanel.showMediaDefinition(mediaFileDefinition);
		}
	};
	
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		UIUtils.setPreferredLookAndFeel();
		//NativeSwing.initialize();
		NativeInterface.open();
		//EventQueue.invokeLater(new Runnable() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//load the logging properties
					InputStream loggingProps = JMainWindow.class.getClassLoader().getResourceAsStream("logging.properties");
					LogManager.getLogManager().readConfiguration(loggingProps);
					
					String defaultScrapingMediaFolder = "";
					
					if(args.length > 0){
						defaultScrapingMediaFolder = args[0];
					}
					
					JMainWindow window = new JMainWindow(defaultScrapingMediaFolder);
					
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		NativeInterface.runEventPump();
	}
	
	public static JMainWindow getInstance(){
		return instance;
	}

	private static JMainWindow instance;
	
	/**
	 * Create the application.
	 */
	public JMainWindow() {
		initialize();
	}
	
	public JMainWindow(String defaultScrapingMediaFolder) {
		this.defaultScrapingMediaFolder = defaultScrapingMediaFolder;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JMainWindow.instance = this;
		frame = new JFrame();
		BorderLayout borderLayout = (BorderLayout) frame.getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		frame.setBounds(100, 100, 1279, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel topPanel = new JPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);

		mediaFolder = new JTextField(MEDIA_FOLDER);
		mediaFolder.setText(defaultScrapingMediaFolder);
		mediaFolder.setColumns(40);

		browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openBrowseFolderDialog();
			}
		});

		scanNowButton = new JButton("Scan Now !");
		scanNowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scanFolder();
			}
		});

		scrapingButton = new JButton("Start Scraping ...");
		scrapingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startScrap();
			}
		});

		mediaListPanel = new MediaListPanel();
		frame.getContentPane().add(mediaListPanel, BorderLayout.WEST);

		mediaListPanel.setListener(mediaListPanelListener);
		
		scrapingButton.setEnabled(false);
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		topPanel.add(mediaFolder);
		topPanel.add(browseButton);
		
		JPanel space1 = new JPanel();
		FlowLayout fl_space1 = (FlowLayout) space1.getLayout();
		fl_space1.setHgap(20);
		topPanel.add(space1);
		topPanel.add(scanNowButton);
		
		JPanel space2 = new JPanel();
		FlowLayout fl_space2 = (FlowLayout) space2.getLayout();
		fl_space2.setHgap(20);
		topPanel.add(space2);
		topPanel.add(scrapingButton);
		
		mediaDetailTabPanel = new MediaDetailTabPanel();
		frame.getContentPane().add(mediaDetailTabPanel, BorderLayout.CENTER);
		
	}

	private void openBrowseFolderDialog() {

	}

	private void scanFolder() {
		if (mediaFolder.getText().equals("") || mediaFolder.getText().equals(MEDIA_FOLDER)) {

			JOptionPane.showMessageDialog(frame, "Select a folder with your media before scan can start!",
					"Media folder not selected!", JOptionPane.CLOSED_OPTION);
			return;
		}

		// start scaning the folders

		File rootFolder = new File(mediaFolder.getText());

		if (!rootFolder.isDirectory()) {

			JOptionPane.showMessageDialog(frame, "Select a folder with your media before scan can start!",
					"Media folder not selected!", JOptionPane.CLOSED_OPTION);
			return;
		}

		try {
			scrapingButton.setEnabled(false);
			scanNowButton.setEnabled(false);
			browseButton.setEnabled(false);

			processFolder(rootFolder);
		} finally {
			scanNowButton.setEnabled(true);
			browseButton.setEnabled(true);
			scrapingButton.setEnabled(true);
		}

	}

	private void processFolder(File rootFolder) {
		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		for (File item : rootFolder.listFiles()) {
			if (item.isDirectory()) {
				processFolder(item);
			} else {
				// File
				MediaDetail mediaDetail = null;
				try {
					mediaDetail = mediaFileContext.getMediaFile(item);
					//there is not media details available, insert into the scraping queue
					if(mediaDetail != null && mediaDetail.getStatus().equals(MediaStatus.NO_MEDIA_DETAILS)){
						//check if it is the multi-part and if it is the first part
						mediaFileContext.addToScrapingQueue(mediaDetail);
					}
				} catch (MediaFileException e) {
					log.log(Level.SEVERE, "Error processing media file: " + item.getPath(), e);
					JOptionPane.showMessageDialog(frame, "Error processing media file!",
							"The following error occured: " + e.getMessage(), JOptionPane.CLOSED_OPTION);
					
				}
				if (mediaDetail != null && ! mediaListPanel.contains(mediaDetail)) {
					mediaListPanel.addMediaFile(mediaDetail);
				}
			}
		}
	}

	public void scrape(MediaDetail mediaDetail) {
		scrape(mediaDetail, null, null);
	}
	
	public void scrape(MediaDetail mediaDetail, IScraper scraper, SearchResult searchResult) {
		IScrapingStatusObserver observer = setupStatusBarForScraping(2);
		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		mediaFileContext.scrap(observer, scraper, mediaDetail, searchResult);
	}

	private void startScrap() {

		if (mediaListPanel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(frame, "There are no media files to scrap!", "No Media detected!",
					JOptionPane.CLOSED_OPTION);
			return;
		}
		
		IScrapingStatusObserver observer = setupStatusBarForScraping(mediaListPanel.getRowCount() + 1);

		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		mediaFileContext.startScrap(observer);
	}

	private IScrapingStatusObserver setupStatusBarForScraping(int overralStatusBarSize) {

		mediaFolder.setEnabled(false);
		browseButton.setEnabled(false);
		scanNowButton.setEnabled(false);
		scrapingButton.setEnabled(false);
		
		//setup the progress Bars
		final JProgressBar overralStatus = mediaListPanel.getOverralStatusBar();
		overralStatus.setMinimum(1);
		overralStatus.setMaximum(overralStatusBarSize);
		overralStatus.setValue(1);
		
		final JProgressBar itemStatus = mediaListPanel.getItemStatusBar();
		itemStatus.setMinimum(1);
		itemStatus.setMaximum(15);
		itemStatus.setValue(1);
		itemStatus.setVisible(true);
		
		IScrapingStatusObserver observer = new IScrapingStatusObserver() {

			int overralValue = 1;
			int itemValue = 1;
			
			@Override
			public void notifyErrorOccurred(String message, Throwable throwable) {
				JOptionPane.showMessageDialog(frame, "Error message: " + message, "Error during scraping!",
						JOptionPane.CLOSED_OPTION);

				throwable.printStackTrace();
			}

			@Override
			public void notifyScrapingFinished() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						mediaListPanel.refresh();
						
						mediaFolder.setEnabled(true);
						browseButton.setEnabled(true);
						scanNowButton.setEnabled(true);
						scrapingButton.setEnabled(true);
						
						overralStatus.setValue(overralStatus.getMaximum());//complete the status bar
						itemStatus.setVisible(false);
					}
				});
			}

			@Override
			public void notifyStepProgress() {
				itemValue++;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						itemStatus.setValue(itemValue);
					}
				});
			}

			@Override
			public void notifyNewStep() {
				itemValue = 1;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						itemStatus.setValue(itemValue);
					}
				});
			}

			@Override
			public void notifyStepFinished() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						itemStatus.setValue(itemStatus.getMaximum());
						overralValue++;
						overralStatus.setValue(overralValue);
					}
				});
			}

			@Override
			public void notifyScrapingStarted() {
				
			}

		};
		return observer;
	}

	
	
	

}
