package com.example.mvince.gridimagesearch.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mvince on 2/1/15.
 */
public class SearchSettings implements Serializable{
    public int size = 0;
    public int color = 0;
    public int type = 0;
    public String site = "";

    private final static String[] SIZES = {"icon", "small", "medium", "large", "xlarge", "xxlarge", "huge"};
    private final static String[] COLORS = {"black", "blue", "brown", "gray", "green", "orange", "pink", "purple", "red", "white", "yellow"};
    private final static String[] TYPES = {"face", "photo", "clipart", "lineart"};

    public SearchSettings() {
    }

    public String getUrlParams() {
        String params = "";

        if (size > 0) {
            params += "&imgsz=" + SIZES[size - 1];
        }

        if (color > 0) {
            params += "&imgcolor=" + COLORS[color - 1];
        }

        if (type > 0) {
            params += "&imgtype=" + TYPES[type - 1];
        }

        if (site.length() > 0) {
            params += "&as_sitesearch=" + site;
        }

        return params;
    }
}
