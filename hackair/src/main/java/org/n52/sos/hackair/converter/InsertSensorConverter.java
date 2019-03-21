package org.n52.sos.hackair.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.n52.sos.encode.SensorMLEncoderv20;
import org.n52.sos.hackair.data.PollutantQ;
import org.n52.sos.hackair.util.HackAIRHelper;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapability;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.RequestContext;
import org.n52.sos.util.net.IPAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InsertSensorConverter implements HackAIRHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InsertSensorConverter.class);
    
    public InsertSensorRequest convert(String source, Set<PollutantQ> pollutants, String subProcedure) {
        InsertSensorRequest request = createDefault();
        if (subProcedure != null && !subProcedure.isEmpty()) {
            addData(request, subProcedure, pollutants);
            request.getProcedureDescription().addParentProcedure(source);
        } else {
            addData(request, source, pollutants);
        }
        return request;
    }

    public InsertSensorRequest convert(String source, PollutantQ pollutant, String subProcedure) {
        return convert(source, Sets.newHashSet(pollutant), subProcedure);
    }

    private void addData(InsertSensorRequest request, String source, Set<PollutantQ> pollutants) {
        SosOffering offering = new SosOffering(prepareOfferingIdentifier(source), source);
        createCapabilities(offering);
        final PhysicalSystem system = new PhysicalSystem();

        system.addOffering(offering);
        system
                .setInputs(createInputs(pollutants))
                .setOutputs(createOutputs(pollutants))
                .setIdentifications(createIdentificationList(source))
                .addCapabilities(createCapabilities(offering))
                .addCapabilities(createMobileInsitu(source))
                .setIdentifier(source);

        system.setSensorDescriptionXmlString(encodeToXml(system));

        request.setObservableProperty(pollutants.stream().map(p -> p.getName()).collect(Collectors.toList()));
        request.setProcedureDescription(system);
        request.setMetadata(createInsertSensorMetadata());
    }

    private InsertSensorRequest createDefault() {
        final InsertSensorRequest request = new InsertSensorRequest();
        request.setService(SosConstants.SOS);
        request.setVersion(Sos2Constants.SERVICEVERSION);
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(new IPAddress("127.0.0.1"));
        request.setRequestContext(requestContext);
        request.setProcedureDescriptionFormat(SensorML20Constants.NS_SML_20);
        return request;
    }

    protected SmlIo<?> createInput(PollutantQ phenomeon) {
        return new SmlIo<>(new SweObservableProperty()
                .setDefinition(phenomeon.getName()))
                .setIoName(phenomeon.getName());
    }

    protected SmlIo<?> createQuantityOutput(PollutantQ phenomeon) {
        return new SmlIo<>(new SweQuantity()
                .setUom(phenomeon.getUnit())
                .setDefinition(phenomeon.getName()))
                .setIoName(phenomeon.getName());
    }

    protected SmlIo<?> createCountOutput(PollutantQ phenomeon) {
        return new SmlIo<>(new SweCount()
                .setDefinition(phenomeon.getName()))
                .setIoName(phenomeon.getName());
    }
    protected SmlCapabilities createOfferingCapabilities(SosOffering offering) {
        SmlCapabilities capabilities = new SmlCapabilities("offerings");

        SmlCapability ofering = new SmlCapability("offeringID", createText("urn:ogc:def:identifier:OGC:offeringID", offering.getIdentifier()));
        capabilities.addCapability(ofering);
        return capabilities;
    }

    protected SweField createTextField(String name, String definition, String value) {
        return new SweField(name, new SweText().setValue(value).setDefinition(definition));
    }

    protected SweText createText(String definition, String value) {
        return (SweText) new SweText().setValue(value).setDefinition(definition).setLabel("Offering " + value);
    }

    private List<SmlCapabilities> createMobileInsitu(String source) {
        switch (source) {
        case MOBILE:
            return createMobileInsitu(true, true);
        case WEBCAMS:
        case FLICKR:
            return createMobileInsitu(false, false);
        default:
            return createMobileInsitu(true, false);
        }
    }

    protected List<SmlCapabilities> createMobileInsitu(boolean insitu, boolean mobile) {
        SmlCapabilities capabilities = new SmlCapabilities("metadata");

        SmlCapability smlcInsitu = new SmlCapability("insitu");
        smlcInsitu.setAbstractDataComponent(new SweBoolean().setValue(insitu).addName("insitu"));
        capabilities.addCapability(smlcInsitu);

        SmlCapability smlcMmobile = new SmlCapability("mobile");
        smlcMmobile.setAbstractDataComponent(new SweBoolean().setValue(mobile).addName("mobile"));
        capabilities.addCapability(smlcMmobile);

        return Lists.newArrayList(capabilities);
    }

    protected List<SmlCapabilities> createCapabilities(SosOffering offering) {
        List<SmlCapabilities> capabilities = new ArrayList<>();
        capabilities.add(createOfferingCapabilities(offering));
        return capabilities;
}
    
    protected SosInsertionMetadata createInsertSensorMetadata() {
        SosInsertionMetadata metadata = new SosInsertionMetadata();
        metadata.setFeatureOfInterestTypes(Collections.singleton(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT));
        metadata.setObservationTypes(Lists.newArrayList(OmConstants.OBS_TYPE_MEASUREMENT,
                OmConstants.OBS_TYPE_CATEGORY_OBSERVATION,
                OmConstants.OBS_TYPE_COUNT_OBSERVATION,
                OmConstants.OBS_TYPE_TEXT_OBSERVATION,
                OmConstants.OBS_TYPE_TRUTH_OBSERVATION,
                OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION));
        return metadata;
    }

    protected List<SmlIdentifier> createIdentificationList(String procedureId) {
        List<SmlIdentifier> idents = new ArrayList<>();
        idents.add(new SmlIdentifier(
                OGCConstants.UNIQUE_ID,
                OGCConstants.URN_UNIQUE_IDENTIFIER,
                // TODO check feautre id vs name
                procedureId));
        idents.add(new SmlIdentifier(
                "longName",
                "urn:ogc:def:identifier:OGC:1.0:longName",
                procedureId));
        return idents;
    }


    protected List<SmlIo<?>> createOutputs(Set<PollutantQ> pollutants) {
        List<SmlIo<?>> outputs = Lists.newArrayList();
        for (PollutantQ pollutant : pollutants) {
            outputs.add(createQuantityOutput(pollutant));
        }
        return outputs;
    }


    protected List<SmlIo<?>> createInputs(Set<PollutantQ> pollutants) {
        List<SmlIo<?>> inputs = Lists.newArrayList();
        for (PollutantQ pollutant : pollutants) {
            inputs.add(createInput(pollutant));
        }
        return inputs;
}
    protected String encodeToXml(final PhysicalSystem system) {
        try {
            return new SensorMLEncoderv20().encode(system).xmlText();
        } catch (OwsExceptionReport ex) {
            LOGGER.error("Could not encode SML to valid XML.", ex);
            return "";  // TODO empty but valid sml
        }
}
}
