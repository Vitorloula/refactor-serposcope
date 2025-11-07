package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.BindProxy;
import com.serphacker.serposcope.scraper.http.proxy.HttpProxy;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import com.serphacker.serposcope.scraper.http.proxy.SocksProxy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.protocol.HttpContext;

class ScrapClientRoutePlanner implements HttpRoutePlanner {

    private final Map<HttpHost, HttpHost> routes;
    private final Supplier<ScrapProxy> proxySupplier;

    ScrapClientRoutePlanner(Map<HttpHost, HttpHost> routes, Supplier<ScrapProxy> proxySupplier) {
        this.routes = routes;
        this.proxySupplier = proxySupplier;
    }

    @Override
    public HttpRoute determineRoute(HttpHost originalTarget, HttpRequest request, HttpContext context)
            throws HttpException {
        boolean ssl = "https".equalsIgnoreCase(originalTarget.getSchemeName());
        HttpHost target = routes.getOrDefault(originalTarget, originalTarget);
        ScrapProxy proxy = proxySupplier.get();

        if (proxy == null) {
            return new HttpRoute(target);
        }

        if (proxy instanceof SocksProxy) {
            SocksProxy socksProxy = (SocksProxy) proxy;
            context.setAttribute("proxy.socks", new InetSocketAddress(socksProxy.getIp(), socksProxy.getPort()));
            return new HttpRoute(target);
        }

        if (proxy instanceof BindProxy) {
            BindProxy bindProxy = (BindProxy) proxy;
            try {
                return new HttpRoute(target, InetAddress.getByName(bindProxy.ip), ssl);
            } catch (UnknownHostException cause) {
                throw new HttpException("invalid bind ip", cause);
            }
        }

        if (proxy instanceof HttpProxy) {
            HttpProxy httpProxy = (HttpProxy) proxy;

            return new HttpRoute(
                    target,
                    null,
                    new HttpHost(httpProxy.getIp(), httpProxy.getPort()),
                    ssl,
                    ssl ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN,
                    ssl ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN);
        }

        throw new UnsupportedOperationException("unsupported proxy type : " + proxy);
    }
}

