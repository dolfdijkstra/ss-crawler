package com.fatwire.dta.sscrawler.har;

public class HarPage {
    private final long startedDateTime;
    private final String id;// ", "page_0");
    private String title = "";// ", "Test Page");
    private int onContentLoad = -1;// ", -1);
    private int onLoad = -1;// ", -1);
    private String comment = "";

    public HarPage(String id, long startedDateTime) {
        super();
        this.id = id;
        this.startedDateTime = startedDateTime;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the onContentLoad
     */
    public int getOnContentLoad() {
        return onContentLoad;
    }

    /**
     * @param onContentLoad the onContentLoad to set
     */
    public void setOnContentLoad(int onContentLoad) {
        this.onContentLoad = onContentLoad;
    }

    /**
     * @return the onLoad
     */
    public int getOnLoad() {
        return onLoad;
    }

    /**
     * @param onLoad the onLoad to set
     */
    public void setOnLoad(int onLoad) {
        this.onLoad = onLoad;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the startedDateTime
     */
    public long getStartedDateTime() {
        return startedDateTime;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

}
