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
                + "?car a ex:Car ;  ex:soldBy ?dealer ;  ex:priceUSD ?price . \n"
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
                System.out.println("Name: " + dealerName.stringValue());
                System.out.println("Total Sales (USD): " + totalSales.stringValue());
                System.out.println("---");
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
                + "SELECT ?company (COUNT(?car) AS ?totalSales) (AVG(?price) AS ?averagePrice) WHERE {\n"
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
                //   Value averagePrice = bindSet.getValue("averagePrice");

                System.out.println("Company: " + company.stringValue());
                System.out.println("Average price of sold car: " + averagePrice.stringValue());
                System.out.println("Total sales: " + totalSales.stringValue());
                System.out.println("---");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void mostSoldCarModelPerCompany() {
        String queryString = PREFIXES
                + "SELECT ?company ?model (COUNT(?car) AS ?sales) WHERE {\n"
                + "     ?car a ex:Car ; \n"
                + "         ex:hasModel ?model .\n"
                + "      ?model ex:hasCompany ?company .\n"
                + "} \n"
                + "GROUP BY ?company ?model\n"
                + "ORDER BY ?company DESC(?sales)";

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
                System.out.println("---");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }

    public static void totalRevenuePerDealerAndRegion() {
        String queryString = PREFIXES
                + "SELECT ?dealer ?region (SUM(?price) AS ?totalRevenue) WHERE {\n"
                + "     ?car a ex:Car ; \n"
                + "       ex:priceUSD ?price ;\n"
                + "       ex:soldBy ?dealer .\n"
                + "  ?dealer ex:dealerName ?dealerName ;\n"
                + "          ex:locatedIn ?region .\n"
                + "} \n"
                + "GROUP BY ?dealer ?region\n"
                + "ORDER BY DESC(?totalRevenue)";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnection.getCarSalesRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);


        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindSet = result.next();
                Value dealer = bindSet.getValue("dealer");
                Value region = bindSet.getValue("region");
                Value totalRevenue = bindSet.getValue("totalRevenue");

                System.out.println("Dealer name: " + dealer.stringValue());
                System.out.println("Region of dealer: " + region.stringValue());
                System.out.println("Total revenue of dealer: " + totalRevenue.stringValue());
                System.out.println("---");
            }
        } catch (Exception e) {
            GraphDBConnection.closeRepositoryConnection();
        }
    }
}
