package nz.co.iswe.mediamanager.ui.swing;

import nz.co.iswe.mediamanager.media.file.MediaDetail;

public interface MediaDetailPanelListener {

	void notifyCandidateConfirmed();

	void showBrowser(MediaDetail mediaDetail);

}
