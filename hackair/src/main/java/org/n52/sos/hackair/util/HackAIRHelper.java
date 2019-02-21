package org.n52.sos.hackair.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.PollutantQ;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.ds.HackAIRConfiguration;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

public interface HackAIRHelper {
    
    
    String FLICKR = "flickr";
    String WEBCAMS = "webcams";
    String MOBILE = "mobile";
    String SENSORS_ARDUINO = "sensors_arduino";
    String SENSORS_BLEAIR = "sensors_bleair";
    String SENSORS_COTS = "sensors_cots";
    String WEBSERVICES = "webservices";
    
    default Set<PollutantQ> getPollutants(Response response) {
        if (response != null && response.hasData()) {
            return response.getData().stream().map(d -> d.getPollutantQ()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
    
    default Set<String> getSubSources(Response response) {
        if (response != null && response.hasData()) {
            return response.getData().stream().filter(d -> d.hasSourceInfo() && d.getSourceInfo().hasSource())
                    .map(d -> d.getSourceInfo().getSource()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    default Set<Integer> getSubProcedures(Response response) {
        if (response != null && response.hasData()) {
            return response.getData().stream().filter(
                    d -> d.hasSourceInfo() && d.getSourceInfo().hasSensor() && d.getSourceInfo().getSensor().hasId())
                    .map(d -> d.getSourceInfo().getSensor().getId()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    default Set<PollutantQ> getPollutantQForSensor(Integer sensorId, Response response) {
        if (response != null && response.hasData()) {
            return response.getData().stream()
                    .filter(d -> d.hasSourceInfo() && d.getSourceInfo().hasSensor()
                            && d.getSourceInfo().getSensor().hasId()
                            && d.getSourceInfo().getSensor().getId().equals(sensorId))
                    .map(d -> d.getPollutantQ()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
    
    default boolean checkForIntervalBetweenResponseAndStartTime(DateTime startTime, Response response) {
        try {
            Set<String> collect = response.getData().stream().map(d -> d.getDateStr()).collect(Collectors.toSet());
            DateTime first = DateTime.now();
            for (String timeString : collect) {
                DateTime current = DateTimeHelper.parseIsoString2DateTime(timeString);
                first = current.isBefore(first) ? current : first;

            }
            return new Interval(startTime, first).toDurationMillis() > 60000;
        } catch (DateTimeParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    default String prepareSubProcedure(String source, Integer sensorId) {
        return prepareSubProcedure(source, sensorId.toString());
    }
    
    default String prepareSubProcedure(String source, String sensorId) {
        return source != null && !source.isEmpty() ? joinValues(source, sensorId) : sensorId;
    }
    
    default String prepareOfferingIdentifier(String source) {
        return "https://api.hackair.eu/sos/offering/" + source;
    }
    
//    default String prepareProcedureIdentifier(String source, Data data) {
//        if (data != null && data.hasSourceInfo() && data.getSourceInfo().hasSensor()) {
//            if (data.getSourceInfo().hasSource()) {
//                return prepareSubProcedure(joinValues(source, data.getSourceInfo().getSource()),
//                        data.getSourceInfo().getSensor().getId());
//            }
//            return prepareSubProcedure(source, data.getSourceInfo().getSensor().getId());
//        }
//        return source;
//    }
    
    default String prepareProcedureIdentifier(Data data) {
        String identifier = data.getSourceType();
        if (data != null && data.hasSourceInfo()) {
            identifier = data.getSourceInfo().hasSource() ? joinValues(identifier, data.getSourceInfo().getSource())
                    : identifier;
            identifier = data.getSourceInfo().hasSensor() && data.getSourceInfo().getSensor().hasId()
                    ? joinValues(identifier, data.getSourceInfo().getSensor().getId().toString())
                    : identifier;
        }
        return identifier;
    }
    
    default String joinValues(String... parts) {
        return Joiner.on("_").join(parts);
    }
    
    default HackAIRConfiguration readConfig(String file) throws CodedException {
        try (InputStream is  = Files.newInputStream(Paths.get(getClass().getResource(getFileName(file)).toURI()))) {
            return new ObjectMapper().readValue(is, HackAIRConfiguration.class);
        } catch (IOException | URISyntaxException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while reading configuration from file '%s'", getFileName(file));
        }
    }
   
    default void writeConfig(HackAIRConfiguration config) throws CodedException {
        try (OutputStream os = Files.newOutputStream(Paths.get(getClass().getResource("/hackair.json").toURI()))) {
            new ObjectMapper().writerFor(HackAIRConfiguration.class).writeValue(os, config);
        } catch (IOException | URISyntaxException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while reading configuration from file '%s'", getFileName(null));
        }
    }
    
    default String getFileName(String file) {
        return file != null && !file.isEmpty() ? file : "/hackair.json";
    }

    default DateTime getDateTime(String timeString) throws DateTimeParseException {
        return DateTimeHelper.parseIsoString2DateTime(timeString);
    }


}
