package com.serphacker.serposcope.scraper.strategy.mapping;

import java.util.Objects;

/**
 * Represents a single SERP entry.
 */
public class ResultEntry {

    private final int position;
    private final String url;
    private final String title;

    public ResultEntry(int position, String url, String title) {
        this.position = position;
        this.url = url;
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, url, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ResultEntry other = (ResultEntry) obj;
        return position == other.position
            && Objects.equals(url, other.url)
            && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        return "ResultEntry{"
            + "position=" + position
            + ", url='" + url + '\''
            + ", title='" + title + '\''
            + '}';
    }
}
