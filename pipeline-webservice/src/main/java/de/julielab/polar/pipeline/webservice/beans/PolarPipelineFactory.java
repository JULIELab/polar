package de.julielab.polar.pipeline.webservice.beans;

import de.julielab.jcore.ae.lingpipegazetteer.chunking.ConfigurableChunkerProviderImplAlt;
import de.julielab.jcore.ae.lingpipegazetteer.uima.GazetteerAnnotator;
import de.julielab.polar.pipeline.webservice.ConfigurationConstants;
import de.julielab.polar.pipeline.webservice.domainobjects.UimaAnalysisEngineSequence;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PolarPipelineFactory implements FactoryBean<UimaAnalysisEngineSequence> {
private final static Logger log = LoggerFactory.getLogger(PolarPipelineFactory.class);
    private final ExternalResourceDescription externalResource4diseases;
    private final ExternalResourceDescription externalResource4medication;

    public PolarPipelineFactory(@Value("${" + ConfigurationConstants.POLAR_DISEASEDICTIONARY + ":/var/polar/disease_dicts.tsv}") String diseaseDictPath,
                                @Value("${" + ConfigurationConstants.POLAR_MEDICATIONDICTIONARY + ":/var/polar/combinedDrugs.lingpipe}") String medicationDictPath) {
        externalResource4diseases = getExternalResourceDescription4Gazetteer(diseaseDictPath);
        externalResource4medication = getExternalResourceDescription4Gazetteer(medicationDictPath);
    }

    private ExternalResourceDescription getExternalResourceDescription4Gazetteer(String dictionaryPath) {
        final ExternalResourceDescription externalResource4fall;
        externalResource4fall = ExternalResourceFactory.createExternalResourceDescription(
                ConfigurableChunkerProviderImplAlt.class, "file:"+ dictionaryPath,
                ConfigurableChunkerProviderImplAlt.PARAM_CASE_SENSITIVE, false,
                ConfigurableChunkerProviderImplAlt.PARAM_NORMALIZE_TEXT, true,
                ConfigurableChunkerProviderImplAlt.PARAM_TRANSLITERATE_TEXT, false,
                ConfigurableChunkerProviderImplAlt.PARAM_STOPWORD_FILE, "de/julielab/jcore/ae/lingpipegazetteer/stopwords/DE_from_unine.ch",
                ConfigurableChunkerProviderImplAlt.PARAM_USE_APPROXIMATE_MATCHING, true,
                ConfigurableChunkerProviderImplAlt.PARAM_MAKE_VARIANTS, false);
        return externalResource4fall;
    }

    @Override
    public UimaAnalysisEngineSequence getObject() throws Exception {
        log.debug("Creating a new pipeline instance. This message should only appear as many times as the value of the environment variable POLAR_NUMCONCURRENTPIPELINES is set to.");
        return new UimaAnalysisEngineSequence(getSentenceSplitter(), getTokenSplitter(), getDiseaseGazetteer(), getMedicationGazetteer());
    }

    @Override
    public Class<?> getObjectType() {
        return UimaAnalysisEngineSequence.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private AnalysisEngine getSentenceSplitter() throws ResourceInitializationException, InvalidXMLException, IOException {
        return AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jsbd.desc.jcore-jsbd-ae-medical-german");
    }

    private AnalysisEngine getTokenSplitter() throws ResourceInitializationException, InvalidXMLException, IOException {
        return AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jtbd.desc.jcore-jtbd-ae-medical-german");
    }

    private AnalysisEngine getDiseaseGazetteer() throws ResourceInitializationException {
        return  AnalysisEngineFactory.createEngine(GazetteerAnnotator.class,
                GazetteerAnnotator.PARAM_OUTPUT_TYPE, "de.julielab.jcore.types.Disease",
                GazetteerAnnotator.CHUNKER_RESOURCE_NAME, externalResource4diseases);
    }

    private AnalysisEngine getMedicationGazetteer() throws ResourceInitializationException {
        return  AnalysisEngineFactory.createEngine(GazetteerAnnotator.class,
                GazetteerAnnotator.PARAM_OUTPUT_TYPE, "de.julielab.jcore.types.medical.Medication",
                GazetteerAnnotator.CHUNKER_RESOURCE_NAME, externalResource4medication);
    }
}
