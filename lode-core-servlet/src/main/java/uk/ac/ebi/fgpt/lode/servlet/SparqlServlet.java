package uk.ac.ebi.fgpt.lode.servlet;



import com.hp.hpl.jena.query.QueryParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;
import uk.ac.ebi.fgpt.lode.utils.TupleQueryFormats;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
@Controller
@RequestMapping("/query")
public class SparqlServlet {

    private Logger log = LoggerFactory.getLogger(getClass());

    public SparqlService getSparqlService() {
        return sparqlService;
    }

    @Autowired
    public void setSparqlService(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    private SparqlService sparqlService;

    protected Logger getLog() {
        return log;
    }

    @RequestMapping (produces="application/sparql-results+xml")
    public @ResponseBody
    void getSparqlXml(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,

            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql xml");
        query(query, "XML", offset, limit, inference, response);
    }

    @RequestMapping (produces="application/sparql-results+json")
    public @ResponseBody
    void getSparqlJson(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,

            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql json");
        query(query, "JSON", offset, limit, inference, response);
    }

    @RequestMapping (produces="text/csv")
    public @ResponseBody
    void getSparqlCsv(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,

            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql csv");
        query(query, "CSV", offset, limit, inference, response);
    }

    @RequestMapping (produces="text/tab-separated-values")
    public @ResponseBody
    void getSparqlTsv(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,

            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql tsv");
        query(query, "TSV", offset, limit, inference, response);
    }

    @RequestMapping (produces="application/rdf+xml")
    public @ResponseBody
    void getGraphXML(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for graph rdf+xml");
        ServletOutputStream out = response.getOutputStream();
        if (query == null) {
            response.setContentType("application/rdf+xml; charset=utf-8");
            sparqlService.getServiceDescription(out, "RDF/XML");
        }
        else {
            getSparqlService().query(
                    query,
                    "RDF/XML",
                    false,
                    out
            );
            out.close();
        }
    }

    @RequestMapping (produces="application/rdf+n3")
    public @ResponseBody
    void getGraphN3(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for graph rdf+n3");
        ServletOutputStream out = response.getOutputStream();
        if (query == null) {
            response.setContentType("application/rdf+n3; charset=utf-8");
            sparqlService.getServiceDescription(out, "N3");
        }
        else {
            getSparqlService().query(
                    query,
                    "N3",
                    false,
                    out
            );
            out.close();
        }
    }

    @RequestMapping (produces="application/rdf+json")
    public @ResponseBody
    void getGraphJson(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for graph rdf+json");
        ServletOutputStream out = response.getOutputStream();
        if (query == null) {
            response.setContentType("application/rdf+json; charset=utf-8");
            sparqlService.getServiceDescription(out, "JSON-LD");
        }
        else {
            getSparqlService().query(
                    query,
                    "JSON-LD",
                    false,
                    out
            );
            out.close();
        }
    }

    @RequestMapping (produces="text/plain")
    public @ResponseBody
    void getGraphNTriples(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for graph text/plain (rdf+ntriples)");
        response.setContentType("text/plain; charset=utf-8");
        ServletOutputStream out = response.getOutputStream();
        if (query == null) {
            getSparqlService().getServiceDescription(out, "N-TRIPLES");
        }
        else {
            getSparqlService().query(
                    query,
                    "N-TRIPLES",
                    false,
                    out
            );
            out.close();
        }
    }

    @RequestMapping (produces="text/turtle")
    public @ResponseBody
    void getGraphTurtle(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for graph turtle");
        ServletOutputStream out = response.getOutputStream();
        if (query == null) {
            response.setContentType("text/turtle; charset=utf-8");
            sparqlService.getServiceDescription(out, "TURTLE");
        }
        else {
            getSparqlService().query(
                    query,
                    "TURTLE",
                    false,
                    out
            );
            out.close();
        }
    }

    @RequestMapping ( method = RequestMethod.POST, produces="application/sparql-results+xml", consumes = "application/x-www-form-urlencoded")
    public void postRequest (
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, IOException, LodeException {

        getSparqlXml (query, offset, limit, inference, response);
    }


    @RequestMapping ( method = RequestMethod.POST, produces="application/sparql-results+xml", consumes = "application/sparql-query")
    public void directPostRequest (
            @RequestBody String query,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, IOException, LodeException {

        query (query, "XML", 0, null, false, response);
    }

    @RequestMapping
    public @ResponseBody
    void query(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {

        ServletOutputStream out = response.getOutputStream();

        if (query == null) {
            // Default to format N3 unless an acceptable format is given
            if (format == null || !(format.equals("TURTLE")
                    || format.equals("N-TRIPLES")
                    || format.equals("JSON-LD")
                    || format.equals("N3")
                    || format.equals("RDF/XML"))) {
                response.setContentType("text/plain; charset=utf-8");
                sparqlService.getServiceDescription(out, "N3");
            } else {
                response.setContentType( getMimeType(format));
                sparqlService.getServiceDescription(out, format);
            }
            out.close();
            return;
        }

        log.info("Processing raw query:\n" + query + "\nEnd of query.");
        // if no format, try and work out the query type
        String outputFormat = format;
        if (outputFormat == null) {
            QueryType qType = getSparqlService().getQueryType(query);
            if (qType.equals(QueryType.BOOLEANQUERY) || qType.equals(QueryType.TUPLEQUERY)) {
                log.debug("no format, tuple query: setting format to XML");
                outputFormat = TupleQueryFormats.XML.toString();
            }
            else if (qType.equals(QueryType.CONSTRUCTQUERY) || qType.equals(QueryType.DESCRIBEQUERY)) {
                outputFormat = GraphQueryFormats.RDFXML.toString();
            }
            else {
                response.setStatus(406);
                response.setContentType("text/plain; charset=utf-8");
                out.println("406 Not Acceptable: Can't handle this query type");
                out.println("Supported queries are BOOLEAN, SELECT, CONSTRUCT, DESCRIBE, ASK and SERVICE");
                out.close();
                return;
            }

        }

        if (validFormat(outputFormat)) {
            response.setContentType( getMimeType(outputFormat) );
            getSparqlService().query(
                query,
                outputFormat.toUpperCase(),
                offset,
                limit,
                inference,
                out
            );
            out.close();
        }
        else {
            response.setStatus(406);
            response.setContentType("text/plain");
            out.println("406 Not Acceptable: The requested data format is not supported.");
            out.println("Must be one of XML, JSON, RDF/XML, TURTLE or N3");
            out.close();
            return;
        }
    }

    private boolean validFormat(String format) {

        for (GraphQueryFormats gf : GraphQueryFormats.values()) {
            if (format.toUpperCase().equals(gf.toString())) {
                return true;
            }
        }

        for (TupleQueryFormats tf : TupleQueryFormats.values()) {
            if (format.toUpperCase().equals(tf.toString())) {
                return true;
            }
        }
        return false ;

    }

    private String getMimeType(String format) {

        for (GraphQueryFormats gf : GraphQueryFormats.values()) {
            if (format.toUpperCase().equals(gf.toString())) {
                return gf.toMimeType()+"; charset=utf-8";
            }
        }

        for (TupleQueryFormats tf : TupleQueryFormats.values()) {
            if (format.toUpperCase().equals(tf.toString())) {
                return tf.toMimeType()+"; charset=utf-8";
            }
        }

        return "text/plain; charset=utf-8" ;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(QueryParseException.class)
    public @ResponseBody String handleQueryException(QueryParseException e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LodeException.class)
    public @ResponseBody String handleLodException(LodeException e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public @ResponseBody String handleIOException(IOException e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleException(Exception e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }
}
