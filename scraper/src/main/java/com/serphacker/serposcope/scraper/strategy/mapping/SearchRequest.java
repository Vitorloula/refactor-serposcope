package com.serphacker.serposcope.scraper.strategy.mapping;

import com.serphacker.serposcope.scraper.google.GoogleCountryCode;
import com.serphacker.serposcope.scraper.google.GoogleDevice;
import java.util.Objects;

/**
 * Data transfer object describing the parameters of a search query.
 */
public class SearchRequest {

    private final String keyword;
    private final String tld;
    private final GoogleCountryCode country;
    private final String datacenter;
    private final GoogleDevice device;
    private final String local;
    private final String customParameters;
    private final int pages;
    private final int resultsPerPage;
    private final long minPauseBetweenPageMs;
    private final long maxPauseBetweenPageMs;

    private SearchRequest(Builder builder) {
        this.keyword = builder.keyword;
        this.tld = builder.tld;
        this.country = builder.country;
        this.datacenter = builder.datacenter;
        this.device = builder.device;
        this.local = builder.local;
        this.customParameters = builder.customParameters;
        this.pages = builder.pages;
        this.resultsPerPage = builder.resultsPerPage;
        this.minPauseBetweenPageMs = builder.minPauseBetweenPageMs;
        this.maxPauseBetweenPageMs = builder.maxPauseBetweenPageMs;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getTld() {
        return tld;
    }

    public GoogleCountryCode getCountry() {
        return country;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public GoogleDevice getDevice() {
        return device;
    }

    public String getLocal() {
        return local;
    }

    public String getCustomParameters() {
        return customParameters;
    }

    public int getPages() {
        return pages;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public long getMinPauseBetweenPageMs() {
        return minPauseBetweenPageMs;
    }

    public long getMaxPauseBetweenPageMs() {
        return maxPauseBetweenPageMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, tld, country, datacenter, device, local, customParameters,
            pages, resultsPerPage, minPauseBetweenPageMs, maxPauseBetweenPageMs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SearchRequest other = (SearchRequest) obj;
        return pages == other.pages
            && resultsPerPage == other.resultsPerPage
            && minPauseBetweenPageMs == other.minPauseBetweenPageMs
            && maxPauseBetweenPageMs == other.maxPauseBetweenPageMs
            && Objects.equals(keyword, other.keyword)
            && Objects.equals(tld, other.tld)
            && country == other.country
            && Objects.equals(datacenter, other.datacenter)
            && device == other.device
            && Objects.equals(local, other.local)
            && Objects.equals(customParameters, other.customParameters);
    }

    @Override
    public String toString() {
        return "SearchRequest{"
            + "keyword='" + keyword + '\''
            + ", tld='" + tld + '\''
            + ", country=" + country
            + ", datacenter='" + datacenter + '\''
            + ", device=" + device
            + ", local='" + local + '\''
            + ", customParameters='" + customParameters + '\''
            + ", pages=" + pages
            + ", resultsPerPage=" + resultsPerPage
            + ", minPauseBetweenPageMs=" + minPauseBetweenPageMs
            + ", maxPauseBetweenPageMs=" + maxPauseBetweenPageMs
            + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String keyword;
        private String tld;
        private GoogleCountryCode country = GoogleCountryCode.__;
        private String datacenter;
        private GoogleDevice device = GoogleDevice.DESKTOP;
        private String local;
        private String customParameters;
        private int pages = 1;
        private int resultsPerPage = 10;
        private long minPauseBetweenPageMs;
        private long maxPauseBetweenPageMs;

        private Builder() {
        }

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder tld(String tld) {
            this.tld = tld;
            return this;
        }

        public Builder country(GoogleCountryCode country) {
            if (country != null) {
                this.country = country;
            }
            return this;
        }

        public Builder datacenter(String datacenter) {
            this.datacenter = datacenter;
            return this;
        }

        public Builder device(GoogleDevice device) {
            if (device != null) {
                this.device = device;
            }
            return this;
        }

        public Builder local(String local) {
            this.local = local;
            return this;
        }

        public Builder customParameters(String customParameters) {
            this.customParameters = customParameters;
            return this;
        }

        public Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        public Builder resultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
            return this;
        }

        public Builder pause(long minMs, long maxMs) {
            this.minPauseBetweenPageMs = Math.max(0, minMs);
            this.maxPauseBetweenPageMs = Math.max(0, maxMs);
            return this;
        }

        public SearchRequest build() {
            return new SearchRequest(this);
        }
    }
}
