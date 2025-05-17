package org.example;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;


public final class RMLPipeline {
    static String rootFolder = "./src/main/resources/";
    static String mappingTemplatePath = "car-sales-mappings.ttl";
    static String convertedCarOntology = "car_sales_ontology_converted.ttl";
    static String rmlRulesMappingsOutput = "rml_rules_mappings_output.ttl";

    public static void main(String[] args) throws IOException {
        File mappingFile = new File(rootFolder + mappingTemplatePath);
        Writer outputMappingFile = new FileWriter(rootFolder + rmlRulesMappingsOutput);

        // Execute RML pipeline to apply RML rules to our dataset and create rml_rules_mappings_output.ttl
        Utils.executeRMLMapper(mappingFile, outputMappingFile);

        HTTPRepository repository = new HTTPRepository("http://pop-os:7200/repositories/MiniProject");
        RepositoryConnection connection = repository.getConnection();

        connection.clear(); // Clear the repository before we start connecting to the GraphDB repository
        connection.begin();

        try {
            // Add ontology to GraphDB repo
            connection.add(new FileInputStream(rootFolder + convertedCarOntology),
                    "urn:base",
                    RDFFormat.TURTLE);

            // Add mappings file to GraphDB repo after applying RML rules to our data
            connection.add(new FileInputStream(rootFolder + mappingTemplatePath),
                    "urn:base",
                    RDFFormat.TURTLE);

            // A simple ASK query to test connectivity
            boolean isWorking = connection.prepareBooleanQuery("ASK { ?s ?p ?o }").evaluate();
            System.out.println("Connection successful. Data exists: " + isWorking);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Committing the transaction persists the data
        connection.commit();

        repository.shutDown();
    }
}