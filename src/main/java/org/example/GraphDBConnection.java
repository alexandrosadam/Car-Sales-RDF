package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GraphDBConnection {
    private static RepositoryConnection connection;
    private static HTTPRepository carSalesRepository;

    public static void initializeRepository() {
        Dotenv dotenv = Dotenv.load();

        carSalesRepository = new HTTPRepository(dotenv.get("GRAPHDB_URL"));
        connection = carSalesRepository.getConnection();

        connection.clear(); // Clear the repository before we start connecting to the GraphDB repository
    }

    public static void addRDFFileToGraphDB(String rdfFilePath) throws FileNotFoundException {
        connection.begin(); // starts a new transaction with the GraphDB repository

        try {
            connection.add(new FileInputStream(rdfFilePath), "urn:base", RDFFormat.TURTLE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        connection.commit(); // Commiting the data to the repository
    }

    public static RepositoryConnection getCarSalesRepositoryConnection() {
        return connection;
    }

    public static void closeRepositoryConnection() {
        connection.close();
        carSalesRepository.shutDown();
    }
}
