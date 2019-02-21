/**
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
package org.n52.sos.web.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.hackair.ds.HackAIRConfiguration;
import org.n52.sos.hackair.harvester.HackAIRDataSourceHarvestJobFactory;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin/hackair")
public class AdminHackAIRController extends AbstractAdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminHackAIRController.class);
    
    private static final ObjectMapper om = new ObjectMapper();
    
    @Inject
    private HackAIRDataSourceHarvestJobFactory jobFactory;
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getConfig() {
        HackAIRConfiguration readConfig = readConfig();
        Map<String, Object> model = new HashMap<String, Object>(5);
//        String current = getSelectedProfile();
//        if (current != null && !current.isEmpty()) {
//            model.put(ACTIVE, current);
//        }
//        List<String> profiles = Lists.newArrayList();
//        for (Profile prof : getProfileHandler().getAvailableProfiles().values()) {
//            profiles.add(prof.getIdentifier());
//        }
//        model.put(PROFILES, profiles);
        return new ModelAndView("/admin/hackair", model);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public void stopHarvester() throws OwsExceptionReport {
        jobFactory.stop();
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public void startHarvester() throws CodedException {
        if (checkTransactionalSupported()) {
            jobFactory.startOrUpdate();
        } else {
            throw new RuntimeException("Please activate the transactional operations via the admin interface!");
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public void updateHarvester(JsonNode node) throws CodedException {
        // writeConfig(readConfig());
        if (checkTransactionalSupported()) {
            jobFactory.startOrUpdate();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Please activate the transactional operations via the admin interface!");
        }
    }
    
    
    private HackAIRConfiguration readConfig() {
        try (InputStream taskConfig = getClass().getResourceAsStream("/hackair.json")) {
//            return om.readTree(taskConfig);
            return om.readValue(taskConfig, HackAIRConfiguration.class);
        } catch (IOException e) {
            log.error("Could not load {}. Using empty config.", "hackair.json", e);
            return new HackAIRConfiguration();
        }
    }
    
    private boolean checkTransactionalSupported() {
        try {
            InsertObservationRequest insertObservationRequest = new InsertObservationRequest();
            insertObservationRequest.setService(Sos2Constants.SOS).setVersion(Sos2Constants.SERVICEVERSION);
            InsertSensorRequest insertSensorRequest = new InsertSensorRequest();
            insertSensorRequest.setService(Sos2Constants.SOS).setVersion(Sos2Constants.SERVICEVERSION);
            return getRequestOperator(insertObservationRequest.getServiceOperatorKeyType(),
                    insertObservationRequest.getOperationName()).size() > 0
                    && getRequestOperator(insertSensorRequest.getServiceOperatorKeyType(),
                            insertSensorRequest.getOperationName()).size() > 0;
        } catch (OwsExceptionReport e) {
            return false;
        }
    }

    private void writeConfig(HackAIRConfiguration config) {
        try {
            File file = new File(getClass().getResource("/hackair.json").getFile());
            om.writerFor(HackAIRConfiguration.class).writeValue(file, config);
        } catch (IOException e) {
            log.error("Could not write {}.", "hackair.json", e);
        }
    }
    
    private Set<RequestOperator> getRequestOperator(List<ServiceOperatorKey> list, String name) {
        return list.stream().map(sok -> getRequestOperator(sok, name)).filter(Objects::nonNull).collect(Collectors.toSet());
    }
    
    private RequestOperator getRequestOperator(ServiceOperatorKey sok, String name) {
        return getRequestOperatorRepository().getRequestOperator(new RequestOperatorKey(sok, name));
    }
    
    private RequestOperatorRepository getRequestOperatorRepository() {
        return RequestOperatorRepository.getInstance();
    }
    
}
