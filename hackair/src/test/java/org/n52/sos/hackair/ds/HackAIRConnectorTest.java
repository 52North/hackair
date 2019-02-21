package org.n52.sos.hackair.ds;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.util.HttpClientHandler;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HackAIRConnectorTest {

    private static final String date = "2018-01-01T00:00:00Z";
    private static HttpClientHandler httpClient;

    private static HackAIRConnector connector;

    public HackAIRConnectorTest() throws JsonParseException, JsonMappingException, IOException {
        httpClient = new HttpClientHandler();
        httpClient.init();
        connector = new HackAIRConnector("/hackair.json");
        connector.setHttpClientHandle(httpClient);
    }

    public static void main(String[] args) {
        try {
            HackAIRConnectorTest test = new HackAIRConnectorTest();
            test.test_connection();
            System.out.println(test.calculateDurationSesonds(DateTime.now().minusYears(1), DateTime.now()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.destroy();
        }
    }

    public void test_connection() throws OwsExceptionReport {
        Response data = connector.getData(DateTimeHelper.parseIsoString2DateTime(date), null, "webservices");
        if (data.getCount() == 600) {
            System.out.println(data.getCount());
            checkMinMaxTime(data);
        } else {
            System.out.println(data);
        }
    }
    
    private void checkMinMaxTime(Response response) throws DateTimeParseException {
        DateTime first = null;
        DateTime last = null;
        for (Data data : response.getData()) {
            DateTime current = new DateTime(DateTimeHelper.parseIsoString2DateTime(data.getDateStr()));
            first = first == null || current.isBefore(first) ? current : first;
            last = last == null || current.isAfter(last) ? current : last;
        }
        System.out.println("First: " + first);
        System.out.println("Last: " + last);
    }
    
    private int calculateDurationSesonds(DateTime startTime, DateTime endTime) {
        long durationMillis = new Interval(startTime, endTime).toDurationMillis();
        long halfDurationMillis = durationMillis/2;
        return Math.toIntExact(durationMillis/2000);
    }

}
