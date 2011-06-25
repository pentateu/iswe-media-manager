package nz.co.iswe.mediamanager;

import nz.co.iswe.mediamanager.media.file.CandidateMediaDetailTest;
import nz.co.iswe.mediamanager.media.file.CandidateMediaDetailTest_02;
import nz.co.iswe.mediamanager.media.file.MediaDetailTest;
import nz.co.iswe.mediamanager.media.subtitles.SubtitleContextTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	UtilTest.class,
	MediaDetailTest.class,
	CandidateMediaDetailTest.class,
	CandidateMediaDetailTest_02.class,
	SubtitleContextTest.class
})
public class AllTestSuite {

}
