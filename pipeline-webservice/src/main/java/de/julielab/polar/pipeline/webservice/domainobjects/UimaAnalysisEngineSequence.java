package de.julielab.polar.pipeline.webservice.domainobjects;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class UimaAnalysisEngineSequence {
    private AnalysisEngine[] engines;

    public UimaAnalysisEngineSequence(AnalysisEngine... engines) {
        this.engines = engines;
    }

    public void process(JCas jCas) throws AnalysisEngineProcessException {
        for (AnalysisEngine engine : engines) {
            engine.process(jCas);
        }
    }
}
