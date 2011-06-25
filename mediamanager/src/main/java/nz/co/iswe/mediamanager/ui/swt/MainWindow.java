package nz.co.iswe.mediamanager.ui.swt;

import java.io.File;

import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaFile;
import nz.co.iswe.mediamanager.media.file.MediaFileContext;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.IScrapingStatusObserver;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MainWindow {

	private static final String MEDIA_FOLDER = "Media Folder";
	private Shell shell = null; // @jve:decl-index=0:visual-constraint="38,29"
	private Text rootFolderText = null;
	private Button browseFolderButton = null;
	private List list = null;
	private Button lookUpOneDDLButton = null;
	private Button searchImdbButton = null;
	private Button moveToMediaFolderCheckBox = null;
	private Composite contentComposite = null;
	private Browser browser = null;
	private Button scanFolderButton = null;
	private ListViewer listViewer = null;
	private Button scrapFolder = null;
	private ProgressBar progressBar = null;

	/**
	 * This method initializes sShell
	 * 
	 */
	@SuppressWarnings("unused")
	private void createSShell() {
		GridData gridData22 = new GridData();
		gridData22.widthHint = 260;
		GridData gridData17 = new GridData();
		gridData17.horizontalAlignment = GridData.END;
		gridData17.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.widthHint = 100;
		GridData gridData6 = new GridData();
		gridData6.widthHint = 100;
		GridData gridData21 = new GridData();
		gridData21.widthHint = 250;
		GridData gridData1 = new GridData();
		gridData1.verticalSpan = 2;
		gridData1.heightHint = 400;
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.verticalAlignment = GridData.BEGINNING;
		gridData1.widthHint = 250;
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		shell = new Shell();
		shell.setText("ISWE Media Manager");
		shell.setLayout(gridLayout);
		shell.setSize(new Point(957, 520));
		rootFolderText = new Text(shell, SWT.BORDER);
		rootFolderText.setText("G:\\Media");
		rootFolderText.setLayoutData(gridData21);
		browseFolderButton = new Button(shell, SWT.NONE);
		browseFolderButton.setText("Browse");
		browseFolderButton.setLayoutData(gridData);
		Label filler4 = new Label(shell, SWT.NONE);
		scanFolderButton = new Button(shell, SWT.NONE);
		scanFolderButton.setText("Scan Folder !");
		Label filler5 = new Label(shell, SWT.NONE);
		scrapFolder = new Button(shell, SWT.NONE);
		scrapFolder.setText("Start Scraping ...");
		scrapFolder
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						startScrap();
					}
				});
		scanFolderButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						startScan();
					}
				});
		Label filler11 = new Label(shell, SWT.NONE);
		moveToMediaFolderCheckBox = new Button(shell, SWT.CHECK);
		moveToMediaFolderCheckBox
				.setText("Move to Media foder once scraping is complete !");
		moveToMediaFolderCheckBox.setLayoutData(gridData17);
		browseFolderButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						browseForRootFolder();
					}
				});
		list = new List(shell, SWT.NONE);
		list.setLayoutData(gridData1);
		listViewer = new ListViewer(list);
		lookUpOneDDLButton = new Button(shell, SWT.NONE);
		lookUpOneDDLButton.setText("Look-up OneDDL");
		lookUpOneDDLButton.setLayoutData(gridData6);
		Label filler63 = new Label(shell, SWT.NONE);
		searchImdbButton = new Button(shell, SWT.NONE);
		searchImdbButton.setText("Search in iMDB");
		searchImdbButton.setLayoutData(gridData7);
		Label filler1 = new Label(shell, SWT.NONE);
		Label filler613 = new Label(shell, SWT.NONE);
		Label filler3 = new Label(shell, SWT.NONE);
		Label filler615 = new Label(shell, SWT.NONE);
		createContentComposite();
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setLayoutData(gridData22);
		/*
		 * progressBar.addPaintListener(new PaintListener() { public void
		 * paintControl(PaintEvent e) { System.out.println("PAINT"); // string
		 * to draw. String string = (progressBar.getSelection() * 1.0
		 * /(progressBar.getMaximum()-progressBar.getMinimum()) * 100) + "%";
		 * 
		 * Point point = progressBar.getSize(); Font font = new
		 * Font(shell.getDisplay(),"Courier",10,SWT.BOLD); e.gc.setFont(font);
		 * e.
		 * gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		 * 
		 * FontMetrics fontMetrics = e.gc.getFontMetrics(); int stringWidth =
		 * fontMetrics.getAverageCharWidth() * string.length(); int stringHeight
		 * = fontMetrics.getHeight();
		 * 
		 * e.gc.drawString(string, (point.x-stringWidth)/2 ,
		 * (point.y-stringHeight)/2, true); font.dispose(); } });
		 */
	}

	/**
	 * This method initializes contentComposite
	 * 
	 */
	private void createContentComposite() {
		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 7;
		gridData2.widthHint = 660;
		gridData2.horizontalAlignment = GridData.CENTER;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.heightHint = 372;
		contentComposite = new Composite(shell, SWT.NONE);
		contentComposite.setLayout(new FillLayout());
		createBrowser();
		contentComposite.setLayoutData(gridData2);
	}

	private void startScrap() {

		if (list.getItemCount() == 0) {
			MessageDialog.openInformation(shell, "No Media detected!",
					"There are no media files to scrap!");
			return;
		}

		scrapFolder.setEnabled(false);

		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		mediaFileContext.startScrap(new IScrapingStatusObserver() {
			
			int value = 1;
			
			public void setMinimum(Integer value) {
				progressBar.setMinimum(value);
			}

			
			public void setMaximum(Integer value) {
				progressBar.setMaximum(value);
			}

			
			public void setCurrentValue(final Integer value) {
				
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (progressBar.isDisposed()) {
							return;
						}
						progressBar.setSelection(value);
						// progressBar.redraw();
					}
				});
			}

			@Override
			public void notifyErrorOccurred(String message, Throwable throwable) {
				//
				MessageDialog.openInformation(shell, "Error during scraping!",
						"Error message: " + message);
				throwable.printStackTrace();
			}

			@Override
			public void notifyScrapingFinished() {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (list.isDisposed()) {
							return;
						}
						refreshList();
						
						if (scrapFolder.isDisposed()) {
							return;
						}
						scrapFolder.setEnabled(true);
					}
				});
				
			}

			@Override
			public void notifyStepProgress() {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (progressBar.isDisposed()) {
							return;
						}
						progressBar.setSelection(progressBar.getSelection() + 1);
						// progressBar.redraw();
					}
				});
				
			}


			@Override
			public void notifyNewStep() {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void notifyStepFinished() {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void notifyScrapingStarted() {
				// TODO Auto-generated method stub
				
			}

		});

	}

	
	
	private void startScan() {

		if (rootFolderText.getText().equals("")
				|| rootFolderText.getText().equals(MEDIA_FOLDER)) {
			MessageDialog.openInformation(shell, "Media folder not selected!",
					"Select a folder with your media before scan can start!");
			return;
		}

		// start scaning the folders

		File rootFolder = new File(rootFolderText.getText());

		if (!rootFolder.isDirectory()) {
			MessageDialog.openInformation(shell, "Media folder not selected!",
					"Select a folder with your media before scan can start!");
			return;
		}

		try {
			scrapFolder.setEnabled(false);
			scanFolderButton.setEnabled(false);
			browseFolderButton.setEnabled(false);
			processFolder(rootFolder);
		} finally {
			scanFolderButton.setEnabled(true);
			browseFolderButton.setEnabled(true);
			scrapFolder.setEnabled(true);
		}

	}
	
	private void refreshList(){
		list.removeAll();
		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		for(MediaFile mediaFileDefinition : mediaFileContext.getAll()){
			addFileToSideList(mediaFileDefinition);
		}
	}

	private void processFolder(File rootFolder) {
		MediaFileContext mediaFileContext = MediaFileContext.getInstance();
		for (File item : rootFolder.listFiles()) {
			if (item.isDirectory()) {
				processFolder(item);
			} else {
				// File
				MediaFile mediaFileDefinition = null;
				try {
					mediaFileDefinition = mediaFileContext.addFile(item);
				} catch (MediaFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mediaFileDefinition != null) {
					addFileToSideList(mediaFileDefinition);
				}
			}
		}
	}

	private void addFileToSideList(MediaFile mediaFileDefinition) {
		//
		if (mediaFileDefinition.getStatus().equals(MediaStatus.MEDIA_DETAILS_FOUND)) {
			list.add("[OK] " + mediaFileDefinition.getTitle());
		} 
		else if (mediaFileDefinition.getStatus().equals(MediaStatus.CANDIDATE_DETAILS_FOUND)) {
			list.add("[REVIEW] " + mediaFileDefinition.getTitle());
		}
		else if (mediaFileDefinition.getStatus().equals(MediaStatus.MEDIA_DETAILS_NOT_FOUND)) {
			list.add("[NOT FOUND] " + mediaFileDefinition.getTitle());
		}
		else if (mediaFileDefinition.getStatus().equals(MediaStatus.NO_MEDIA_DETAILS)) {
			list.add("[NEW] " + mediaFileDefinition.getTitle());
		}
	}

	private void browseForRootFolder() {

		DirectoryDialog dlg = new DirectoryDialog(shell);

		if (!rootFolderText.getText().equals("")
				|| !rootFolderText.getText().equals(MEDIA_FOLDER)) {

			// Set the initial filter path according
			// to anything they've selected or typed in
			dlg.setFilterPath(rootFolderText.getText());

		}

		// Change the title bar text
		dlg.setText("Select the folder to scan for Media");

		// Customizable message displayed in the dialog
		dlg.setMessage("Select a directory");

		// Calling open() will open and run the dialog.
		// It will return the selected directory, or
		// null if user cancels
		String dir = dlg.open();
		if (dir != null) {
			// Set the text box to the new selection
			rootFolderText.setText(dir);
		}

	}

	public static void main(String[] args) {
		/*
		 * Before this is run, be sure to set up the following in the launch
		 * configuration (Arguments->VM Arguments) for the correct SWT library
		 * path. The following is a windows example: -Djava.library.path=
		 * "installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
		 */
		org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display
				.getDefault();
		
		MainWindow thisClass = new MainWindow();
		thisClass.createSShell();
		thisClass.shell.open();

		while (!thisClass.shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes browser
	 * 
	 */
	private void createBrowser() {
		browser = new Browser(contentComposite, SWT.NONE);
	}

}
