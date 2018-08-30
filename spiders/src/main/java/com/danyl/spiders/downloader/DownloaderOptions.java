package com.danyl.spiders.downloader;

import lombok.Data;

@Data
public class DownloaderOptions {
    private Boolean useProxy = true;
    // L1 包括所有，既没有匿名性要求，L2包括本身和更高级别的匿名性代理
    private String anonymity = "L2";

    public DownloaderOptions() {
    }

    public DownloaderOptions(Boolean useProxy, String anonymity) {
        this.useProxy = useProxy;
        this.anonymity = anonymity;
    }
}