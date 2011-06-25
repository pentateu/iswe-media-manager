package nz.co.iswe.mediamanager.media;

public enum MediaStatus {
	NO_MEDIA_DETAILS,
	
	/**
	 * Indicate that a list of candidate URLs with details information for
	 * this media has being found, but has bot being pre-loaded.
	 */
	CANDIDATE_LIST_FOUND,
	
	/**
	 * Indicate that a list of candidate URLs with details information for
	 * this media has being found AND the details has being pre-loaded (scraped)
	 * and the user can see the details and confirm off-line.
	 */
	CANDIDATE_DETAILS_FOUND,
	
	MEDIA_DETAILS_FOUND,
	
	MEDIA_DETAILS_NOT_FOUND,
	
	ERROR
}
