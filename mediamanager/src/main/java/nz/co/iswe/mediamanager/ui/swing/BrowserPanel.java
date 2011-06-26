package nz.co.iswe.mediamanager.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.scraper.IScraper;
import nz.co.iswe.mediamanager.scraper.ScraperContext;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;

public class BrowserPanel extends JPanel {
		
	private static final long serialVersionUID = -2264565511106370097L;
	//private static Logger log = Logger.getLogger(BrowserPanel.class.getName());
	
	private JWebBrowser webBrowser;
	private MediaDetail mediaDetail;
	
	public BrowserPanel(MediaDetail mediaDetail) {
		super(new BorderLayout());
		
		this.mediaDetail = mediaDetail;
		
		//JPanel webBrowserPanel = new JPanel(new BorderLayout());
	    //webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Native Web Browser component"));
	    
	    webBrowser = new JWebBrowser();
	    
	    //webBrowser.navigate("http://www.google.com");
	    
	    //webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
	    
	    //add(webBrowserPanel, BorderLayout.CENTER);
	    add(webBrowser, BorderLayout.CENTER);
	    
	    webBrowser.setMenuBarVisible(false);
	    
	    
	    // Create an additional bar allowing to show/hide the menu bar of the web browser.
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
	    JButton scrapeButton = new JButton("Scrape Media Details");
	    scrapeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Invoke the scraper for this site
				scrape();
			}
	    });
	    
	    buttonPanel.add(scrapeButton);
	    add(buttonPanel, BorderLayout.SOUTH);
		
	    //listener to when the browser URL changes.
	    webBrowser.addWebBrowserListener(new WebBrowserListenerAdapter(){
	    	@Override
	    	public void locationChanging(WebBrowserNavigationEvent e) {
	    		String location = e.getNewResourceLocation();
	    		
	    		//check if there is any scraper for this url
	    		location.toString();
	    		
	    	}
	    });
	}


	protected void scrape() {
		//Locate the right scraper component
		String url = webBrowser.getResourceLocation();
		IScraper scraper = ScraperContext.getInstance().getScraperByURL(url);
		if(scraper == null){
			//can scrape this page
			JOptionPane.showMessageDialog(this, "You cannot scrape this page at the moment! There is not scraper component that support this page. Check the updates and try again.",
					"Current page does not have a scraper!", JOptionPane.CLOSED_OPTION);
		}
		scraper.setURLToScrape(url);
		JMainWindow.getInstance().scrape(scraper, mediaDetail);
	}


	public void navigateTo(String url) {
		webBrowser.navigate(url);
	}
	

	
}
