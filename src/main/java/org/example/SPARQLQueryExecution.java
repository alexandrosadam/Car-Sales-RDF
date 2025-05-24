package org.example;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class SPARQLQueryExecution {
    private static final String PREFIXES = "PREFIX ex: <http://example.com/cars#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

    public static void totalSalesPerDealer() {
        String queryString = PREFIXES
                + "SELECT ?dealer ?dealerName (SUM(?price) AS ?totalSalesUSD) WHERE { \n"
                + "?car a ex:Car ;\n"
                + "     ex:soldBy ?dealer ; \n"
                + "     ex:priceUSD ?price . \n"
                + "?dealer ex:dealerName ?dealerName . } \n"
                + "GROUP BY ?dealer ?dealerName \n"
                + "ORDER BY DESC(?totalSalesUSD)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value dealer = bindSet.getValue("dealer");
                Value dealerName = bindSet.getValue("dealerName");
                Value totalSales = bindSet.getValue("totalSalesUSD");

                System.out.println("Dealer: " + dealer.stringValue());
                System.out.println("Dealer name: " + dealerName.stringValue());
                System.out.println("Total Sales (USD): " + totalSales.stringValue());
                System.out.println("=========================================");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void countManualAndAutomaticPerDealer() {
        String queryString = PREFIXES
                + "SELECT ?dealer \n"
                + "     (SUM(IF(LCASE(STRAFTER(STR(?transmission), \"transmission/\")) = \"auto\", 1, 0)) AS ?AutoCarCount) \n"
                + "     (SUM(IF(LCASE(STRAFTER(STR(?transmission), \"transmission/\")) = \"manual\", 1, 0)) AS ?ManualCarCount) \n"
                + "WHERE { \n"
                + " ?car a ex:Car ; \n"
                + "      ex:soldBy ?dealer ;\n"
                + "      ex:hasTransmission ?transmission . \n"
                + "} \n"
                + "GROUP BY ?dealer";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);


        System.out.println("=====================================\n");

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value dealer = bindSet.getValue("dealer");
                Value autoCarCount = bindSet.getValue("AutoCarCount");
                Value manualCarCount = bindSet.getValue("ManualCarCount");

                System.out.println("Dealer: " + dealer.stringValue());
                System.out.println("Auto car count: " + autoCarCount.stringValue());
                System.out.println("Manual car count: " + manualCarCount.stringValue());
                System.out.println("---");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void totalSalesAndAveragePricePerCompany() {
        String queryString = PREFIXES
                + "SELECT ?company (COUNT(?car) AS ?totalSales) (ROUND(AVG(xsd:integer(?price))) AS ?averagePrice) WHERE {\n"
                + "     ?car a ex:Car ;\n"
                + "         ex:hasModel ?model ;\n"
                + "         ex:priceUSD ?price .\n"
                + "      ?model a ex:CarModel ;\n"
                + "         ex:hasCompany ?company .\n"
                + "}\n"
                + "GROUP BY ?company\n"
                + "ORDER BY DESC(?totalSales)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

        System.out.println("=====================================\n");

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value company = bindSet.getValue("company");
                Value totalSales = bindSet.getValue("totalSales");
                Value averagePrice = bindSet.getValue("averagePrice");

                System.out.println("Company: " + company.stringValue());
                System.out.println("Average price of sold car: " + averagePrice.stringValue());
                System.out.println("Total sales: " + totalSales.stringValue());
                System.out.println("=========================================");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void mostSoldCarModelPerCompany() {
        String queryString = PREFIXES
                + "SELECT ?company ?model ?sales WHERE {\n"
                + "{\n"
                + "SELECT ?company ?model (COUNT(?car) AS ?sales) WHERE {\n"
                + "     ?car a ex:Car ; \n"
                + "         ex:hasModel ?model ;\n"
                + "         ex:manufacturedBy ?company .\n"
                + "      ?model ex:hasCompany ?company .\n"
                + "} \n"
                + "GROUP BY ?company ?model\n"
                + "}\n"
                + "FILTER NOT EXISTS {\n"
                + "{\n"
                + "SELECT ?company (COUNT(?car2) AS ?higherSales) WHERE {\n"
                + "         ?car2 a ex:Car ;\n"
                + "             ex:hasModel ?otherModel ;\n"
                + "             ex:manufacturedBy ?company .\n"
                + "          ?otherModel ex:hasCompany ?company . \n"
                + "          FILTER(?otherModel != ?model) \n"
                + "}\n"
                + "          GROUP BY ?company ?otherModel\n"
                + "          HAVING (?higherSales > ?sales)\n"
                + "          }\n"
                + "     }\n"
                + "}\n"
                + "ORDER BY DESC(?sales)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

        System.out.println("=====================================\n");

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value company = bindSet.getValue("company");
                Value model = bindSet.getValue("model");
                Value sales = bindSet.getValue("sales");

                System.out.println("Company: " + company.stringValue());
                System.out.println("Most sold model: " + model.stringValue());
                System.out.println("Total sales: " + sales.stringValue());
                System.out.println("=====================================\n");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void totalRevenuePerDealerAndRegions() {
        String queryString = PREFIXES
                + "SELECT ?dealer ?dealerName \n"
                + "     (GROUP_CONCAT(DISTINCT ?region; separator=\", \") AS ?regions)"
                + "     (SUM(xsd:integer(?price)) AS ?totalRevenue) WHERE {\n"
                + "     ?car a ex:Car ; \n"
                + "       ex:priceUSD ?price ;\n"
                + "       ex:soldBy ?dealer .\n"
                + "  ?dealer ex:dealerName ?dealerName ;\n"
                + "          ex:locatedIn ?region .\n"
                + "} \n"
                + "GROUP BY ?dealer ?dealerName\n"
                + "ORDER BY DESC(?totalRevenue)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);


        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value dealer = bindSet.getValue("dealer");
                Value dealerName = bindSet.getValue("dealerName");
                Value regions = bindSet.getValue("regions");
                Value totalRevenue = bindSet.getValue("totalRevenue");

                System.out.println("Dealer: " + dealer.stringValue());
                System.out.println("Dealer name: " + dealerName.stringValue());
                System.out.println("Regions: " + regions.stringValue());
                System.out.println("Total revenue of dealer: " + totalRevenue.stringValue());
                System.out.println("=====================================\n");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void findTopFiveCompaniesWithTheMostSoldCars() {
        String queryString = PREFIXES
                + "SELECT ?company (COUNT(?car) AS ?totalSales) WHERE {\n"
                + "     ?car a ex:Car ; \n"
                + "         ex:manufacturedBy ?company  .\n"
                + "} \n"
                + "GROUP BY ?company \n"
                + "ORDER BY DESC(?totalSales)"
                + "LIMIT 5";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

        System.out.println("=====================================\n");

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value company = bindSet.getValue("company");
                Value totalSales = bindSet.getValue("totalSales");

                System.out.println("Company: " + company.stringValue());
                System.out.println("Total sales: " + totalSales.stringValue());
                System.out.println("=====================================\n");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void countMaleAndFemaleCustomers() {
        String queryString = PREFIXES
                + "SELECT ?gender (COUNT(DISTINCT ?customer) AS ?totalCustomers) WHERE {\n"
                + "     ?customer a ex:Customer ;\n"
                + "         ex:gender ?gender .\n"
                + "} \n"
                + "GROUP BY ?gender \n"
                + "ORDER BY DESC(?totalCustomers)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

        System.out.println("=====================================\n");

        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value gender = bindSet.getValue("gender");
                Value totalCustomers = bindSet.getValue("totalCustomers");

                System.out.println("Gender: " + gender.stringValue());
                System.out.println("Total customers: " + totalCustomers.stringValue());
                System.out.println("=====================================\n");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }
}
