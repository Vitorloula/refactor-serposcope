package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.BindProxy;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import java.util.function.Supplier;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.HttpContext;

class ScrapClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy {

    private final Supplier<Boolean> proxyChangedSinceLastRequest;
    private final Supplier<ScrapProxy> proxySupplier;

    ScrapClientConnectionReuseStrategy(Supplier<Boolean> proxyChangedSinceLastRequest,
            Supplier<ScrapProxy> proxySupplier) {
        this.proxyChangedSinceLastRequest = proxyChangedSinceLastRequest;
        this.proxySupplier = proxySupplier;
    }

    @Override
    public boolean keepAlive(HttpResponse response, HttpContext context) {
        ScrapProxy proxy = proxySupplier.get();
        if (!proxyChangedSinceLastRequest.get() && (proxy == null || (proxy instanceof BindProxy))) {
            return super.keepAlive(response, context);
        }
        return false;
    }
}

