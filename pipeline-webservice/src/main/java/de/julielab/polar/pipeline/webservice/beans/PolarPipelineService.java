package de.julielab.polar.pipeline.webservice.beans;

import de.julielab.polar.pipeline.webservice.ConfigurationConstants;
import de.julielab.polar.pipeline.webservice.domainobjects.UimaAnalysisEngineSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * This service allows the creation of a number of analysis pipelines that fulfill the task of this application
 * (see {@link de.julielab.polar.pipeline.webservice.PipelineWebserviceApplication} for the purpose of the application).
 * This is required to serve multiple requests concurrently.
 */
@Service
public class PolarPipelineService {
    private final static Logger log = LoggerFactory.getLogger(PolarPipelineService.class);
    private PolarPipelineFactory polarPipelineFactory;
    private ArrayBlockingQueue<UimaAnalysisEngineSequence> pipelineRepository;

    public PolarPipelineService(@Value("${" + ConfigurationConstants.POLAR_NUMCONCURRENTPIPELINES + ":1}") int numConcurrentPipelines, PolarPipelineFactory polarPipelineFactory) throws Exception {
        this.polarPipelineFactory = polarPipelineFactory;
        pipelineRepository = new ArrayBlockingQueue<>(numConcurrentPipelines);
        createPipelines(numConcurrentPipelines);
    }

    private void createPipelines(int numConcurrentPipelines) throws Exception {
        for (int i = 0; i < numConcurrentPipelines; i++) {
            pipelineRepository.offer(polarPipelineFactory.getObject());
        }
    }

    public UimaAnalysisEngineSequence getPolarPipeline() throws Exception {
        log.trace("Requesting a pipeline from the pipeline repository");
        return pipelineRepository.take();
    }

    public void releaseUimaEngineSequence(UimaAnalysisEngineSequence uimaAnalysisEngineSequence) {
        pipelineRepository.offer(uimaAnalysisEngineSequence);
    }
}
