package nz.co.iswe.mediamanager.scraper;

public class SearchResult {

	private String url;
	private MediaType mediaType;
	
	public SearchResult(String url, MediaType mediaType) {
		this.url = url;
		this.mediaType = mediaType;
	}

	public String getURL(){
		return url;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SearchResult){
			SearchResult other = (SearchResult)obj;
			return other.url.equals(this.url);
		}
		else{
			return false;
		}
	}

}
