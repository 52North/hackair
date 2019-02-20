package org.n52.sos.hackair.converter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.Loc;
import org.n52.sos.hackair.data.PollutantI;
import org.n52.sos.hackair.util.HackAIRHelper;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.RequestContext;
import org.n52.sos.util.net.IPAddress;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class InsertObservationConverter implements HackAIRHelper {
    
    private static GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING_SINGLE), 4326);
    
    public InsertObservationRequest convert(Data data) throws OwsExceptionReport {
        InsertObservationRequest request = createDefault();
        String procedure = prepareProcedureIdentifier(data);
        String offering = prepareOfferingIdentifier(procedure);
        request.setOfferings(Lists.newArrayList(offering));
        request.addObservation(createObservations(data, procedure, offering));
        return request;
    }
    
    private OmObservation createObservations(Data data, String procedure, String offering)
            throws InvalidSridException, DateTimeParseException {
        OmObservation observation = new OmObservation();
        observation.setGmlId(data.getId());
        observation.setObservationConstellation(createObservationConstellation(data, procedure, offering));
        observation.setValue(createValue(data));
        observation.setParameter(createParameter(data));
        observation.addSpatialFilteringProfileParameter(createGeometry(data.getLoc()));
        return observation;
    }

    private InsertObservationRequest createDefault() {
        InsertObservationRequest request = new InsertObservationRequest();
        request.setService(SosConstants.SOS);
        request.setVersion(Sos2Constants.SERVICEVERSION);
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(new IPAddress("127.0.0.1"));
        request.setRequestContext(requestContext);
        return request;
    }

    private OmObservationConstellation createObservationConstellation(Data data, String procedure, String offering) throws InvalidSridException {
        OmObservationConstellation constellation = new OmObservationConstellation();
        constellation.setObservableProperty(createPhenomenon(data.getPollutantQ().getName()));
        constellation.setFeatureOfInterest(createFeatureOfInterest(data));
        constellation.setOfferings(createOffering(offering));
        constellation.setObservationType(OmConstants.OBS_TYPE_MEASUREMENT);
        constellation.setProcedure(createProcedure(procedure));
        return constellation;
}
    
    private AbstractFeature createFeatureOfInterest(Data data) throws InvalidSridException {
        SamplingFeature feature = new SamplingFeature(getFeatureIdentifier(data));
        feature.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        feature.setGeometry(createGeometry(data.getLoc()));
//        if (data.hasSourceInfo() && data.getSourceInfo().hasCountryCode()) {
//            feature.addParameter(createTextParameter("countryCode", data.getSourceInfo().getCountryCode()));
//        }
        return feature;
    }

    private CodeWithAuthority getFeatureIdentifier(Data data) {
        if (data.getSourceInfo().hasLocation()) {
            if (data.getSourceType().equals(WEBSERVICES) && data.getSourceInfo().hasSource()) {
                return createIdentifier(joinValues(data.getSourceType(), data.getSourceInfo().getSource()),
                        data.getSourceInfo().getLocation());
            } else {
                return createIdentifier(data.getSourceType(), data.getSourceInfo().getLocation());
            }
        } else if (data.getSourceInfo().hasSensor()) {
            return createIdentifier(data.getSourceType(), data.getSourceInfo().getSensor().getId().toString());
        } else if (data.getSourceInfo().hasWebcamId()) {
            return createIdentifier(data.getSourceType(), data.getSourceInfo().getWebcamId());
        } else if (data.getSourceInfo().hasDevice() && data.getSourceInfo().getDevice().hasUuid()) {
            return createIdentifier(data.getSourceType(), data.getSourceInfo().getDevice().getUuid());
        }
        return createIdentifier(data.getSourceType(), data.getSourceInfo().getId());
    }
    
    private CodeWithAuthority createIdentifier(String source, String id) {
        return new CodeWithAuthority(joinValues(source,id));
    }
    
    private SosProcedureDescription createProcedure(String procedure) {
        return new PhysicalSystem().setIdentifier(procedure);
    }

    private Set<String> createOffering(String offering) {
        return Sets.newHashSet(offering);
    }

    private AbstractPhenomenon createPhenomenon(String identifier) {
        return new OmObservableProperty(identifier);
}

    private Collection<NamedValue<?>> createParameter(Data data) {
        Set<NamedValue<?>> parameters = new LinkedHashSet<>();
        parameters.add(createPollutantI(data.getPollutantI()));
        parameters.add(createTextParameter("source", data.getSourceType()));
        if (data.getSourceInfo().hasImageUrl()) {
            parameters.add(createTextParameter("image", data.getSourceInfo().getImageUrl()));
        }
        if (data.getSourceInfo().hasUser() && data.getSourceInfo().getUser().hasUsername()) {
            parameters.add(createTextParameter("user", data.getSourceInfo().getUser().getUsername()));
        }
        if (data.getSourceInfo().hasUsername()) {
            parameters.add(createTextParameter("user", data.getSourceInfo().getUsername()));
        }
        return parameters;
    }

    private NamedValue<String> createPollutantI(PollutantI pollutantI) {
        return createTextParameter(pollutantI.getName(), pollutantI.getIndex());
    }
    
    private NamedValue<String> createTextParameter(String name, String value) {
        NamedValue<String> namedValue = new NamedValue<>();
        namedValue.setName(new ReferenceType(name));
        namedValue.setValue(new TextValue(value));
        return namedValue;
    }

    private ObservationValue<?> createValue(Data data) throws DateTimeParseException {
        QuantityValue value = new QuantityValue(data.getPollutantQ().getValue(), data.getPollutantQ().getUnit());
        SingleObservationValue<Double> observationValue = new SingleObservationValue<>(value);
        observationValue.setPhenomenonTime(new TimeInstant(getDateTime(data.getDateStr())));
        return observationValue;
    }
    
    private Geometry createGeometry(Loc loc) {
        return factory.createPoint(new Coordinate(loc.getCoordinates().get(1), loc.getCoordinates().get(0)));
    }

}
