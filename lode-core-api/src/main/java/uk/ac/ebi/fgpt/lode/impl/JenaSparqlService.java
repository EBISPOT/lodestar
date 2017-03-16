/*
 * Copyright (c) 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;

import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.PathBlock;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.service.SparqlServiceDescription;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;
import uk.ac.ebi.fgpt.lode.utils.TupleQueryFormats;

import java.io.OutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaSparqlService implements SparqlService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${lode.sparql.query.maxlimit}")
    private int maxQueryLimit = -1;

    private SparqlServiceDescription sparqlServiceDescription;

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Integer getMaxQueryLimit() {
        return maxQueryLimit;
    }

    public void setMaxQueryLimit(Integer maxQueryLimit) {
        this.maxQueryLimit = maxQueryLimit;
    }

    private JenaQueryExecutionService queryExecutionService;

    public JenaQueryExecutionService getQueryExecutionService() {
        return queryExecutionService;
    }

    public void setQueryExecutionService(JenaQueryExecutionService queryExecutionService) {
        this.queryExecutionService = queryExecutionService;
    }


    public Collection<Element> getAllElements(Collection<Element> elements) {

        Collection<Element> elements1 = new HashSet<Element>();
        for (Element e : elements) {
            elements1.add(e);
            if (e instanceof ElementGroup) {
                elements1.addAll(getAllElements( ((ElementGroup) e).getElements()));
            }
            else if (e instanceof ElementSubQuery) {
                Query query =  ((ElementSubQuery) e).getQuery();
                elements1.addAll(getAllElements(Collections.singleton(query.getQueryPattern())));
            }
            else if (e instanceof  ElementService) {
                elements1.addAll(getAllElements( Collections.singleton( ((ElementService) e).getElement())));
            }
            else if (e instanceof  ElementOptional) {
                elements1.addAll(getAllElements(Collections.singleton( ((ElementOptional) e).getOptionalElement())));
            }
            else if (e instanceof  ElementUnion) {
                elements1.addAll(getAllElements(((ElementUnion) e).getElements()));
            }
        }
        return elements1;

    }

    public Collection<String> getNamedPredicates (Collection<Element> elements) throws QueryParseException {
        Collection<String> uris = new HashSet<String>();

        for (Element e : elements) {
            if (e instanceof ElementTriplesBlock) {
                ElementTriplesBlock tb = (ElementTriplesBlock) e;
                BasicPattern pattern = tb.getPattern();
                for (Triple t : pattern.getList()) {
                    if (t.getPredicate() != null && t.getPredicate().isURI()) {
                        uris.add(t.getPredicate().getURI());
                    }
                }
            }
            if (e instanceof ElementPathBlock) {
                ElementPathBlock epb = (ElementPathBlock) e ;

                PathBlock pb = epb.getPattern();
                for (TriplePath t : pb.getList()) {
                    if (t.getPredicate() != null && t.getPredicate().isURI()) {
                        uris.add(t.getPredicate().getURI());
                    }
                }
            }
        }
        return uris;
    }

    public  boolean containsOptionalElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {  if (e instanceof ElementOptional) { return true;} } return false;
    }

    public  boolean containsBindElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementBind) { return true;}   }   return false;
    }

    public  boolean containsFilterElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementFilter) {   return true;    }   }   return false;
    }

    public  boolean containsGroupElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementGroup) {    return true;    }   }   return false;
    }

    public  boolean containsServiceElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementService) {  return true;    }   }   return false;
    }

    public  boolean containsSubqueryElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementSubQuery) { return true;    }   }   return false;
    }

    public  boolean containsUnionElement (Collection<Element> elements) throws QueryParseException {
        for (Element e : elements) {    if (e instanceof ElementUnion) {    return true;    }   }   return false;
    }

    public void query(String query, String format, Integer offset, Integer limit, boolean inference, OutputStream output, HttpServletRequest request) throws LodeException {
        Collection<Element> elements = new HashSet<Element>();
        //From JenaSparqlQueryParser
        Query squery=QueryFactory.create(query);
        Element element = squery.getQueryPattern();
        elements = getAllElements(Collections.singleton(element));
        String jenaLog=" JENA";

        //getSelectedVariableCount
        jenaLog+=" SelectedVariableCount: "+squery.getResultVars().size();

        //getVariablesinQueryCount
        jenaLog+=" VariablesInQueryCount: "+squery.getProjectVars().size();

        //getQueryPathSize
        int size=0;
        for (Element e : elements) {
            if (e instanceof ElementTriplesBlock) {
                ElementTriplesBlock tb = (ElementTriplesBlock) e;
                BasicPattern pattern = tb.getPattern();
                size += pattern.getList().size();
            }
            if (e instanceof ElementPathBlock) {
                ElementPathBlock epb = (ElementPathBlock) e ;

                PathBlock pb = epb.getPattern();
                size += pb.getList().size();
            }
        }
        jenaLog+=" QueryPathSize: "+size;

        //
        jenaLog+=" namedPredicates: "+getNamedPredicates(elements);

        //containsOptionalElement
        jenaLog+=" optionalElement: "+containsOptionalElement(elements);
        jenaLog+=" unionElement: "+containsUnionElement(elements);
        jenaLog+=" subqueryElement: "+containsSubqueryElement(elements);
        jenaLog+=" bindElement: "+containsBindElement(elements);
        jenaLog+=" filterElement: "+containsFilterElement(elements);
        jenaLog+=" serviceElement: "+containsServiceElement(elements);

        //Logging NamedGraph and Graph URI in one field
        List<String> list1 = squery.getGraphURIs();
        List<String> list2=squery.getNamedGraphURIs();
        list1.addAll(list2);

        Set<String> s = new LinkedHashSet<String>(list1);   //To get rid of duplicated entries
        list1.clear();  //
        list2.clear();  //Given
        list1.addAll(s);
        jenaLog+=" usedGraphs: "+list1.toString();

        //This print out all headers we get. Unfortunatley there is no forward header to get us the 'real IP address' of the request
        //Enumeration<String> x=request.getHeaderNames();
        //while (x.hasMoreElements()){
        //   String tmpelement=x.nextElement().toString();
        //    log.info(tmpelement);
        //    log.info(" ... "+request.getHeader(tmpelement));
        //}

        String logInfo;
        if (request!=null) {
           // logInfo = " HOST: " + request.getHeader("host") + " - USER-AGENT: " + request.getHeader("user-agent") + " - SESSION-ID: " + request.getSession().getId() + jenaLog;
            //IF the query ends with the noLog tag, then do not write into logfile
            if (!query.endsWith("#noLog"))
            {
                String address;
                try{
                    address=request.getAttribute("X-Cluster-Client-Ip").toString();
                }
                catch(Exception e){

                log.info("Could not find X-Cluster-Client-IP so I go with remote Addr");
                address=request.getRemoteAddr();
                }


                log.info(address);

                logInfo = " HOST: " + address + " - USER-AGENT: " + request.getHeader("user-agent") + " - SESSION-ID: " + request.getSession().getId() + jenaLog;
            }
            else{
                logInfo="Query followed by the noLog flag";
            }
        }
        else
        {
            logInfo = "There was no request class";
        }

        try {

            Query q1 = QueryFactory.create(query, Syntax.syntaxARQ);
            QueryType qtype = getQueryType(query);

            // detect format

            if (qtype.equals(QueryType.TUPLEQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = TupleQueryFormats.XML.toString();
                }
                executeTupleQuery(q1, format, offset, limit, inference, output, logInfo);
            }
            else if (qtype.equals(QueryType.DESCRIBEQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = GraphQueryFormats.RDFXML.toString();
                }
                executeDescribeQuery(q1, format, output, logInfo);
            }
            else if (qtype.equals(QueryType.CONSTRUCTQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = GraphQueryFormats.RDFXML.toString();
                }
                executeConstructQuery(q1, format, output, logInfo);
            }
            else if (qtype.equals((QueryType.BOOLEANQUERY))) {
                if (isNullOrEmpty(format)) {
                    format = TupleQueryFormats.XML.toString();
                }
                executeBooleanQuery(q1, format, output, logInfo);
            }
            else {
                // unknown query type
                log.error("Invalid query type: " + query);
                throw new LodeException("Invalid query type, must be one of TUPLE, DESCRIBE, CONSTRUCT or BOOLEAN");
            }


        } catch (QueryParseException e) {
            throw new LodeException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LodeException(e.getMessage());
        }
    }

    public void query(String query, String format, Integer offset, Integer limit, boolean inference, OutputStream output) throws LodeException {
        query(query, format, offset, limit, inference, output, null);

    }

    public void query(String query, String format, boolean inference, OutputStream output) throws LodeException {
        query(query, format, 0, getMaxQueryLimit(), inference, output);
    }

    public void query(String query, String format, boolean inference, OutputStream output, HttpServletRequest request) throws LodeException {
        query(query, format, 0, getMaxQueryLimit(), inference, output, request);
    }

    public void getServiceDescription(OutputStream outputStream, String format) {

        // todo implement this properly
        String  q = "CONSTRUCT  {\n" +
                "?s ?p ?o\n" +
                "}\n" +
                "WHERE {\n" +
                "?s a <http://www.w3.org/ns/sparql-service-description#Service> .\n" +
                "?s ?p ?o \n" +
                "}";
        Query q1 = QueryFactory.create(q);
        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();
        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model m = endpoint.execConstruct();

            if (!m.listStatements().hasNext()) {
                //            Model model = ModelFactory.createDefaultModel();

            }
            else {
                m.write(outputStream, format);
            }
            endpoint.close();
            if (g!=null) {
                g.close();
            }
        } catch (LodeException e) {
            log.error(e.getMessage(), e);
        }

    }

    public QueryType getQueryType(String query) {
        // detect query type
        Query q1 = QueryFactory.create(query, Syntax.syntaxARQ);

        return QueryType.getQueryType(q1);

    }

    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        }
        return "".equals(o);
    }


    private void executeConstructQuery(Query q1, String format, OutputStream output, String logInfo) {
        long startTime = System.currentTimeMillis();
        //log.info("preparing to execute describe query: " + startTime+ "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g=getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model model = endpoint.execConstruct();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            //log.info("Construct " + q1.serialize().replace("\n", " ") + " - elapsedTime " + elapsedTime + logInfo);
            model.write(output, format);
            model.close();

        }  catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }

    }

    public Query setLimits (Query q1, Integer limit) {
        if (getMaxQueryLimit() > -1) {
            // if a limit is submitted as a parameter, check if there is one in the query
            if (limit != null) {
                if (q1.hasLimit()) {
                    // if the limit in the query is less than the parameter and max, all is ok
                    if (q1.getLimit() < limit && q1.getLimit() < getMaxQueryLimit()) {
                        return q1;
                    }
                    else if (limit < getMaxQueryLimit()) {
                        q1.setLimit(limit);
                    }
                    else  {
                        q1.setLimit(getMaxQueryLimit());
                    }
                }
                else {
                    if (limit < getMaxQueryLimit()) {
                        q1.setLimit(limit);
                    }
                    else {
                        q1.setLimit(getMaxQueryLimit());
                    }
                }
            }
            else if (q1.hasLimit()) {
                if (q1.getLimit() > getMaxQueryLimit()) {
                    q1.setLimit(getMaxQueryLimit());
                }
            }
            else {
                q1.setLimit(getMaxQueryLimit());
            }
        }
        else {
            if (limit!= null &&limit >-1) {
                q1.setLimit(limit);
            }
        }
        return q1;

    }

    private void executeTupleQuery(Query q1, String format, Integer offset, Integer limit, boolean inference, OutputStream output)
    {
        executeTupleQuery(q1,format,offset, limit,inference, output, null);
    }

    private void executeTupleQuery(Query q1, String format, Integer offset, Integer limit, boolean inference, OutputStream output, String logInfo)  {
        // check the limit is not greater that the max

        q1 = setLimits(q1, limit);
        // set any offset
        if (offset != null) {
            q1.setOffset(offset);
        }

        long startTime = System.currentTimeMillis();
        QueryExecution endpoint = null;
        Graph g=getQueryExecutionService().getDefaultGraph();

        try {

            endpoint = getQueryExecutionService().getQueryExecution(g, q1, inference);
            ResultSet results = endpoint.execSelect();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (logInfo!=null) {
                log.info("Query " + q1.serialize().replace("\n", " ") + " - elapsedTime " + elapsedTime + logInfo);
            }

            if (format.equals(TupleQueryFormats.JSON.toString())) {
                ResultSetFormatter.outputAsJSON(output, results);
            }
            else if (format.equals(TupleQueryFormats.CSV.toString())) {
                ResultSetFormatter.outputAsCSV(output, results);
            }
            else if (format.equals(TupleQueryFormats.TSV.toString())) {
                ResultSetFormatter.outputAsTSV(output, results);
            }
            else {
                ResultSetFormatter.outputAsXML(output, results);
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }

    }

    private void executeDescribeQuery(Query q1, String format, OutputStream output)  {
            executeDescribeQuery(q1,format,output, null);
    }

    private void executeDescribeQuery(Query q1, String format, OutputStream output, String logInfo)  {

        long startTime = System.currentTimeMillis();

        QueryExecution endpoint = null;
        Graph g=getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model model = endpoint.execDescribe();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (logInfo!=null) {
                logInfo=logInfo.substring(0,logInfo.indexOf("JENA"));
                logInfo="Describe " + q1.serialize() + " - elapsedTime " + elapsedTime + logInfo;
                log.info(logInfo.replace("\n", " "));
            }
            model.write(output, format);
            model.close();

        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }
    }


    private void executeBooleanQuery(Query q1, String format, OutputStream output, String logInfo) {

        long startTime = System.currentTimeMillis();
        //log.info("preparing to execute ASK query: " + startTime+ "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g=getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            boolean value = endpoint.execAsk();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            //log.info("ASK query" +  startTime+ " finished in :" + elapsedTime  + " milliseconds");
            logInfo=logInfo.substring(0, logInfo.indexOf("JENA"));
            log.info("ASK " + q1.serialize().replace("\n", " ") + " - elapsedTime " + elapsedTime + logInfo);

            if (format.equals(TupleQueryFormats.JSON.toString())) {
                ResultSetFormatter.outputAsJSON(output, value);
            }
            else if (format.equals(TupleQueryFormats.CSV.toString())) {
                ResultSetFormatter.outputAsCSV(output, value);
            }
            else if (format.equals(TupleQueryFormats.TSV.toString())) {
                ResultSetFormatter.outputAsTSV(output, value);
            }
            else {
                ResultSetFormatter.outputAsXML(output, value);
            }
//            output.flush();
//            output.close();
        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }
    }







}
