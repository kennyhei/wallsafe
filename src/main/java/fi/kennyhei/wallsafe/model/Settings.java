package fi.kennyhei.wallsafe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class Settings {

    // User preferences stored in registry
    private final Preferences preferences;

    // Settings is a singleton class
    private static Settings instance = null;

    // Resolution of the wallpaper, defaults to user's native resolution
    private String resolution;

    // Tags for searching wallpapers
    private List<String> keywords;

    // Download directory
    private String directoryPath;

    // Interval settings
    private int changeIntervalValue;
    private String changeIntervalTimeunit;

    private int downloadIntervalValue;
    private String downloadIntervalTimeunit;

    // Index of current wallpaper
    private int indexOfCurrentWallpaper;

    // Base URL where wallpapers are downloaded from
    private final String baseURL = "http://alpha.wallhaven.cc/search";
    private String URL;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    protected Settings() {

        // Initialize preferences
        this.preferences = Preferences.userRoot().node(this.getClass().getName());

        // Load user preferences
        this.loadPreferences();

        // Build URL where wallpapers are downloaded from
        this.buildURL();
    }

    public String getResolution() {

        return resolution;
    }

    public void setResolution(String resolution) {

        this.resolution = resolution;

        this.updatePreference("resolution", this.resolution);
        this.buildURL();
    }

    public List<String> getKeywords() {

        return keywords;
    }

    public void setKeywords(List<String> keywords) {

        this.keywords = keywords;
    }

    public String getDirectoryPath() {

        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {

        this.directoryPath = directoryPath;
        this.updatePreference("download.directory", this.directoryPath);
    }

    public int getChangeIntervalValue() {

        return changeIntervalValue;
    }

    public void setChangeIntervalValue(int changeIntervalValue) {

        this.changeIntervalValue = changeIntervalValue;
        this.updatePreference("change.interval.value", Integer.toString(this.changeIntervalValue));
    }

    public String getChangeIntervalTimeunit() {

        return changeIntervalTimeunit;
    }

    public void setChangeIntervalTimeunit(String changeIntervalTimeunit) {

        this.changeIntervalTimeunit = changeIntervalTimeunit;
        this.updatePreference("change.interval.timeunit", this.changeIntervalTimeunit);
    }

    public int getDownloadIntervalValue() {

        return downloadIntervalValue;
    }

    public void setDownloadIntervalValue(int downloadIntervalValue) {

        this.downloadIntervalValue = downloadIntervalValue;
        this.updatePreference("download.interval.value", Integer.toString(this.downloadIntervalValue));
    }

    public String getDownloadIntervalTimeunit() {

        return downloadIntervalTimeunit;
    }

    public void setDownloadIntervalTimeunit(String downloadIntervalTimeunit) {

        this.downloadIntervalTimeunit = downloadIntervalTimeunit;
        this.updatePreference("download.interval.timeunit", this.downloadIntervalTimeunit);
    }

    public int getIndexOfCurrentWallpaper() {

        return indexOfCurrentWallpaper;
    }

    public void setIndexOfCurrentWallpaper(int indexOfCurrentWallpaper) {

        this.indexOfCurrentWallpaper = indexOfCurrentWallpaper;
        this.updatePreference("current.wallpaper.index", Integer.toString(this.indexOfCurrentWallpaper));
    }

    public String getURL() {

        return URL;
    }

    public void buildURL(String keyword) {

        StringBuilder sb = new StringBuilder(baseURL);

        sb.append("?")
          .append("q=")
          .append(keyword)
          .append("&")
          .append("categories=101&")
          .append("purity=101&")
          .append("resolutions=")
          .append(resolution)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.URL = sb.toString();
    }

    public void buildURL() {

        StringBuilder sb = new StringBuilder(baseURL);

        sb.append("?")
          .append("categories=101&")
          .append("purity=101&")
          .append("resolutions=")
          .append(resolution)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.URL = sb.toString();
    }

    private void loadPreferences() {

        this.changeIntervalValue = Integer.parseInt(preferences.get("change.interval.value", "60"));
        this.changeIntervalTimeunit = preferences.get("change.interval.timeunit", "seconds");

        this.downloadIntervalValue = Integer.parseInt(preferences.get("download.interval.value", "60"));
        this.downloadIntervalTimeunit = preferences.get("download.interval.timeunit", "seconds");

        this.resolution = preferences.get("resolution", "1920x1080");

        this.directoryPath = preferences.get("download.directory", System.getProperty("user.home") + "\\Desktop\\Wallpapers");

        this.indexOfCurrentWallpaper = Integer.parseInt(preferences.get("current.wallpaper.index", "0"));

        String[] prefKeywords = preferences.get("keywords", "space,nature,abstract").split(",");
        this.keywords = new ArrayList<>(Arrays.asList(prefKeywords));
    }

    private void updatePreference(String key, String value) {

        this.preferences.put(key, value);
    }
}
