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
package org.n52.sos.hackair.ds;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.util.HttpClientHandler;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HackAIRConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HackAIRConnector.class);
    private static final String DEFAULT_HOST ="https://api.hackair.eu/";
    private static final String MEASUREMENTS_PATH = "measurements";
    private static final String TIMESTAMP_START_PARAM = "timestampStart";
    private static final String TIMESTAMP_END_PARAM = "timestampEnd";
    private static final String SOURCE_PARAM = "source";
    private static final String SHOW_PARAM = "show";
    private static final String SHOW_ALL_DEFAULT = "all";
    
    
    private HttpClientHandler httpClientHandler;
    private HackAIRConfiguration config;
    private ObjectMapper mapper = new ObjectMapper();
    
    public HackAIRConnector(String configFile) {
        this.config = readConfig(configFile != null && !configFile.isEmpty() ? configFile : "/hackair.json");
    }
    
    @Inject
    public void setHttpClientHandle(HttpClientHandler httpClientHandler) {
        this.httpClientHandler = httpClientHandler;
    }
    
    public Response getData(DateTime start, DateTime end, String source) throws OwsExceptionReport {
        try {
            return mapper.convertValue(httpClientHandler.doGet(getHost(), MEASUREMENTS_PATH, getParameter(start, end, source)), Response.class);
        } catch (URISyntaxException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }
    
    private Map<String, String> getParameter(DateTime start, DateTime end, String source) throws CodedException {
        Map<String, String> map = new HashMap<>();
        if (start != null) {
            map.put(TIMESTAMP_START_PARAM, DateTimeHelper.formatDateTime2IsoString(start));
        } else {
            throw new NoApplicableCodeException().withMessage("The start time is missing!");
        }
        if (end != null) {
            map.put(TIMESTAMP_END_PARAM, DateTimeHelper.formatDateTime2IsoString(end));
        }
        if (source != null && !source.isEmpty()) {
            map.put(SOURCE_PARAM, source);
        }
        map.put(SHOW_PARAM, SHOW_ALL_DEFAULT);
        return map;
    }

    private URI getHost() throws URISyntaxException {
        return new URI(config != null && config.getUrl() != null && !config.getUrl().isEmpty() 
                ? config.getUrl()
                : DEFAULT_HOST);
    }
    
    private HackAIRConfiguration readConfig(String file) {
        try (InputStream taskConfig = getClass().getResourceAsStream("/hackair.json")) {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(taskConfig, HackAIRConfiguration.class);
        } catch (IOException e) {
            LOGGER.error("Could not load {}. Using empty config.", file, e);
            return new HackAIRConfiguration();
        }
    }
    
}
