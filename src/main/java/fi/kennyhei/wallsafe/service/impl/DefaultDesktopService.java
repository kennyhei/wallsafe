package fi.kennyhei.wallsafe.service.impl;

import com.sun.jna.platform.win32.WinDef;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDesktopService;
import fi.kennyhei.wallsafe.util.SPI;
import fi.kennyhei.wallsafe.service.DesktopService;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DefaultDesktopService extends AbstractBackgroundService implements DesktopService {

    private String currentFilePath;
    private String currentKeyword;

    public DefaultDesktopService() {

        super(new ScheduledDesktopService(), new DefaultSettingsService());
    }

    @Override
    public void changeWallpaper(String path) {

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    @Override
    public void changeToNext() {

        String keyword = this.settingsService.getRandomKeyword();
        int index = this.settingsService.getIndexOfKeyword(keyword);

        this.changeToIndex(index + 1, keyword);
    }

    @Override
    public void changeToPrevious() {

        String keyword = this.settingsService.getRandomKeyword();
        int index = this.settingsService.getIndexOfKeyword(keyword);

        this.changeToIndex(index - 1, keyword);
    }

    private void changeToIndex(int index, String keyword) {

        String path = this.settingsService.getDirectoryPath();
        path += "\\" + keyword;

        System.out.println("Selecting wallpaper from: " + path);

        File directory = new File(path);

        if (!directory.exists()) {
            return;
        }

        FileFilter fileFilter = new WildcardFileFilter("wallhaven-*");
        File[] wallpapers = directory.listFiles(fileFilter);

        if (wallpapers.length == 0) {
            return;
        }

        if (index >= wallpapers.length) {
            index = 0;
        }

        if (index < 0) {
            index = wallpapers.length - 1;
        }

        Arrays.sort(wallpapers, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
        path += "\\" + wallpapers[index].getName();

        this.currentFilePath = path;
        this.currentKeyword = keyword;

        this.changeWallpaper(path);

        this.settingsService.setIndexOfKeyword(keyword, index);
    }

    @Override
    public void resetIndex() {

        Map<String, Integer> keywords = this.settingsService.getKeywords();

        for (String key : keywords.keySet()) {
            this.settingsService.setIndexOfKeyword(key, -1);
        }
    }

    @Override
    public void deleteWallpaper() {

        if (this.currentFilePath == null) {
            // This is cheating
            this.changeToNext();
        }

        File file = new File(this.currentFilePath);
        file.delete();

        int index = this.settingsService.getIndexOfKeyword(this.currentKeyword);

        if (index > 0) {
            this.settingsService.setIndexOfKeyword(this.currentKeyword, index - 1);
        }

        this.changeToNext();
    }

    @Override
    public void start() {

        this.scheduledService = new ScheduledDesktopService();

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
        this.setIsRunning(true);
    }

    @Override
    public void updateInterval() {

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
    }
}
