package org.example;

import be.ugent.rml.Executor;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    // Prevent instantiation
    private Utils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void executeRMLMapper(String mappingFilePath, String outputFilePath) {
        try {
            File mappingFile = new File(mappingFilePath);
            Writer outputMappingFile = new FileWriter(outputFilePath);
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

            // Set up the output store (needed when you want to output something else than nquads
            QuadStore outputStore = new RDF4JStore();

            // Create the Executor
            Executor executor = new Executor(rmlStore, factory, functionLoader, outputStore, be.ugent.rml.Utils.getBaseDirectiveTurtle(mappingStream));

            // Execute the mapping
            QuadStore result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));

            // Output the results in a file
            result.write(outputMappingFile, "turtle");
            outputMappingFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void executePipelineOfSPARQLQueries() {
        SPARQLQueryExecution.totalSalesPerDealer(); // Query the total sales per dealer

        SPARQLQueryExecution.countManualAndAutomaticPerDealer(); // Query the total number of manual and automatic cars per dealer

        SPARQLQueryExecution.totalSalesAndAveragePricePerCompany(); // Query the total sales and average car price per company

        SPARQLQueryExecution.mostSoldCarModelPerCompany(); // Query the most sold car model per company

        SPARQLQueryExecution.totalRevenuePerDealerAndRegion(); // Query the total revenue per dealer and region

        SPARQLQueryExecution.findTopFiveCompaniesWithTheMostSoldCars(); // Query that finds the top five companies with the most sold cars

        SPARQLQueryExecution.countMaleAndFemaleCustomers(); // Query that count male and female customers
    }
}
