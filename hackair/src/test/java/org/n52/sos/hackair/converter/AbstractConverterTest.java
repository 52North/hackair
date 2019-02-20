package org.n52.sos.hackair.converter;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.util.HackAIRHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractConverterTest implements HackAIRHelper {
    
    private ObjectMapper om = new ObjectMapper();
    
    @BeforeClass
    public static void initSettingsManager() {
        SettingsManager.getInstance();
        CodingRepository.getInstance();
    }

    @AfterClass
    public static void cleanupSettingManager() {
        SettingsManager.getInstance().cleanup();
    }
    
    protected Response loadData() throws JsonParseException, JsonMappingException, IOException {
        return om.readValue(this.getClass().getResourceAsStream("/data.json"), Response.class);
    }

}
