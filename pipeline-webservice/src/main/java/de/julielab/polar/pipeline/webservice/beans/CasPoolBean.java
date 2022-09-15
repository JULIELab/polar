package de.julielab.polar.pipeline.webservice.beans;

import de.julielab.polar.pipeline.webservice.ConfigurationConstants;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.ResourceManager_impl;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.ProcessingResourceMetaData_impl;
import org.apache.uima.util.CasPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * <p>
 * UIMA components use CAS objects to store and manipulate data. Each CAS corresponds to one input text.
 * </p>
 * <p>This component manages fixed-size pool of CAS objects. It basically just offers CASes and saves resources
 * by re-using them. This is normal procedure in UIMA to avoid the costly creation of CAS objects.</p>
 */
@Component
@Scope(value = "singleton")
public class CasPoolBean {

    private static Logger logger = LoggerFactory.getLogger(CasPoolBean.class);
    private final CasPool casPool;

    public CasPoolBean(@Value("${" + ConfigurationConstants.POLAR_NUMCONCURRENTPIPELINES + ":1}") int numConcurrentPipelines) throws ResourceInitializationException {
        TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
                "de.julielab.jcore.types.jcore-morpho-syntax-types",
                "de.julielab.jcore.types.jcore-document-structure-pubmed-types",
                "de.julielab.jcore.types.jcore-document-meta-pubmed-types",
                "de.julielab.jcore.types.extensions.jcore-document-meta-extension-types",
                "de.julielab.jcore.types.casmultiplier.jcore-dbtable-multiplier-types",
                "de.julielab.jcore.types.jcore-semantics-biology-types",
                "de.julielab.jcore.types.extensions.jcore-medical-types"
        );
        final ProcessingResourceMetaData_impl metaData = new ProcessingResourceMetaData_impl();
        metaData.setTypeSystem(tsd);
        // Create a CasPool.
        casPool = new CasPool(numConcurrentPipelines, metaData, new ResourceManager_impl());
    }

    public CAS getCas() throws InterruptedException {
        CAS cas = casPool.getCas();
        while (cas == null) {
            logger.debug("No CAS currently available, waiting for a CAS to become available.");
            synchronized (casPool) {
                casPool.wait();
                cas = casPool.getCas();
            }
        }
        return cas;
    }

    public void release(CAS cas) {
        casPool.releaseCas(cas);
    }
}
