package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws OWLException {
        loadOntology();
    }

    public static void loadOntology() throws OWLException {
        // Create an ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        String baseIRI = "http://purl.org/vso/ns#";

        try {
            File file = new File("src/main/resources/car-sales-ontology.owl");
            OWLOntology carSalesOntology = manager.loadOntologyFromOntologyDocument(file);

            // Uncomment to display all available classes inside ontology
            // carSalesOntology.classesInSignature().forEach(cls -> System.out.println("Class: " + cls.getIRI()));

            // Extract the class automobile and vehicle from ontology
            OWLClass car = dataFactory.getOWLClass(IRI.create(baseIRI + "Automobile"));
            OWLClass vehicle = dataFactory.getOWLClass(IRI.create(baseIRI + "Vehicle"));
            // Create new classes and extend the current ontology
            List<String> newClassNames = Arrays.asList("Customer", "Dealer", "Company");
            for (String className : newClassNames) {
                OWLClass newClass = dataFactory.getOWLClass(IRI.create(baseIRI + className));
                OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
                // Add the axiom to the ontology
                manager.addAxiom(carSalesOntology, declarationAxiom);
            }


            // Uncomment to display the properties (relationships that describe of individuals) of the ontology
            // carSalesOntology.objectPropertiesInSignature().forEach(prop -> System.out.println("Property: " + prop.getIRI()));
            // Extracted properties bodyStyle and engineType
            OWLProperty bodyStyle = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "bodyStyle"));
            OWLProperty engineType = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "engineType"));
            // Created properties
            OWLProperty dealerLocatedIn = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "dealerLocatedIn"));
            OWLProperty hasPrice = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "hasPrice"));
            OWLProperty hasCarId = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "hasCarId"));

            manager.addAxiom(carSalesOntology, dataFactory.getOWLDeclarationAxiom(dealerLocatedIn));
            manager.addAxiom(carSalesOntology, dataFactory.getOWLDeclarationAxiom(hasPrice));
            manager.addAxiom(carSalesOntology, dataFactory.getOWLDeclarationAxiom(hasCarId));

            // Individuals are the data from dataset itself

        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology: " + e.getMessage());
        }
    }
}