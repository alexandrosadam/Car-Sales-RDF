package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CreateOntology {
    // Create an ontology manager
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLDataFactory dataFactory = manager.getOWLDataFactory();
    static File file = new File("src/main/resources/car-sales-ontology.owl");

    public static void createOntology() throws OWLException {
        try {
            // Load downloaded ontology
            OWLOntology carSalesOntology = manager.loadOntologyFromOntologyDocument(file);
            IRI baseIRI = carSalesOntology.getOntologyID().getOntologyIRI().orElseThrow();


            // Uncomment to display all available classes inside ontology
            // carSalesOntology.classesInSignature().forEach(cls -> System.out.println("Class: " + cls.getIRI()));

            // Extract the class names Automobile and Vehicle from ontology
            OWLClass automobile = dataFactory.getOWLClass(IRI.create(baseIRI + "Automobile"));
            OWLClass vehicle = dataFactory.getOWLClass(IRI.create(baseIRI + "Vehicle"));
            // Create new classes and extend the current ontology
            OWLClass person = dataFactory.getOWLClass(baseIRI + "#Person");
            OWLClass customer = dataFactory.getOWLClass(baseIRI + "#Customer");
            OWLClass dealer = dataFactory.getOWLClass(baseIRI + "#Dealer");
            OWLClass company = dataFactory.getOWLClass(baseIRI + "#Company");

            // Add new classes to the current ontology
            List<OWLClass> newOwlClasses = Arrays.asList(person, customer, dealer, company);
            for (OWLClass newClass : newOwlClasses) {
                // Create the declaration axiom
                OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
                // Add the axiom to the ontology
                manager.addAxiom(carSalesOntology, declarationAxiom);
            }

            // Subclass axiom: Customer ⊑ Person
            addSubclassAxiomToTheOntology(customer, person, carSalesOntology);
            // Subclass axiom: Dealer ⊑ Company
            addSubclassAxiomToTheOntology(dealer, company, carSalesOntology);
            // Subclass axiom: Automobile ⊑ Vehicle
            addSubclassAxiomToTheOntology(automobile, vehicle, carSalesOntology);

            // Add annotations to the new classes that we created
            addAnnotations(carSalesOntology, person, baseIRI, "Person", "A human being.", "https://dbpedia.org/page/Person");
            addAnnotations(carSalesOntology, customer, baseIRI, "Customer", "A customer is someone who purchases goods or services..", "http://dbpedia.org/resource/Customer");
            addAnnotations(carSalesOntology, dealer, baseIRI, "Dealer", "A dealer is an agent who sells goods on behalf of a company or manufacturer.", "http://dbpedia.org/resource/Car_dealership");
            addAnnotations(carSalesOntology, company, baseIRI, "Company", "A company is a legal entity formed to conduct business..", "http://dbpedia.org/resource/Company");

            // Uncomment to display the properties (relationships that describe of individuals) of the ontology
            // carSalesOntology.objectPropertiesInSignature().forEach(prop -> System.out.println("Property: " + prop.getIRI()));

            // Extracted properties bodyStyle and engineType
            OWLProperty bodyStyle = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "bodyStyle"));
            OWLProperty engineType = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "engineType"));
            // Created properties
            OWLObjectProperty dealerLocatedIn = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "#dealerLocatedIn"));
            OWLDataProperty hasPrice = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "#hasPrice"));
            OWLDataProperty hasCarId = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "#hasCarId"));

            // Add details to the created properties
            addObjectPropertyDetails(carSalesOntology, dealerLocatedIn, null, Set.of(dealer, company), automobile, "dealerLocatedIn",
                    "Relates a dealer or company to the location where it operates.", baseIRI, List.of(IRI.create("http://dbpedia.org/resource/Dealership")));
            addDataPropertyDetails(carSalesOntology, hasPrice, automobile, dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()),
                    "hasPrice", "The sale price of the automobile.", baseIRI, List.of(IRI.create("http://dbpedia.org/resource/Price")));
            addDataPropertyDetails(carSalesOntology, hasCarId, automobile, dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()), "hasCarId",
                    "A unique identifier for the car.", baseIRI, List.of(IRI.create("http://dbpedia.org/resource/Vehicle_identification_number")));

            // Save ontology to an updated file
            File updatedOntology = new File("src/main/resources/extended-car-sales-ontology.owl");
            manager.saveOntology(carSalesOntology, IRI.create(updatedOntology.toURI()));
            System.out.println("Updated ontology saved.");

        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology: " + e.getMessage());
        }
    }

    public static void addSubclassAxiomToTheOntology(OWLClass childClass, OWLClass parentClass, OWLOntology ontology) {
        OWLAxiom subClassAxiom = dataFactory.getOWLSubClassOfAxiom(childClass, parentClass);

        manager.addAxiom(ontology, subClassAxiom);
    }

    public static void addAnnotations(OWLOntology ontology, OWLEntity classEntity, IRI baseIRI, String label, String comment, String seeAlsoURI) {
        // rdfs:label
        OWLAnnotation labelAnn = dataFactory.getOWLAnnotation(
                dataFactory.getRDFSLabel(),
                dataFactory.getOWLLiteral(label, "en")
        );

        // rdfs:comment
        OWLAnnotation commentAnn = dataFactory.getOWLAnnotation(
                dataFactory.getRDFSComment(),
                dataFactory.getOWLLiteral(comment, "en")
        );

        // rdfs:seeAlso
        OWLAnnotation seeAlsoAnn = dataFactory.getOWLAnnotation(
                dataFactory.getRDFSSeeAlso(),
                IRI.create(seeAlsoURI)
        );

        // rdfs:isDefinedBy
        OWLAnnotation isDefinedByAnn = dataFactory.getOWLAnnotation(
                dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI()),
                baseIRI
        );

        // Apply all annotations to the class
        for (OWLAnnotation annotation : new OWLAnnotation[]{labelAnn, commentAnn, seeAlsoAnn, isDefinedByAnn}) {
            OWLAxiom axiom = dataFactory.getOWLAnnotationAssertionAxiom(classEntity.getIRI(), annotation);
            // apply axiom to the ontology
            manager.addAxiom(ontology, axiom);
        }
    }

    public static void addObjectPropertyDetails(OWLOntology ontology, OWLObjectProperty property, OWLObjectProperty superProperty, Set<OWLClass> domainClasses, OWLClass rangeClass, String label, String comment, IRI isDefinedBy, List<IRI> seeAlsoIRIs) {
        // declare property to the ontology
        manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(property));

        // Sub property of attribute
        if (superProperty != null)
            manager.addAxiom(ontology, dataFactory.getOWLSubObjectPropertyOfAxiom(property, superProperty));
        // domain attribute (union if size is greater than one)
        if (domainClasses.size() == 1)
            manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyDomainAxiom(property, domainClasses.iterator().next()));
        else
            manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyDomainAxiom(property, dataFactory.getOWLObjectUnionOf(domainClasses)));

        // range attribute
        manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeClass));

        // Add annotations
        addCommonAnnotations(ontology, property.getIRI(), label, comment, isDefinedBy, seeAlsoIRIs);
    }

    public static void addDataPropertyDetails(OWLOntology ontology, OWLDataProperty property, OWLClass domainClass, OWLDatatype rangeDT, String label, String comment, IRI isDefinedBy, List<IRI> seeAlsoIRIs) {
        // declare property to the ontology
        manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(property));

        // declare property
        manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(property));

        // domain
        manager.addAxiom(ontology, dataFactory.getOWLDataPropertyDomainAxiom(property, domainClass));

        // range
        manager.addAxiom(ontology, dataFactory.getOWLDataPropertyRangeAxiom(property, rangeDT));

        // annotations
        addCommonAnnotations(ontology, property.getIRI(), label, comment, isDefinedBy, seeAlsoIRIs);
    }

    private static void addCommonAnnotations(OWLOntology ontology, IRI subject, String label, String comment, IRI isDefinedBy, List<IRI> seeAlsoIRIs) {
        // rdfs:label
        OWLAnnotation lbl = dataFactory.getOWLAnnotation(
                dataFactory.getRDFSLabel(),
                dataFactory.getOWLLiteral(label, "en"));
        manager.addAxiom(ontology,
                dataFactory.getOWLAnnotationAssertionAxiom(subject, lbl));

        // rdfs:comment
        OWLAnnotation cmt = dataFactory.getOWLAnnotation(
                dataFactory.getRDFSComment(),
                dataFactory.getOWLLiteral(comment, "en"));
        manager.addAxiom(ontology,
                dataFactory.getOWLAnnotationAssertionAxiom(subject, cmt));

        // rdfs:isDefinedBy
        OWLAnnotation def = dataFactory.getOWLAnnotation(
                dataFactory.getOWLAnnotationProperty(
                        OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI()),
                isDefinedBy);
        manager.addAxiom(ontology,
                dataFactory.getOWLAnnotationAssertionAxiom(subject, def));

        // rdfs:seeAlso
        for (IRI seeAlso : seeAlsoIRIs) {
            OWLAnnotation sa = dataFactory.getOWLAnnotation(
                    dataFactory.getRDFSSeeAlso(),
                    seeAlso);
            manager.addAxiom(ontology,
                    dataFactory.getOWLAnnotationAssertionAxiom(subject, sa));
        }
    }
}
