package de.julielab.polar.pipeline.webservice;

import de.julielab.jcore.types.Disease;
import de.julielab.jcore.types.Header;
import de.julielab.jcore.types.Sentence;
import de.julielab.polar.pipeline.webservice.beans.CasPoolBean;
import de.julielab.polar.pipeline.webservice.beans.PolarPipelineFactory;
import de.julielab.polar.pipeline.webservice.beans.PolarPipelineService;
import de.julielab.polar.pipeline.webservice.beans.RelationExtractionService;
import de.julielab.polar.pipeline.webservice.domainobjects.UimaAnalysisEngineSequence;
import de.julielab.polar.pipeline.webservice.dto.InputText;
import de.julielab.polar.pipeline.webservice.dto.OutputRelation;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping(path = "/pipeline")
    public List<OutputRelation> runPipeline(@RequestBody List<InputText> input) throws PipelineRunException {
        try {
            List<OutputRelation> allRelations = new ArrayList<>();
            for (InputText text : input) {
                final CAS cas = casPoolBean.getCas();
                final JCas jCas = cas.getJCas();
                final Header h = new Header(jCas);
                h.setDocId(text.getTextId());
                h.addToIndexes();
                jCas.setDocumentText(text.getText());
                final UimaAnalysisEngineSequence polarPipeline = pipelineService.getPolarPipeline();
                polarPipeline.process(jCas);
                final List<OutputRelation> relations4document = relationExtractionService.extractRelations(jCas);
                allRelations.addAll(relations4document);
                casPoolBean.release(cas);
                pipelineService.releaseUimaEngineSequence(polarPipeline);
            }
            return allRelations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PipelineRunException("POLAR pipeline run failed", e);
        }
    }



}
