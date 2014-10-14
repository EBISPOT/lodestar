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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.view.DepictionBean;

import javax.servlet.ServletOutputStream;
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

    protected Logger getLog() {
        return log;
    }



    @RequestMapping
    public @ResponseBody
    void describeResource (
            @RequestParam(value = "id", required = true ) String id,
            @RequestParam(value = "format", required = false ) String format,
            @RequestHeader(value = "X-Forwarded-Host", required = false) String forwarded_host,
            HttpServletResponse response) 
        throws IOException, LodeException 
    {
        log.info("In describeResource; id = '" + id + "', format = '" + format + 
                 "', forwarded_host = " + forwarded_host);

        if (id != null && id.length() > 0) {
            String query = "DESCRIBE <http://id.nlm.nih.gov/mesh/" + id + ">";

            String out_content_type = 
                format.equals("rdf") || format.equals("xml") ? "application/rdf+xml" :
                format.equals("n3")                          ? "application/rdf+n3" :
                format.equals("json")                        ? "application/rdf+json" :
                                                               "text/plain";
            response.setContentType(out_content_type);

            ServletOutputStream out = response.getOutputStream();
            String format_spec = 
                format.equals("rdf") || format.equals("xml") ? "RDF/XML" :
                format.equals("n3")                          ? "N3" :
                format.equals("json")                        ? "JSON-LD" :
                                                               "JSON-LD" ;
            getSparqlService().query(query, format_spec, false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty ID request: " + id));
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
