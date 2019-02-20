/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.hackair.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

public class HttpClientHandler implements Constructable, Destroyable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);
    private static final String ACCEPT_VALUE = "application/vnd.hackair.v1+json";
    private CloseableHttpClient httpclient;

    public JsonNode doGet(URI url, String path, Map<String, String> parameter) throws OwsExceptionReport {
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(getGetUrl(url, path, parameter));
            addHeader(httpGet);
            LOGGER.debug("Request: {}", getGetUrl(url, path, parameter));
            return getContent(httpclient.execute(httpGet));
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    private void addHeader(HttpGet httpGet) {
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.ACCEPT, ACCEPT_VALUE);
    }

    public JsonNode doPost(URI url, String content, MediaType contentType) throws CodedException {
        try {
            HttpPost httpPost = new HttpPost(url);
            LOGGER.debug("SOS request: {}", content);
            httpPost.setEntity(new StringEntity(content, ContentType.create(contentType.toString(), "UTF-8").toString()));
            int counter = 4;
            CloseableHttpResponse response = null;
            do {
                try {
                    response = httpclient.execute(httpPost);
                } catch (IOException e) {
                    if (counter == 0) {
                        throw new NoApplicableCodeException().causedBy(e);
                    } else {
                        LOGGER.info("Error while querying data '{}'. {} retries before throwing exception", e, counter);
                    }
                    counter--;
                }
            } while (response == null && counter >= 0);
            return getContent(response);
        } catch (IOException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    private JsonNode getContent(CloseableHttpResponse response) throws IOException {
        return Json.loadString(EntityUtils.toString(response.getEntity(), "UTF-8"));
//        return new JsonNode( response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
    }

    private URI getGetUrl(URI url, Map<String, String> parameters) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (CollectionHelper.isNotEmpty(parameters)) {
            for (Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder.build();
    }

    private URI getGetUrl(URI url, String path, Map<String, String> parameters) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (!Strings.isNullOrEmpty(path)) {
            uriBuilder.setPath(path);
        }
        return getGetUrl(uriBuilder.build(), parameters);
    }

    @Override
    public void init() {
        httpclient = HttpClients.createDefault();
    }

    @Override
    public void destroy() {
        if (httpclient != null) {
            try {
                httpclient.close();
            } catch (IOException ioe) {
                LOGGER.error("Error while closing HttpClient", ioe);
            }
        }

    }
}
