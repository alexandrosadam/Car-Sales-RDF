package org.example;

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public final class Main {
    static String rootFolder = "./src/main/resources/";
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLDataFactory dataFactory = manager.getOWLDataFactory();
    static File file = new File("src/main/resources/custom_car_sales_ontology.owl");
    static String mappingTemplatePath = "car-sales-mappings.ttl";
    static String outputMappingRMLRulesPath = "car_sales/mapping_results.ttl";

    public static void main(String[] args) throws IOException {
        Main pipeline = new Main();

        File mappingFile = new File(rootFolder + mappingTemplatePath);
        Writer outputMappingFile = new FileWriter(rootFolder + "mapping_outputFile.ttl");

        pipeline.executeRMLMapper(mappingFile, outputMappingFile);


        try {
            OWLOntology carSalesOntology = manager.loadOntologyFromOntologyDocument(file);

            // Uncomment Print all classes in ontology
//            for (OWLAxiom axiom : carSalesOntology.getAxioms())
//                if (axiom instanceof OWLDeclarationAxiom) {
//                    OWLDeclarationAxiom declaration = (OWLDeclarationAxiom) axiom;
//                    if (declaration.getEntity().isOWLClass()) {
//                        OWLClass cls = declaration.getEntity().asOWLClass();
//                        System.out.println("Class: " + cls.getIRI());
//                    }
//                }
        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology: " + e.getMessage());
        }
    }


    void executeRMLMapper(File mappingFile, Writer outputFile) {
        try {
            // Get the mapping string stream
            InputStream mappingStream = new FileInputStream(mappingFile);

            // Load the mapping in a QuadStore
            QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

            // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
            RecordsFactory factory = new RecordsFactory(mappingFile.getParent());

            // Set up the functions used during the mapping
            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);

            FunctionLoader functionLoader = new FunctionLoader(null, libraryMap);

            // Set up the outputstore (needed when you want to output something else than nquads
            QuadStore outputStore = new RDF4JStore();

            // Create the Executor
            Executor executor = new Executor(rmlStore, factory, functionLoader, outputStore, Utils.getBaseDirectiveTurtle(mappingStream));

            // Execute the mapping
            QuadStore result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));

            // Output the result in console
            //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
            //result.write(out, "turtle");
            //out.close();

            // Output the results in a file
            result.write(outputFile, "turtle");
            outputFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}