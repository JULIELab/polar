package de.julielab.polar.pipeline.webservice.beans;

import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.Disease;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.julielab.jcore.types.medical.Medication;
import de.julielab.jcore.utility.JCoReAnnotationTools;
import de.julielab.jcore.utility.JCoReTools;
import de.julielab.jcore.utility.index.JCoReOverlapAnnotationIndex;
import de.julielab.polar.pipeline.webservice.dto.OutputRelation;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Scope(value = "singleton")
public class RelationExtractionService {
    public List<OutputRelation> extractRelations(JCas jCas) throws AnalysisEngineProcessException {
        JCoReOverlapAnnotationIndex<Sentence> sentenceIndex = new JCoReOverlapAnnotationIndex<>(jCas, Sentence.type);
        JCoReOverlapAnnotationIndex<Token> tokenIndex = new JCoReOverlapAnnotationIndex<>(jCas, Token.type);
        List<OutputRelation> relations = new ArrayList<>();
        for (Disease d : jCas.<Disease>getAnnotationIndex(Disease.type)) {
            for (Medication m : jCas.<Medication>getAnnotationIndex(Medication.type)) {
                final List<Token> tokensBetween = JCoReAnnotationTools.getAnnotationsBetween(d, m, tokenIndex);
                final Optional<Sentence> mSentence = sentenceIndex.search(m).stream().findFirst();
                final Optional<Sentence> dSentence = sentenceIndex.search(d).stream().findFirst();
                Sentence commonSentence = mSentence.isPresent() && dSentence.isPresent() && mSentence.get() == dSentence.get() ? mSentence.get() : null;

                String docId = JCoReTools.getDocId(jCas);
                Annotation firstAnnotation = d.getBegin() < m.getBegin() ? d : m;
                Annotation secondAnnotation = d.getBegin() < m.getBegin() ? m : d;
                String disease = d.getCoveredText();
                String medication = m.getCoveredText();
                String diseaseCategory = d.getSpecificType();
                String medicationCategory = m.getSpecificType();
                int tokenDistance = tokensBetween.size();
                String cooccurrenceType = commonSentence == null ? "document" : "sentence";
                int firstSentenceBegin = mSentence.isPresent() && dSentence.isPresent() ? Math.min(mSentence.get().getBegin(), dSentence.get().getBegin()) : -1;
                int secondSentenceEnd = mSentence.isPresent() && dSentence.isPresent() ? Math.max(mSentence.get().getEnd(), dSentence.get().getEnd()) : -1;
                String textBetween;
                if (commonSentence != null)
                    textBetween = commonSentence.getCoveredText();
                else if (firstSentenceBegin >= 0)
                    textBetween = jCas.getDocumentText().substring(firstSentenceBegin, secondSentenceEnd);
                else
                    textBetween = jCas.getDocumentText().substring(firstAnnotation.getBegin(), secondAnnotation.getEnd());

                final OutputRelation rel = new OutputRelation();
                rel.setDoc_id(docId);
                rel.setDisease_text(disease);
                rel.setMedication_text(medication);
                rel.setDisease_category(diseaseCategory);
                rel.setMedication_category(medicationCategory);
                rel.setDisease_offset(d.getBegin());
                rel.setMedication_offset(m.getBegin());
                rel.setToken_distance(tokenDistance);
                rel.setCoocurrence_type(cooccurrenceType);
                rel.setContext(StringUtils.normalizeSpace(textBetween));

                relations.add(rel);
            }
        }
        return relations;
    }
}
