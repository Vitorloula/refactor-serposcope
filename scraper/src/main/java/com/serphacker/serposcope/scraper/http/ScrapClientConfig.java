/* 
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 * 
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import org.apache.http.Header;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuração imutável para ScrapClient usando Builder Pattern.
 * Esta classe encapsula todas as configurações do cliente HTTP.
 * 
 * Exemplo de uso:
 * 
 * <pre>
 * ScrapClientConfig config = ScrapClientConfig.builder()
 *         .userAgent("Mozilla/5.0...")
 *         .timeout(5000)
 *         .proxy(myProxy)
 *         .build();
 * 
 * ScrapClient client = new ScrapClient(config);
 * </pre>
 */
public class ScrapClientConfig {

    private final String userAgent;
    private final Integer timeoutMS;
    private final int maxResponseLength;
    private final ScrapProxy proxy;
    private final boolean insecureSSL;
    private final int maxRedirect;
    private final List<Header> requestHeaders;

    /**
     * Construtor privado - apenas o Builder pode criar instâncias
     */
    private ScrapClientConfig(Builder builder) {
        this.userAgent = builder.userAgent;
        this.timeoutMS = builder.timeoutMS;
        this.maxResponseLength = builder.maxResponseLength;
        this.proxy = builder.proxy;
        this.insecureSSL = builder.insecureSSL;
        this.maxRedirect = builder.maxRedirect;
        this.requestHeaders = Collections.unmodifiableList(new ArrayList<>(builder.requestHeaders));
    }

    // Getters

    public String getUserAgent() {
        return userAgent;
    }

    public Integer getTimeoutMS() {
        return timeoutMS;
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public ScrapProxy getProxy() {
        return proxy;
    }

    public boolean isInsecureSSL() {
        return insecureSSL;
    }

    public int getMaxRedirect() {
        return maxRedirect;
    }

    public List<Header> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Cria um novo Builder para construir ScrapClientConfig
     * 
     * @return novo Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder para ScrapClientConfig - permite construção fluente e imutável
     */
    public static class Builder {
        // Valores padrão
        private String userAgent = ScrapClient.DEFAULT_USER_AGENT;
        private Integer timeoutMS = ScrapClient.DEFAULT_TIMEOUT_MS;
        private int maxResponseLength = ScrapClient.DEFAULT_MAX_RESPONSE_LENGTH;
        private ScrapProxy proxy = null;
        private boolean insecureSSL = false;
        private int maxRedirect = 0;
        private List<Header> requestHeaders = new ArrayList<>();

        /**
         * Define o User-Agent para as requisições
         * 
         * @param userAgent string do user agent
         * @return este builder para chamadas encadeadas
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Define o timeout em milissegundos
         * 
         * @param timeoutMS timeout em milissegundos (deve ser >= 0)
         * @return este builder para chamadas encadeadas
         */
        public Builder timeout(Integer timeoutMS) {
            this.timeoutMS = timeoutMS;
            return this;
        }

        /**
         * Define o tamanho máximo da resposta em bytes
         * 
         * @param maxResponseLength tamanho máximo em bytes (deve ser >= 1024)
         * @return este builder para chamadas encadeadas
         */
        public Builder maxResponseLength(int maxResponseLength) {
            this.maxResponseLength = maxResponseLength;
            return this;
        }

        /**
         * Define o proxy a ser utilizado
         * 
         * @param proxy proxy (HTTP, SOCKS ou BIND)
         * @return este builder para chamadas encadeadas
         */
        public Builder proxy(ScrapProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Define se deve aceitar certificados SSL inválidos
         * 
         * @param insecureSSL true para aceitar certificados inválidos
         * @return este builder para chamadas encadeadas
         */
        public Builder insecureSSL(boolean insecureSSL) {
            this.insecureSSL = insecureSSL;
            return this;
        }

        /**
         * Define o número máximo de redirecionamentos
         * 
         * @param maxRedirect número máximo de redirecionamentos (0 = desabilitado)
         * @return este builder para chamadas encadeadas
         */
        public Builder maxRedirect(int maxRedirect) {
            this.maxRedirect = maxRedirect;
            return this;
        }

        /**
         * Habilita redirecionamentos automáticos (máximo de 10)
         * 
         * @return este builder para chamadas encadeadas
         */
        public Builder followRedirects() {
            this.maxRedirect = 10;
            return this;
        }

        /**
         * Desabilita redirecionamentos automáticos
         * 
         * @return este builder para chamadas encadeadas
         */
        public Builder noRedirects() {
            this.maxRedirect = 0;
            return this;
        }

        /**
         * Adiciona um header personalizado às requisições
         * 
         * @param header header HTTP
         * @return este builder para chamadas encadeadas
         */
        public Builder addRequestHeader(Header header) {
            this.requestHeaders.add(header);
            return this;
        }

        /**
         * Constrói a configuração imutável
         * 
         * @return nova instância de ScrapClientConfig
         * @throws IllegalArgumentException se alguma configuração for inválida
         */
        public ScrapClientConfig build() {
            validate();
            return new ScrapClientConfig(this);
        }

        /**
         * Valida as configurações antes de construir
         */
        private void validate() {
            if (timeoutMS != null && timeoutMS < 0) {
                throw new IllegalArgumentException("Timeout não pode ser negativo: " + timeoutMS);
            }
            if (maxResponseLength < 1024) {
                throw new IllegalArgumentException(
                        "maxResponseLength deve ser >= 1024 bytes, atual: " + maxResponseLength);
            }
            if (maxRedirect < 0) {
                throw new IllegalArgumentException("maxRedirect não pode ser negativo: " + maxRedirect);
            }
        }
    }

    @Override
    public String toString() {
        return "ScrapClientConfig{" +
                "userAgent='" + userAgent + '\'' +
                ", timeoutMS=" + timeoutMS +
                ", maxResponseLength=" + maxResponseLength +
                ", proxy=" + proxy +
                ", insecureSSL=" + insecureSSL +
                ", maxRedirect=" + maxRedirect +
                ", requestHeaders=" + requestHeaders.size() + " headers" +
                '}';
    }
}
