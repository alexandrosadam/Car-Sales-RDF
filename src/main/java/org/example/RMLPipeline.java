package org.example;

import java.io.IOException;


public final class RMLPipeline {
    static String rootFolder = "./src/main/resources/";
    static String mappingRMLRulesFile = rootFolder + "car-sales-mappings.ttl";
    static String carSalesOntologyFile = rootFolder + "car_sales_ontology.owl";
    static String outputMappingRMLRulesFile = rootFolder + "rml_rules_mappings_output.ttl";

    public static void main(String[] args) throws IOException {
        GraphDBConnection.initializeRepository(); // Initializes the connection to the GraphDB repository

        GraphDBConnection.addRDFFileToGraphDB(carSalesOntologyFile); // Insert the owl ontology (converted to Turtle format) file into the repository

        Utils.executeRMLMapper(mappingRMLRulesFile, outputMappingRMLRulesFile); // Execute RML pipeline to apply RML rules on car sales dataset

        GraphDBConnection.addRDFFileToGraphDB(outputMappingRMLRulesFile); // Insert output file after mapping RLM rules on car sales dataset into the repository

        Utils.executePipelineOfSPARQLQueries(); // Execute pipeline of SPARQL queries

        GraphDBConnection.closeRepositoryConnection(); // close the connection of the repository in GraphDB
    }
}