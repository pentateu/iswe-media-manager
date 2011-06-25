package nz.co.iswe.mediamanager.scraper;

public enum MediaType {

	MOVIE,
	TV_SHOW,
	DOCUMENTARY;
	
	public String asString(){
		if(this.equals(MOVIE) ){
			return "Movie";
		}
		if(this.equals(TV_SHOW) ){
			return "TV Show";
		}
		if(this.equals(DOCUMENTARY) ){
			return "Documentary";
		}
		return "Unknown";
	}
}
