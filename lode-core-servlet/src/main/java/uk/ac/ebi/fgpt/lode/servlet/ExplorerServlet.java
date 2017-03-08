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

package uk.ac.ebi.fgpt.lode.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.view.DepictionBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 26/04/2013
 * Functional Genomics Group EMBL-EBI
 */
@Controller
@RequestMapping("/explore")
public class ExplorerServlet {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ExplorerViewConfiguration configuration;
    private ExploreService service;
    private SparqlService sparqlService;

   //@Value("${lode.explorer.service.baseuri}")
   @Value("http://rdf-hh-01.ebi.ac.uk:8890/sparql")
    private URI baseUri;

    public SparqlService getSparqlService() {
        return sparqlService;
    }

    @Autowired
    public void setSparqlService(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    public ExploreService getService() {
        return service;
    }

    @Autowired
    public void setService(ExploreService service) {
        this.service = service;
    }

    public ExplorerViewConfiguration getConfiguration() {
        return configuration;
    }

    @Autowired
    public void setConfiguration(ExplorerViewConfiguration configuration) {
        this.configuration = configuration;
    }

    public URI getBaseUri() {
        return this.baseUri;
    }

    public void setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
    }

    protected Logger getLog() {
        return log;
    }

    protected URI resolveUri(String reluri) {
        if (reluri == null || reluri.length() == 0) {
            return null;
        }
        else if (baseUri != null) {
            return baseUri.resolve(reluri);
        }
        else {
            return URI.create(reluri);
        }
    }

    @RequestMapping (produces="application/rdf+xml")
    public @ResponseBody
    void describeResourceAsXml (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        URI absuri = this.resolveUri(uri);
        if (absuri != null) {
            String query = "DESCRIBE <" + absuri + ">";
            response.setContentType("application/rdf+xml; charset=utf-8");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "RDF/XML", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty ID request: " + uri));
        }
    }

    @RequestMapping (produces="text/n3")
    public @ResponseBody
    void describeResourceAsN3 (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        URI absuri = resolveUri(uri);
        if (absuri != null) {
            String query = "DESCRIBE <" + absuri + ">";
            log.info("querying for graph n3");
            response.setContentType("text/n3; charset=utf-8");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "N3", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping (produces="text/turtle")
    public @ResponseBody
    void describeResourceAsTurtle (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        URI absuri = this.resolveUri(uri);
        if (absuri != null) {
            String query = "DESCRIBE <" + absuri + ">";
            log.info("querying for graph turtle");
            response.setContentType("text/turtle; charset=utf-8");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "TURTLE", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping (produces="application/rdf+json")
    public @ResponseBody
    void describeResourceAsJson (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        URI absuri = resolveUri(uri);
        if (absuri != null) {
            String query = "DESCRIBE <" + absuri + ">";
            log.info("querying for graph rdf+n3");
            response.setContentType("application/rdf+json; charset=utf-8");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "JSON-LD", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping (produces="text/plain")
    public @ResponseBody
    void describeResourceAsNtriples (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        URI absuri = resolveUri(uri);
        if (absuri != null) {
            String query = "DESCRIBE <" + absuri + ">";
            log.info("querying for graph rdf+ntriples");
            response.setContentType("text/plain; charset=utf-8");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "N-TRIPLES", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping
    public @ResponseBody
    void describeResource (
            @RequestParam(value = "uri", required = true ) String uri,
            @RequestParam(value = "format", required = false ) String format,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException, LodeException {

        if (format == null) {
            describeResourceAsNtriples(uri, response);
        } else if (format.toLowerCase().equals("rdf") ||
                   format.toLowerCase().equals("xml") ||
                   format.toLowerCase().equals("rdf/xml")) {
            describeResourceAsXml(uri, response);
        }
        else if (format.toLowerCase().equals("n3")) {
            describeResourceAsN3(uri, response);
        }
        else if (format.toLowerCase().equals("ttl") || format.toLowerCase().equals("turtle")) {
            describeResourceAsTurtle(uri, response);
        }
        else if (format.toLowerCase().equals("json") || format.toLowerCase().equals("json-ld")) {
            describeResourceAsJson(uri, response);
        }
        else {
            describeResourceAsNtriples(uri, response);
        }
    }

    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public ModelAndView describeResourceAsHtml(
            @RequestParam(value = "uri", required = true ) String uri,
            @RequestParam(value = "resource_prefix", required = false) String resource_prefix) throws LodeException {
        URI absuri = resolveUri(uri);
        if (absuri != null) {
            ModelAndView mv = new ModelAndView();
            mv.setViewName("explore");
            if (resource_prefix == null) {
                resource_prefix = "";
            }
            mv.addObject("uri", absuri.toString());
            mv.addObject("resource_prefix", resource_prefix);
            return mv;
        } else {
            throw new LodeException("Malformed or empty URI request: " + uri);
        }
    }

    @RequestMapping(value = "/resourceTypes", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getTypesWithLabelsAndDescription(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            return getService().getTypes(
                    absuri,
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes()
                    );
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/resourceAllTypes", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getAllTypesWithLabelsAndDescription(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            return getService().getAllTypes(
                    absuri,
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes()
                    );
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/relatedToObjects", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getRelatedToObjects(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

         URI absuri = resolveUri(uri);
         if (absuri != null) {
            // get the relationships to ignore
            Set<URI> ignoreProps = getConfiguration().getIgnoreRelationships();
            ignoreProps.addAll(getConfiguration().getTopRelationships());

            return getService().getRelatedToObjects(
                    absuri,
                    ignoreProps,
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/resourceShortDescription", method = RequestMethod.GET)
    public @ResponseBody ShortResourceDescription getShortResourceDescritption(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().info("Getting short description for: " + uri);

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            return getService().getShortResourceDescription(
                    absuri,
                    getConfiguration().getLabelRelations(),
                    getConfiguration().getDescriptionRelations()
            );
        }
        else {
            return new ShortResourceDescription(uri, uri, null, null);
        }
    }

    @RequestMapping(value = "/resourceDepictions", method = RequestMethod.GET)
    public @ResponseBody
    Collection<DepictionBean> getShortResourceDepiction(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().info("Getting image urls for: " + uri);
        Set<DepictionBean> dps= new HashSet<DepictionBean>();

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            for (String u :  getService().getResourceDepiction(
                    absuri,
                    getConfiguration().getDepictRelation())) {
                dps.add(new DepictionBean(u));
            }
            return dps;
        }
        else {
            return Collections.emptySet();
        }
    }

    @RequestMapping(value = "/resourceTopObjects", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getTopRelatedResourceByProperty(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().info("Getting top objects for: " + uri);

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            Set<URI> toprelations = new LinkedHashSet<URI>(getConfiguration().getTopRelationships());
            return getService().getRelatedResourceByProperty(
                    absuri,
                    toprelations,
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping("/relatedFromSubjects")
    public @ResponseBody Collection<RelatedResourceDescription> getRelatedFromSubjects(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        URI absuri = resolveUri(uri);
        if (absuri != null) {
            return getService().getRelatedFromSubjects(
                    absuri,
                    new HashSet<URI>(),
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleBadUriException(Exception e)  {
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
}
