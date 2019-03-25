import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.impl.ResourceImpl;

import junit.framework.TestCase;

/**
 * @author Simon Jupp
 * @date 25/05/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestParameterizedSparqlString extends TestCase {


    public void testParameterizedSparqlString () {

        Query query = QueryFactory.create("SELECT * WHERE {?s ?p ?o}");

        QuerySolutionMap initialBinding = new QuerySolutionMap();
        initialBinding.add("s", new ResourceImpl("http://www.example.org"));

        ParameterizedSparqlString sparql = new ParameterizedSparqlString(query.toString(), initialBinding);
        System.out.println(sparql);
        assertTrue(sparql.toString().contains("http://www.example.org"));


    }

}
