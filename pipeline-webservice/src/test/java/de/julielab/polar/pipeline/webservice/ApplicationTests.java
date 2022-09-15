package de.julielab.polar.pipeline.webservice;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static de.julielab.polar.pipeline.webservice.ConfigurationConstants.POLAR_DISEASEDICTIONARY;
import static de.julielab.polar.pipeline.webservice.ConfigurationConstants.POLAR_MEDICATIONDICTIONARY;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {POLAR_DISEASEDICTIONARY + "=src/test/resources/diseases.tsv", POLAR_MEDICATIONDICTIONARY + "=src/test/resources/medication.tsv"})
@AutoConfigureMockMvc
class ApplicationTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void contextLoads() throws Exception {
        final JSONObject testInput = new JSONObject(Map.of("text", "Der Patient war mit einem Beinbruch vorstellig. Gegen die Schmerzen wurde Aspirin verschrieben.", "textId", "4711"));
        final JSONArray testInputArray = new JSONArray();
        testInputArray.put(testInput);
        mvc.perform(post("/pipeline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testInputArray.toString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].doc_id", is("4711")));
    }

}
