package org.n52.sos.hackair.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceInfo {

    private String id;
    @JsonProperty("image_url")
    private String imageUrl;
    private String path;
    @JsonProperty("url_original")
    private String urlOriginal;
    @JsonProperty("thumb_file_path")
    private String thumbFilePath;
    @JsonProperty("thumb_image_url")
    private String thumbImageUrl;
    @JsonProperty("page_url")
    private String pageUrl;
    @JsonProperty("file_path")
    private String filePath;
    private User user;
    private String username;
    private Integer views;
    private String title;
    @JsonProperty("date_uploaded")
    private DateUploaded dateUploaded;
    private Device device;
    private Sensor sensor;
    private String countryCode;
    private String location;
    private String source;
    @JsonProperty("webcam_id")
    private String webcamId;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public boolean hasId() {
        return getId() != null && !getId().isEmpty();
    }

    @JsonProperty("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("image_url")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean hasImageUrl() {
        return getImageUrl() != null && !getImageUrl().isEmpty();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("url_original")
    public String getUrlOriginal() {
        return urlOriginal;
    }

    @JsonProperty("url_original")
    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }
    
    @JsonProperty("thumb_file_path")
    public String getThumbFilePath() {
        return thumbFilePath;
    }

    @JsonProperty("thumb_file_path")
    public void setThumbFilePath(String thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
    }

    @JsonProperty("thumb_image_url")
    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    @JsonProperty("thumb_image_url")
    public void setThumbImageUrl(String thumbImageUrl) {
        this.thumbImageUrl = thumbImageUrl;
    }
    
    @JsonProperty("page_url")
    public String getTPageUrl() {
        return pageUrl;
    }

    @JsonProperty("page_url")
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @JsonProperty("file_path")
    public String getFilePath() {
        return filePath;
    }

    @JsonProperty("file_path")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean hasUser() {
        return getUser() != null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean hasUsername() {
        return getUsername() != null && !getUsername().isEmpty();
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @JsonProperty("date_uploaded")
    public DateUploaded getDateUploaded() {
        return dateUploaded;
    }

    @JsonProperty("date_uploaded")
    public void setDateUploaded(DateUploaded dateUploaded) {
        this.dateUploaded = dateUploaded;
    }
    
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    
    public boolean hasDevice() {
        return getDevice() != null;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
    
    public boolean hasSensor() {
        return getSensor() != null;
    }
    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public boolean hasCountryCode() {
        return getCountryCode() != null && !getCountryCode().isEmpty();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean hasLocation() {
        return getLocation() != null && !getLocation().isEmpty();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean hasSource() {
        return getSource() != null && !getSource().isEmpty();
    }

    @JsonProperty("webcam_id")
    public String getWebcamId() {
        return webcamId;
    }

    @JsonProperty("webcam_id")
    public void setWebcamId(String webcamId) {
        this.webcamId = webcamId;
    }

    public boolean hasWebcamId() {
        return getWebcamId() != null && !getWebcamId().isEmpty();
    }

}
