package com.serphacker.serposcope.scraper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PostType {
    URL_ENCODED {
        @Override
        public HttpEntity createEntity(Map<String, Object> data, Charset charset, ObjectMapper mapper)
                throws Exception {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getValue() instanceof String) {
                    formparams.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                } else {
                    LOG.warn("trying to url encode non string data");
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
            return new UrlEncodedFormEntity(formparams, charset);
        }
    },
    MULTIPART {
        @Override
        public HttpEntity createEntity(Map<String, Object> data, Charset charset, ObjectMapper mapper) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setCharset(charset)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            ContentType formDataCT = ContentType.create("form-data", charset);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();

                if (entry.getValue() instanceof String) {
                    builder = builder.addTextBody(key, (String) entry.getValue(), formDataCT);
                } else if (entry.getValue() instanceof byte[]) {
                    builder = builder.addBinaryBody(key, (byte[]) entry.getValue());
                } else if (entry.getValue() instanceof ContentBody) {
                    builder = builder.addPart(key, (ContentBody) entry.getValue());
                } else {
                    throw new UnsupportedOperationException(
                            "unsupported body type " + entry.getValue().getClass());
                }
            }

            return builder.build();
        }
    },
    JSON {
        @Override
        public HttpEntity createEntity(Map<String, Object> data, Charset charset, ObjectMapper mapper)
                throws Exception {
            if (mapper == null) {
                throw new IllegalArgumentException("ObjectMapper must not be null for JSON POST operations.");
            }
            String json = mapper.writeValueAsString(data);
            return new StringEntity(json, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8));
        }
    };

    private static final Logger LOG = LoggerFactory.getLogger(PostType.class);

    public abstract HttpEntity createEntity(Map<String, Object> data, Charset charset, ObjectMapper mapper)
            throws Exception;
}
