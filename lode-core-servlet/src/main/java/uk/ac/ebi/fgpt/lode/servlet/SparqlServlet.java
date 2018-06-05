package uk.ac.ebi.fgpt.lode.servlet;

import com.hp.hpl.jena.query.QueryParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;
import uk.ac.ebi.fgpt.lode.utils.TupleQueryFormats;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql xml");
        query(query, "XML", offset, limit, inference, request, response);
    }

    @RequestMapping (produces="application/sparql-results+json")
    public @ResponseBody
    void getSparqlJson(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {

        log.trace("querying for sparql json");

        query(query, "JSON", offset, limit, inference, request, response);
    }

    @RequestMapping (produces="text/csv")
    public @ResponseBody
    void getSparqlCsv(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql csv");

        query(query, "CSV", offset, limit, inference, request, response);
    }

    @RequestMapping (produces="text/tab-separated-values")
    public @ResponseBody
    void getSparqlTsv(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, LodeException, IOException {
        log.trace("querying for sparql tsv");
        query(query, "TSV", offset, limit, inference, request, response);
    }

    @RequestMapping (produces="application/rdf+xml")
    public @ResponseBody
    void getGraphXML(
            @RequestParam(value = "query", required = false) String query,
            HttpServletRequest request,
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
                    out,
                    request
            );
            out.close();
        }
    }

    @RequestMapping (produces="application/rdf+n3")
    public @ResponseBody
    void getGraphN3(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response,
            HttpServletRequest request) throws QueryParseException, LodeException, IOException {
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
                    out,
                    request
            );
            out.close();
        }
    }

    @RequestMapping (produces="application/rdf+json")
    public @ResponseBody
    void getGraphJson(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response,
            HttpServletRequest request) throws QueryParseException, LodeException, IOException {
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
                    out,
                    request
            );
            out.close();
        }
    }

    @RequestMapping (produces="text/plain")
    public @ResponseBody
    void getGraphNTriples(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response,
            HttpServletRequest request) throws QueryParseException, LodeException, IOException {
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
                    out,
                    request
            );
            out.close();
        }
    }

    @RequestMapping (produces="text/turtle")
    public @ResponseBody
    void getGraphTurtle(
            @RequestParam(value = "query", required = false) String query,
            HttpServletResponse response,
            HttpServletRequest request) throws QueryParseException, LodeException, IOException {
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
                    out,
                    request
            );
            out.close();
        }
    }

    /*The Post methods had the HttpServletRequest request initally, I added it to all the other functions*/
    @RequestMapping ( method = RequestMethod.POST, produces="application/sparql-results+xml", consumes = "application/x-www-form-urlencoded")
    public void postRequest (
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, IOException, LodeException {

        getSparqlXml (query, offset, limit, inference, request,response);
    }

    /*The Post methods had the HttpServletRequest request initally, I added it to all the other functions*/
    @RequestMapping ( method = RequestMethod.POST, produces="application/sparql-results+xml", consumes = "application/sparql-query")
    public void directPostRequest (
            @RequestBody String query,
            HttpServletRequest request,
            HttpServletResponse response) throws QueryParseException, IOException, LodeException {

        query (query, "XML", 0, null, false, request, response);
    }

    @RequestMapping
    public @ResponseBody
    void query(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "inference", required = false) boolean inference,
            HttpServletRequest request,
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
        //HERE WE COULD DO ADVANCED LOGGING TO FILE
        // Adding HttpServletRequest request as parameter to this query method might enable us to ask for IP
        // via request.getRemoteAddr() and similar methods, getting more information about the USER associated with a request

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
                log.info(outputFormat);
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
                out,
                request
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


    private String constructLogString(HttpServletRequest request){
        String logInfo = " HOST: " + request.getHeader("host") + " - USER-AGENT: " + request.getHeader("user-agent") + " - SESSION-ID: " + request.getSession().getId();
        return logInfo;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(QueryParseException.class)
    public @ResponseBody String handleQueryException(QueryParseException e, HttpServletRequest request) {
        getLog().error(e.getMessage().replace("\n"," ")+constructLogString(request), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LodeException.class)
    public @ResponseBody String handleLodException(LodeException e, HttpServletRequest request) {
        getLog().error(e.getMessage().replace("\n"," ")+constructLogString(request), e);
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public ResponseEntity<String> handleParameterException(MethodArgumentConversionNotSupportedException e) {
        getLog().error(e.getMessage(), e);
        String typeName = (e.getRequiredType()==Integer.class ? "integer" : e.getRequiredType().toString());
        String message = String.format("Parameter [%s] should be a [%s]", e.getName(), typeName);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParameterException(MissingServletRequestParameterException e) {
        getLog().error(e.getMessage(), e);
        String message = String.format("Parameter [%s] is required", e.getParameterName());
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LodeException.class)
    public ResponseEntity<String> handleLodException(LodeException e) {
        getLog().error(e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public @ResponseBody String handleIOException(IOException e, HttpServletRequest request) {
        getLog().error(e.getMessage().replace("\n"," ")+constructLogString(request), e);
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleException(Exception e, HttpServletRequest request) {
        getLog().error(request.toString());
        getLog().error(e.getMessage().replace("\n"," ")+constructLogString(request), e);
        return e.getMessage();
    }

    /**
     * All exception handlers call this method to assure that the Sparql endpoint returns text/plain.
     * This assures that the browser will not attempt to execute any malicious scripts in the URL,
     * and thus prevent XSS attacks.
     *
     * @param message A string message, formatted by the caller
     * @param status An HttpStatus code
     * @return Spring ResponseEntity<String> wrapping the string, content type, and status code.
     */
    private ResponseEntity<String> buildErrorResponse(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
        return new ResponseEntity<String>(message, headers, status);

    }

}
