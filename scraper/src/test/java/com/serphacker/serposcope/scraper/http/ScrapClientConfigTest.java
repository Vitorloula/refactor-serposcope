/* 
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 * 
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.HttpProxy;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes para ScrapClientConfig e seu Builder Pattern
 */
public class ScrapClientConfigTest {

    @Test
    public void testBuilderComValoresPadrao() {
        ScrapClientConfig config = ScrapClientConfig.builder().build();

        assertEquals(ScrapClient.DEFAULT_USER_AGENT, config.getUserAgent());
        assertEquals((Integer) ScrapClient.DEFAULT_TIMEOUT_MS, config.getTimeoutMS());
        assertEquals(ScrapClient.DEFAULT_MAX_RESPONSE_LENGTH, config.getMaxResponseLength());
        assertNull(config.getProxy());
        assertFalse(config.isInsecureSSL());
        assertEquals(0, config.getMaxRedirect());
        assertTrue(config.getRequestHeaders().isEmpty());
    }

    @Test
    public void testBuilderComValoresCustomizados() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Custom Agent")
                .timeout(15000)
                .maxResponseLength(2048)
                .insecureSSL(true)
                .maxRedirect(5)
                .build();

        assertEquals("Custom Agent", config.getUserAgent());
        assertEquals((Integer) 15000, config.getTimeoutMS());
        assertEquals(2048, config.getMaxResponseLength());
        assertTrue(config.isInsecureSSL());
        assertEquals(5, config.getMaxRedirect());
    }

    @Test
    public void testBuilderComProxy() {
        HttpProxy proxy = new HttpProxy("192.168.1.100", 8080);

        ScrapClientConfig config = ScrapClientConfig.builder()
                .proxy(proxy)
                .build();

        assertNotNull(config.getProxy());
        assertEquals(proxy, config.getProxy());
    }

    @Test
    public void testBuilderComProxyAutenticado() {
        HttpProxy proxy = new HttpProxy("192.168.1.100", 8080, "usuario", "senha");

        ScrapClientConfig config = ScrapClientConfig.builder()
                .proxy(proxy)
                .insecureSSL(true)
                .build();

        assertNotNull(config.getProxy());
        assertEquals(proxy, config.getProxy());
        assertTrue(config.isInsecureSSL());
    }

    @Test
    public void testFollowRedirects() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .followRedirects()
                .build();

        assertEquals(10, config.getMaxRedirect());
    }

    @Test
    public void testNoRedirects() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .followRedirects() // primeiro habilita
                .noRedirects() // depois desabilita
                .build();

        assertEquals(0, config.getMaxRedirect());
    }

    @Test
    public void testAddRequestHeader() {
        BasicHeader header1 = new BasicHeader("X-Custom", "Value1");
        BasicHeader header2 = new BasicHeader("X-Another", "Value2");

        ScrapClientConfig config = ScrapClientConfig.builder()
                .addRequestHeader(header1)
                .addRequestHeader(header2)
                .build();

        assertEquals(2, config.getRequestHeaders().size());
        assertTrue(config.getRequestHeaders().contains(header1));
        assertTrue(config.getRequestHeaders().contains(header2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimeoutNegativo() {
        ScrapClientConfig.builder()
                .timeout(-100)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxResponseLengthMuitoPequeno() {
        ScrapClientConfig.builder()
                .maxResponseLength(100) // menor que 1024
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxRedirectNegativo() {
        ScrapClientConfig.builder()
                .maxRedirect(-5)
                .build();
    }

    @Test
    public void testImutabilidade() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .addRequestHeader(new BasicHeader("X-Test", "Value"))
                .build();

        // Lista de headers deve ser imutável
        try {
            config.getRequestHeaders().add(new BasicHeader("X-Fail", "Should Fail"));
            fail("Deveria lançar UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Esperado - lista é imutável
        }
    }

    @Test
    public void testEncadeamentoFluente() {
        // Testa se todos os métodos retornam o builder para encadeamento
        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Test")
                .timeout(5000)
                .maxResponseLength(2048)
                .insecureSSL(false)
                .maxRedirect(3)
                .followRedirects()
                .addRequestHeader(new BasicHeader("X-Test", "Value"))
                .build();

        assertNotNull(config);
        assertEquals("Test", config.getUserAgent());
        assertEquals(10, config.getMaxRedirect()); // followRedirects() define como 10
    }

    @Test
    public void testTimeoutNulo() {
        // Timeout null deve ser aceito (usa padrão do HTTP client)
        ScrapClientConfig config = ScrapClientConfig.builder()
                .timeout(null)
                .build();

        assertNull(config.getTimeoutMS());
    }

    @Test
    public void testConfigCompleta() {
        HttpProxy proxy = new HttpProxy("10.0.0.1", 3128, "user", "pass");

        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Mozilla/5.0 (Custom)")
                .timeout(8000)
                .maxResponseLength(5 * 1024 * 1024) // 5MB
                .proxy(proxy)
                .insecureSSL(true)
                .followRedirects()
                .addRequestHeader(new BasicHeader("Accept", "text/html"))
                .addRequestHeader(new BasicHeader("Accept-Language", "pt-BR"))
                .build();

        assertEquals("Mozilla/5.0 (Custom)", config.getUserAgent());
        assertEquals((Integer) 8000, config.getTimeoutMS());
        assertEquals(5 * 1024 * 1024, config.getMaxResponseLength());
        assertEquals(proxy, config.getProxy());
        assertTrue(config.isInsecureSSL());
        assertEquals(10, config.getMaxRedirect());
        assertEquals(2, config.getRequestHeaders().size());
    }

    @Test
    public void testToString() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Test Agent")
                .timeout(5000)
                .build();

        String str = config.toString();

        assertNotNull(str);
        assertTrue(str.contains("Test Agent"));
        assertTrue(str.contains("5000"));
    }
}
