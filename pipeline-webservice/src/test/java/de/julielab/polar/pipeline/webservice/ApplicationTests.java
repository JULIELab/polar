package de.julielab.polar.pipeline.webservice;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.util.Map;

import static de.julielab.polar.pipeline.webservice.ConfigurationConstants.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = {POLAR_DISEASEDICTIONARY + "=src/test/resources/diseases.tsv", POLAR_MEDICATIONDICTIONARY + "=src/test/resources/medication.tsv", POLAR_NUMCONCURRENTPIPELINES+"=2"})
@AutoConfigureMockMvc
public class ApplicationTests extends AbstractTestNGSpringContextTests {
    @Autowired
    private MockMvc mvc;

    @Test(threadPoolSize = 3, invocationCount = 10,  timeOut = 10000)
    public void test() throws Exception {
        final JSONObject testInput = new JSONObject(Map.of("text", "Der Patient war mit einem Beinbruch vorstellig. Gegen die Schmerzen wurde Aspirin verschrieben.", "textId", "4711"));
        final JSONObject testInput2 = new JSONObject(Map.of("text", "Die vorstellige Person hatte Bauchschmerzen und war deshalb sehr streitbar. Gegen die Beschwerden wurde Darmfosan gegeben.", "textId", "42"));
        final JSONArray testInputArray = new JSONArray();
        testInputArray.put(testInput);
        testInputArray.put(testInput2);
        mvc.perform(post("/pipeline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testInputArray.toString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].doc_id", is("4711")))
                .andExpect(jsonPath("$[0].disease_text", is("Beinbruch")))
                .andExpect(jsonPath("$[0].medication_text", is("Aspirin")))
                .andExpect(jsonPath("$[1].doc_id", is("42")))
                .andExpect(jsonPath("$[1].disease_text", is("streitbar")))
                .andExpect(jsonPath("$[1].medication_text", is("Darmfosan")));
    }


}
