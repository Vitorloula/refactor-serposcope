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
 * Testes de integração para ScrapClient usando Builder Pattern
 */
public class ScrapClientBuilderTest {

    @Test
    public void testConstrutorPadraoFunciona() {
        try (ScrapClient client = new ScrapClient()) {
            assertNotNull(client);
            assertEquals(ScrapClient.DEFAULT_USER_AGENT, client.getUseragent());
            assertEquals((Integer) ScrapClient.DEFAULT_TIMEOUT_MS, client.getTimeout());
        } catch (Exception e) {
            fail("Construtor padrão não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConstrutorComConfigPadrao() {
        ScrapClientConfig config = ScrapClientConfig.builder().build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            assertEquals(ScrapClient.DEFAULT_USER_AGENT, client.getUseragent());
            assertEquals((Integer) ScrapClient.DEFAULT_TIMEOUT_MS, client.getTimeout());
        } catch (Exception e) {
            fail("Construtor com config padrão não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConstrutorComConfigCustomizado() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Custom User Agent")
                .timeout(10000)
                .maxResponseLength(2 * 1024 * 1024)
                .insecureSSL(true)
                .build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            assertEquals("Custom User Agent", client.getUseragent());
            assertEquals((Integer) 10000, client.getTimeout());
            // Note: setMaxResponseLength adiciona +1 internamente, então 2097152 vira
            // 2097153
            assertEquals(2 * 1024 * 1024 + 1, client.getMaxResponseLength());
            assertTrue(client.isInsecureSSL());
        } catch (Exception e) {
            fail("Construtor com config customizado não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConstrutorComProxy() {
        HttpProxy proxy = new HttpProxy("192.168.1.100", 8080);

        ScrapClientConfig config = ScrapClientConfig.builder()
                .proxy(proxy)
                .build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            assertNotNull(client.getProxy());
            assertEquals(proxy, client.getProxy());
        } catch (Exception e) {
            fail("Construtor com proxy não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConstrutorComHeaders() {
        BasicHeader header1 = new BasicHeader("X-Custom-Header", "Value1");
        BasicHeader header2 = new BasicHeader("X-Another-Header", "Value2");

        ScrapClientConfig config = ScrapClientConfig.builder()
                .addRequestHeader(header1)
                .addRequestHeader(header2)
                .build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            // Headers são aplicados internamente nas requisições
        } catch (Exception e) {
            fail("Construtor com headers não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConstrutorComRedirects() {
        ScrapClientConfig config = ScrapClientConfig.builder()
                .followRedirects()
                .build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            assertEquals(10, client.getMaxRedirect());
        } catch (Exception e) {
            fail("Construtor com redirects não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testConfigCompleta() {
        HttpProxy proxy = new HttpProxy("10.0.0.1", 3128, "user", "pass");

        ScrapClientConfig config = ScrapClientConfig.builder()
                .userAgent("Mozilla/5.0 (Test)")
                .timeout(15000)
                .maxResponseLength(10 * 1024 * 1024)
                .proxy(proxy)
                .insecureSSL(true)
                .followRedirects()
                .addRequestHeader(new BasicHeader("Accept", "application/json"))
                .build();

        try (ScrapClient client = new ScrapClient(config)) {
            assertNotNull(client);
            assertEquals("Mozilla/5.0 (Test)", client.getUseragent());
            assertEquals((Integer) 15000, client.getTimeout());
            // Note: setMaxResponseLength adiciona +1 internamente
            assertEquals(10 * 1024 * 1024 + 1, client.getMaxResponseLength());
            assertEquals(proxy, client.getProxy());
            assertTrue(client.isInsecureSSL());
            assertEquals(10, client.getMaxRedirect());
        } catch (Exception e) {
            fail("Config completa não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void testMultiplasInstanciasComDiferentesConfigs() {
        // Testa que múltiplas instâncias podem ter configs diferentes
        ScrapClientConfig config1 = ScrapClientConfig.builder()
                .userAgent("Agent 1")
                .timeout(5000)
                .build();

        ScrapClientConfig config2 = ScrapClientConfig.builder()
                .userAgent("Agent 2")
                .timeout(10000)
                .insecureSSL(true)
                .build();

        try (ScrapClient client1 = new ScrapClient(config1);
                ScrapClient client2 = new ScrapClient(config2)) {

            assertEquals("Agent 1", client1.getUseragent());
            assertEquals((Integer) 5000, client1.getTimeout());
            assertFalse(client1.isInsecureSSL());

            assertEquals("Agent 2", client2.getUseragent());
            assertEquals((Integer) 10000, client2.getTimeout());
            assertTrue(client2.isInsecureSSL());

        } catch (Exception e) {
            fail("Múltiplas instâncias não deveriam lançar exceção: " + e.getMessage());
        }
    }
}
