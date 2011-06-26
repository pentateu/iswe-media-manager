//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.20 at 05:47:56 PM NZST 
//


package nz.co.iswe.mediamanager.media.nfo.xml.movie;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}title"/>
 *         &lt;element ref="{}originaltitle"/>
 *         &lt;element ref="{}sorttitle"/>
 *         &lt;element ref="{}set"/>
 *         &lt;element ref="{}rating"/>
 *         &lt;element ref="{}year"/>
 *         &lt;element ref="{}top250"/>
 *         &lt;element ref="{}votes"/>
 *         &lt;element ref="{}outline"/>
 *         &lt;element ref="{}plot"/>
 *         &lt;element ref="{}tagline"/>
 *         &lt;element ref="{}runtime"/>
 *         &lt;element ref="{}thumb"/>
 *         &lt;element ref="{}mpaa"/>
 *         &lt;element ref="{}playcount"/>
 *         &lt;element ref="{}watched"/>
 *         &lt;element ref="{}id"/>
 *         &lt;element ref="{}filenameandpath"/>
 *         &lt;element ref="{}trailer"/>
 *         &lt;element ref="{}genre"/>
 *         &lt;element ref="{}credits"/>
 *         &lt;element ref="{}fileinfo"/>
 *         &lt;element ref="{}director"/>
 *         &lt;element ref="{}actor" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "originaltitle",
    "sorttitle",
    "set",
    "rating",
    "year",
    "top250",
    "votes",
    "outline",
    "plot",
    "tagline",
    "runtime",
    "thumb",
    "mpaa",
    "playcount",
    "watched",
    "id",
    "filenameandpath",
    "trailer",
    "genre",
    "credits",
    "fileinfo",
    "director",
    "actor"
})
@XmlRootElement(name = "movie")
public class Movie {

    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String originaltitle;
    @XmlElement(required = true)
    protected String sorttitle;
    @XmlElement(required = true)
    protected String set;
    @XmlElement(required = true)
    protected String rating;
    @XmlElement(required = true)
    protected BigInteger year;
    @XmlElement(required = true)
    protected BigInteger top250;
    @XmlElement(required = true)
    protected BigInteger votes;
    @XmlElement(required = true)
    protected String outline;
    @XmlElement(required = true)
    protected String plot;
    @XmlElement(required = true)
    protected Tagline tagline;
    @XmlElement(required = true)
    protected String runtime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String thumb;
    @XmlElement(required = true)
    protected String mpaa;
    @XmlElement(required = true)
    protected BigInteger playcount;
    protected boolean watched;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String id;
    @XmlElement(required = true)
    protected String filenameandpath;
    @XmlElement(required = true)
    protected Trailer trailer;
    @XmlElement(required = true)
    protected Genre genre;
    @XmlElement(required = true)
    protected Credits credits;
    @XmlElement(required = true)
    protected Fileinfo fileinfo;
    @XmlElement(required = true)
    protected String director;
    @XmlElement(required = true)
    protected List<Actor> actor;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the originaltitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginaltitle() {
        return originaltitle;
    }

    /**
     * Sets the value of the originaltitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginaltitle(String value) {
        this.originaltitle = value;
    }

    /**
     * Gets the value of the sorttitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSorttitle() {
        return sorttitle;
    }

    /**
     * Sets the value of the sorttitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSorttitle(String value) {
        this.sorttitle = value;
    }

    /**
     * Gets the value of the set property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSet() {
        return set;
    }

    /**
     * Sets the value of the set property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSet(String value) {
        this.set = value;
    }

    /**
     * Gets the value of the rating property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRating(String value) {
        this.rating = value;
    }

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setYear(BigInteger value) {
        this.year = value;
    }

    /**
     * Gets the value of the top250 property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTop250() {
        return top250;
    }

    /**
     * Sets the value of the top250 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTop250(BigInteger value) {
        this.top250 = value;
    }

    /**
     * Gets the value of the votes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVotes() {
        return votes;
    }

    /**
     * Sets the value of the votes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVotes(BigInteger value) {
        this.votes = value;
    }

    /**
     * Gets the value of the outline property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutline() {
        return outline;
    }

    /**
     * Sets the value of the outline property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutline(String value) {
        this.outline = value;
    }

    /**
     * Gets the value of the plot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Sets the value of the plot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlot(String value) {
        this.plot = value;
    }

    /**
     * Gets the value of the tagline property.
     * 
     * @return
     *     possible object is
     *     {@link Tagline }
     *     
     */
    public Tagline getTagline() {
        return tagline;
    }

    /**
     * Sets the value of the tagline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tagline }
     *     
     */
    public void setTagline(Tagline value) {
        this.tagline = value;
    }

    /**
     * Gets the value of the runtime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Sets the value of the runtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuntime(String value) {
        this.runtime = value;
    }

    /**
     * Gets the value of the thumb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThumb() {
        return thumb;
    }

    /**
     * Sets the value of the thumb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThumb(String value) {
        this.thumb = value;
    }

    /**
     * Gets the value of the mpaa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMpaa() {
        return mpaa;
    }

    /**
     * Sets the value of the mpaa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMpaa(String value) {
        this.mpaa = value;
    }

    /**
     * Gets the value of the playcount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPlaycount() {
        return playcount;
    }

    /**
     * Sets the value of the playcount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPlaycount(BigInteger value) {
        this.playcount = value;
    }

    /**
     * Gets the value of the watched property.
     * 
     */
    public boolean isWatched() {
        return watched;
    }

    /**
     * Sets the value of the watched property.
     * 
     */
    public void setWatched(boolean value) {
        this.watched = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the filenameandpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilenameandpath() {
        return filenameandpath;
    }

    /**
     * Sets the value of the filenameandpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilenameandpath(String value) {
        this.filenameandpath = value;
    }

    /**
     * Gets the value of the trailer property.
     * 
     * @return
     *     possible object is
     *     {@link Trailer }
     *     
     */
    public Trailer getTrailer() {
        return trailer;
    }

    /**
     * Sets the value of the trailer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trailer }
     *     
     */
    public void setTrailer(Trailer value) {
        this.trailer = value;
    }

    /**
     * Gets the value of the genre property.
     * 
     * @return
     *     possible object is
     *     {@link Genre }
     *     
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * Sets the value of the genre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Genre }
     *     
     */
    public void setGenre(Genre value) {
        this.genre = value;
    }

    /**
     * Gets the value of the credits property.
     * 
     * @return
     *     possible object is
     *     {@link Credits }
     *     
     */
    public Credits getCredits() {
        return credits;
    }

    /**
     * Sets the value of the credits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credits }
     *     
     */
    public void setCredits(Credits value) {
        this.credits = value;
    }

    /**
     * Gets the value of the fileinfo property.
     * 
     * @return
     *     possible object is
     *     {@link Fileinfo }
     *     
     */
    public Fileinfo getFileinfo() {
        return fileinfo;
    }

    /**
     * Sets the value of the fileinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fileinfo }
     *     
     */
    public void setFileinfo(Fileinfo value) {
        this.fileinfo = value;
    }

    /**
     * Gets the value of the director property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets the value of the director property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirector(String value) {
        this.director = value;
    }

    /**
     * Gets the value of the actor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Actor }
     * 
     * 
     */
    public List<Actor> getActor() {
        if (actor == null) {
            actor = new ArrayList<Actor>();
        }
        return this.actor;
    }

}