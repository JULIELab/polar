package de.julielab.polar.pipeline.webservice;

import de.julielab.jcore.types.Header;
import de.julielab.polar.pipeline.webservice.beans.CasPoolBean;
import de.julielab.polar.pipeline.webservice.beans.PolarPipelineService;
import de.julielab.polar.pipeline.webservice.beans.RelationExtractionService;
import de.julielab.polar.pipeline.webservice.domainobjects.UimaAnalysisEngineSequence;
import de.julielab.polar.pipeline.webservice.dto.InputText;
import de.julielab.polar.pipeline.webservice.dto.OutputRelation;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class offers the /pipeline endpoint of the application. It is responsible for receiving text data,
 * processing it and returning the results. All other classes exist to be used from this class directly or indirectly.
 * </p>
 * <p>The controller receives a text analysis pipeline from {@link PolarPipelineService} to process incoming texts for
 * <tt>disease</tt> and <tt>medication</tt> entities. The {@link RelationExtractionService} then captures co-occurring
 * pairs of disease and medication entities into the output format, a list of {@link OutputRelation} objects.</p>
 */
@RestController
public class PipelineController {

    private PolarPipelineService pipelineService;
    private RelationExtractionService relationExtractionService;
    private CasPoolBean casPoolBean;

    public PipelineController(PolarPipelineService pipelineService, RelationExtractionService relationExtractionService, CasPoolBean casPoolBean) {
        this.pipelineService = pipelineService;
        this.relationExtractionService = relationExtractionService;
        this.casPoolBean = casPoolBean;
    }

    /**
     * The main method of the application: receive input texts, process them for disease and medication entities,
     * extract co-occurrences between the two classes and return the results.
     * @param input A list of German medical texts.
     * @return Pairs of disease and medication entities that appeared together within a single text.
     * @throws PipelineRunException
     */
    @PostMapping(path = "/pipeline", consumes = "application/json")
    public List<OutputRelation> runPipeline(@RequestBody List<InputText> input) throws PipelineRunException {
        try {
            final UimaAnalysisEngineSequence polarPipeline = pipelineService.getPolarPipeline();
            List<OutputRelation> allRelations = new ArrayList<>();
            for (InputText text : input) {
                final CAS cas = casPoolBean.getCas();
                final JCas jCas = cas.getJCas();
                final Header h = new Header(jCas);
                h.setDocId(text.getTextId());
                h.addToIndexes();
                jCas.setDocumentText(text.getText());
                polarPipeline.process(jCas);
                final List<OutputRelation> relations4document = relationExtractionService.extractRelations(jCas);
                allRelations.addAll(relations4document);
                casPoolBean.release(cas);
                pipelineService.releaseUimaEngineSequence(polarPipeline);
            }
            return allRelations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PipelineRunException(e.getClass().getCanonicalName() + " occurred. Error message: " + e.getMessage(), e);
        }
    }


}
