package org.example;

import java.io.IOException;


public final class RMLPipeline {
    static String rootFolder = "./src/main/resources/";
    static String mappingPathFile = rootFolder + "car-sales-mappings.ttl";
    static String convertedCarOntology = rootFolder + "car_sales_ontology_turtle_format_converted.ttl";
    static String rmlRulesMappingsOutput = rootFolder + "rml_rules_mappings_output.ttl";

    public static void main(String[] args) throws IOException {
        GraphDBConnection.initializeRepository(); // Initializes the connection to the GraphDB repository

        GraphDBConnection.addRDFFileToGraphDB(convertedCarOntology); // Insert the owl ontology (converted to Turtle format) file into the repository

        Utils.executeRMLMapper(mappingPathFile, rmlRulesMappingsOutput); // Execute RML pipeline to apply RML rules on car sales dataset

        GraphDBConnection.addRDFFileToGraphDB(rmlRulesMappingsOutput); // Insert output file after mapping RLM rules on car sales dataset into the repository

        Utils.executePipelineOfSPARQLQueries(); // Execute pipeline of SPARQL queries

        GraphDBConnection.closeRepositoryConnection(); // close the connection of the repository in GraphDB
    }
}