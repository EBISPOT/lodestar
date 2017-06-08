

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

var activateQueryHistory=true;

var exampleQueries = [
    {
        category: "Biomodels",
        queries: [
            {
                shortname: "Query 1",
                description: "List all species identifers and their names in model http://identifiers.org/biomodels.db/BIOMD0000000001",
                query:
                "PREFIX sbmlrdf: <http://identifiers.org/biomodels.vocabulary#>\n\n" +
                "SELECT ?speciesid ?name WHERE {\n" +
                "<http://identifiers.org/biomodels.db/BIOMD0000000001> sbmlrdf:species ?speciesid .\n" +
                "?speciesid sbmlrdf:name ?name}"
            },
            {
                shortname: "Query 2",
                description: "Get element annotations of the model http://identifiers.org/biomodels.db/BIOMD0000000001",
                query:
                "PREFIX sbmlrdf: <http://identifiers.org/biomodels.vocabulary#>\n\n" +
                "SELECT ?element ?qualifier ?annotation WHERE {\n" +
                "<http://identifiers.org/biomodels.db/BIOMD0000000001> ?p ?element . \n" +
                "?p rdfs:subPropertyOf sbmlrdf:sbmlElement .\n" +
                "?element ?qualifier ?annotation .\n" +
                "?qualifier rdfs:subPropertyOf sbmlrdf:sbmlAnnotation}"
            },
            {
                shortname: "Query 3",
                description: "All model elements with annotations to acetylcholine-gated channel complex (http://identifiers.org/go/GO:0005892)",
                query:
                "PREFIX sbmlrdf: <http://identifiers.org/biomodels.vocabulary#>\n\n" +
                "SELECT ?modelElement ?elementType ?qualifier WHERE {\n" +
                "   ?modelElement ?qualifier <http://identifiers.org/go/GO:0005892> .\n" +
                "   ?qualifier rdfs:subPropertyOf sbmlrdf:sbmlAnnotation .\n" +
                "?modelElement rdf:type ?elementType}"
            }



        ]
    },
    {
        category: "Biosamples",
        queries: [
            {
                shortname: "Show main resources",
                description: "An example that selects Sample Groups, Samples, the main resource types in the dataset.",
                query:
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "SELECT DISTINCT *\n"+
                "WHERE {\n"+
                "{ select ?item WHERE { ?item a biosd-terms:BiosamplesSubmission. } LIMIT 3}\n"+
                "UNION { select ?item { ?item a biosd-terms:SampleGroup. } LIMIT 3 }\n"+
                "UNION { select ?item { ?item a biosd-terms:Sample. } LIMIT 3 }\n"+
                "}"
            },
            {
                shortname: "Samples from homo sapiens and their provenance",
                description: "Shows how to get and filter sample attributes. It also shows how the web pages on the provenance databases are linked.",
                query:
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "PREFIX pav: <http://purl.org/pav/2.0/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/terms/atlas/>\n"+
                "# \n"+
                "## Samples with a given property value and type, and external links\n"+
                "## This version doesn't exploit any ontology for sample properties.\n"+
                "#\n"+
                "SELECT DISTINCT ?smp ?pvLabel ?propTypeLabel ?repoName ?repoAcc ?repoUrl\n"+
                "WHERE\n"+
                "{\n"+
                "?smp\n"+
                "a biosd-terms:Sample;\n"+
                "biosd-terms:has-sample-attribute ?pv;\n"+
                "pav:derivedFrom ?webRec.\n"+

                "# atlas:propertyValue, atlas:propertyType, dcterms:title are available too, data integration purposes.\n"+
                "?pv\n"+
                "rdfs:label ?pvLabel;\n"+
                "dc:type ?propTypeLabel. # this is used even when no specific type is present\n"+
                "FILTER ( LCASE ( STR ( ?propTypeLabel ) ) = 'organism' ).\n"+
                "FILTER ( LCASE ( STR ( ?pvLabel ) ) = 'homo sapiens' ).\n"+
                "?webRec\n"+
                "dcterms:identifier ?repoAcc;\n"+
                "dcterms:source ?repoName;\n"+
                "foaf:page ?repoUrl.\n"+
                "}"

            },
            {
                shortname: "Samples that derives from a given genus",
                description: "Samples derived from the mus mus genus or specific organisms under it, as they are classified by. the NCBI Taxonomy.",
                query: "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"+
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "#"+
                "## All samples that derives from a given genus (Mus)\n"+
                "#\n"+
                "SELECT DISTINCT ?smp ?pvLabel ?propTypeLabel\n"+
                "WHERE {\n"+
                "?smp biosd-terms:has-bio-characteristic ?pv.\n"+
                "?pv a ?pvType;\n"+
                "rdfs:label ?pvLabel.\n"+
                "# Mus\n"+
                "?pvType\n"+
                "rdfs:label ?propTypeLabel;\n"+
                "# '*' gives you transitive closure, even when inference is disabled\n"+
                "rdfs:subClassOf* <http://purl.obolibrary.org/obo/NCBITaxon_10088>\n"+
                "}"
            },
            {
                shortname: "Samples treated with alcohol",
                description: "Samples treated with a compound of 'alcohol' type, or a more specific type of alcohol. This is made through a query over the bioportal sparql endpoint (i.e., a federated query).",
                query: "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"+
                "PREFIX dcterms: <http://purl.org/dc/terms/>\n"+
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n"+
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "PREFIX pav: <http://purl.org/pav/2.0/>\n"+
                "PREFIX prov: <http://www.w3.org/ns/prov#>\n"+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/terms/atlas/>\n"+
                "PREFIX oac: <http://www.openannotation.org/ns/>\n"+
                "#\n"+
                "## All samples treated with a compound of 'alcohol' type or a more specific alcohol type\n"+
                "#  this is made through a query over the bioportal sparql endpoint (ie, a federated query)\n"+
                "#\n"+
                "SELECT DISTINCT ?smp ?pvLabel ?pvTypeLabel ?pvTypeClass ?pvTypeClassLabel\n"+
                "WHERE {\n"+
                "SERVICE <http://sparql.bioontology.org/ontologies/sparql/?apikey=c6ae1b27-9f86-4e3c-9dcf-087e1156eabe>\n"+
                "{\n"+
                "?pvTypeClass\n"+
                "rdfs:subClassOf <http://purl.obolibrary.org/obo/CHEBI_30879>;\n"+
                "rdfs:label ?pvTypeClassLabel.\n"+
                "}\n"+
                "?pv\n"+
                "a ?pvTypeClass;\n"+
                "atlas:propertyValue ?pvLabel; # equivalent to rdfs:label, dc:title\n"+
                "atlas:propertyType ?pvTypeLabel. # equivalent to dc:type\n"+
                "?smp\n"+
                "a biosd-terms:Sample;\n"+
                "biosd-terms:has-bio-characteristic ?pv.\n"+
                "}"

            },
            {
                shortname: "Temperature values and units",
                description: "This shows how numerical values and units are represented in RDF. When possible, dates are detected and represented the same way, using xsd^dateTime.",
                query:
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "#\n"+
                "## Samples with temperature attributes. DO REQUIRE Inference enabled\n"+
                "#\n"+
                "SELECT DISTINCT ?smp ?pvTypeLabel ?tvalLabel ?tval ?unitLabel\n"+
                "WHERE {\n"+
                "?smp\n"+
                "a biosd-terms:Sample;\n"+
                "biosd-terms:has-sample-attribute ?tPv.\n"+
                "?tPv\n"+
                "sio:SIO_000300 ?tval; # sio:has value\n"+
                "rdfs:label ?tvalLabel; # contains a string composed with value and unit\n"+
                "sio:SIO_000221 [ # sio:has unit\n"+
                "a obo:UO_0000027; # temperature\n"+
                "rdfs:label ?unitLabel\n"+
                "].\n"+
                "?tPv a ?pvType.\n"+
                "?pvType rdfs:label ?pvTypeLabel\n"+
                "}\n"

            }
        ]
    },

    {
        category: "ChEMBL",
        queries: [
            {
                shortname: "Query 1",
                description: "Get ChEMBL molecules",
                query:
                "PREFIX cco: <http://rdf.ebi.ac.uk/terms/chembl#>\n"+
                "SELECT ?molecule\n"+
                "    WHERE {\n"+
                "?molecule a ?type .\n"+
                "?type rdfs:subClassOf* cco:Substance .\n"+
                "}"
            },

            {
                shortname: "Query 2",
                description: "Get ChEMBL targets",
                query:
                "PREFIX cco: <http://rdf.ebi.ac.uk/terms/chembl#>\n"+
                "SELECT ?target \n"+
                "    WHERE { \n"+
                "?target a ?type . \n"+
                "?type rdfs:subClassOf* cco:Target .\n"+
                "}"
            },
            {
                shortname: "Query 3",
                description: "Get ChEMBL sources",
                query:
                "PREFIX cco: <http://rdf.ebi.ac.uk/terms/chembl#>\n"+
                "SELECT ?Source ?Description\n"+
                "   WHERE {\n"+
                "   ?Source ?p cco:Source .\n"+
                "   ?Source dcterms:description ?Description\n"+
                "}"
            },
            {
                shortname: "Query 4",
                description: "Get ChEMBL protein classification level 1 breakdown",
                query:
                    "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"+
                    "PREFIX cco: <http://rdf.ebi.ac.uk/terms/chembl#>\n\n"+
                    "SELECT ?parent ?parent_name  (COUNT(DISTINCT ?parent_tc) AS ?parent_desc_count)\n"+
                    "WHERE {\n"+
                    "?parent cco:classLevel 'L1' ;\n"+
                    "skos:prefLabel ?parent_name ;\n"+
                    "cco:hasTargetDescendant ?parent_tc .\n"+
                    "}\n"+
                    "GROUP BY ?parent ?parent_name"
            },
            {
                shortname: "Query 5",
                description: "Get ChEMBL activities, assays and targets for the drug Gleevec (CHEMBL941)",
                query:
                    "PREFIX cco: <http://rdf.ebi.ac.uk/terms/chembl#>\n"+
                    "PREFIX chembl_molecule: <http://rdf.ebi.ac.uk/resource/chembl/molecule/>\n"+
                    "SELECT ?activity ?assay ?target ?targetcmpt ?uniprot\n"+
                    "WHERE {\n"+
                    "?activity a cco:Activity ;\n"+
                    "cco:hasMolecule chembl_molecule:CHEMBL941 ;\n"+
                    "cco:hasAssay ?assay .\n"+
                    "?assay cco:hasTarget ?target .\n"+
                    "?target cco:hasTargetComponent ?targetcmpt .\n"+
                    "?targetcmpt cco:targetCmptXref ?uniprot .\n"+
                    "?uniprot a cco:UniprotRef\n"+
                    "}"

            }
        ]
    },
    {
        category: "Ensembl",
        queries: [
            {
                shortname: "Query 1",
                description: "Show all transcripts of human BRCA2 gene and their coordinates",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/resource/ensembl/>\n\n"+
                "SELECT DISTINCT ?transcript ?id ?typeLabel ?reference ?begin ?end ?location {\n"+
                "?transcript obo:SO_transcribed_from ensembl:ENSG00000139618 ;\n"+
                "a ?type;\n"+
                "dc:identifier ?id .\n"+
                "OPTIONAL {\n"+
                "?transcript faldo:location ?location .\n"+
                "?location faldo:begin [faldo:position ?begin] .\n"+
                "?location faldo:end [faldo:position ?end ] .\n"+
                "?location faldo:reference ?reference .\n"+
                "}\n"+
                "OPTIONAL {?type rdfs:label ?typeLabel}\n"+
                "}"
            },
            {
                shortname: "Query 2",
                description: "Show ordered exons with their length for transcript ENST00000380152",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX ensembltranscript: <http://rdf.ebi.ac.uk/resource/ensembl.transcript/>\n"+
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n\n"+
                "SELECT DISTINCT ?id ?order ?reference ?begin ?end ?strand {\n"+
                "ensembltranscript:ENST00000380152 obo:SO_has_part ?exon;\n"+
                "sio:SIO_000974 ?orderedPart .\n"+
                "?exon dc:identifier ?id .\n"+
                "# we include an explicit exon order\n"+
                "# so that we can order correctly in both + and - strand\n"+
                "?orderedPart sio:SIO_000628 ?exon .\n"+
                "?orderedPart sio:SIO_000300 ?order .\n\n"+
                "OPTIONAL {\n"+
                "?exon faldo:location ?location .\n"+
                "?location faldo:begin\n"+
                "[a ?strand ;\n"+
                "faldo:position ?begin] .\n"+
                "?location faldo:end\n"+
                "[a ?strand ;\n"+
                "faldo:position ?end] .\n"+
                "?location faldo:reference ?reference .\n"+
                "}\n"+
                "FILTER (?strand != faldo:ExactPosition)\n"+
                "}\n"+
                "ORDER BY ASC(?order)"
            },
            {
                shortname:"Query 3",
                description:"Get all mouse genes on chromosome 11 between location 101,100,523 and 101,190,725 forward strand",
                query:
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n\n"+
                "SELECT DISTINCT ?gene ?id ?label ?typelabel ?desc ?begin ?end {\n"+
                "?location faldo:begin\n"+
                "[a faldo:ForwardStrandPosition ;\n"+
                "faldo:position ?begin] .\n"+
                "?location faldo:end\n"+
                "[a faldo:ForwardStrandPosition ;\n"+
                "faldo:position ?end] .\n"+
                "?location faldo:reference <http://rdf.ebi.ac.uk/resource/ensembl/88/mus_musculus/GRCm38/11> .\n"+
                "?gene a ?type ;\n"+
                "rdfs:label ?label ;\n"+
                "dc:description ?desc ;\n"+
                "dc:identifier ?id ;\n"+
                "faldo:location ?location .\n"+
                "FILTER (?begin >= 101100523 && ?end <= 101190725 )\n"+
                "OPTIONAL {?type rdfs:label ?typelabel}\n"+
                "}"
            },
            {
                shortname: "Query 4",
                description: "Get orthologs for human gene ENSG00000139618",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/resource/ensembl/>\n\n"+
                "SELECT DISTINCT ?gene ?ortholog ?orthologLabel ?name {\n"+
                "?gene sio:SIO_000558 ?ortholog .\n"+
                "?gene obo:RO_0002162 ?taxon .\n"+
                "?gene rdfs:label ?geneLabel .\n"+
                "?ortholog rdfs:label ?orthologLabel .\n"+
                "?ortholog obo:RO_0002162 ?ortholog_taxon .\n"+
                "?ortholog_taxon skos:altLabel ?name\n"+
                "VALUES ?gene {ensembl:ENSG00000139618}\n"+
                "FILTER (?taxon != ?ortholog_taxon)\n"+
                "}"
            },
            {
                shortname: "Do it like Biomart Query 1",
                description: "Get all exons for a given list of transcripts",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX ensembltranscript: <http://rdf.ebi.ac.uk/resource/ensembl.transcript/>\n"+
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n\n"+
                "SELECT ?transcript ?exon ?id ?order ?begin ?end ?strand{\n"+
                "?transcript obo:SO_has_part ?exon;\n"+
                "sio:SIO_000974 ?orderedPart .\n"+
                "?exon dc:identifier ?id .\n"+
                "?orderedPart sio:SIO_000628 ?exon .\n"+
                "?orderedPart sio:SIO_000300 ?order .\n"+
                "OPTIONAL {\n"+
                "?exon faldo:location ?location .\n"+
                "?location faldo:begin ?startthing .\n"+
                "?startthing faldo:position ?begin .\n"+
                "?startthing a ?strand .\n"+
                "?location faldo:end ?endthing .\n"+
                "?endthing faldo:position ?end .\n"+
                "}\n"+
                "VALUES ?transcript { ensembltranscript:ENST00000380152 ensembltranscript:ENST00000408937 ensembltranscript:ENST00000403559 ensembltranscript:ENST00000393494 ensembltranscript:ENST00000350908}\n"+
                "}\n"+
                "ORDER BY ?id ?order\n"
            },
            {
                shortname: "Do it like Biomart Query 2",
                description: "Find all non-Ensembl references for features that have EMBL references",
                query:
                "PREFIX identifiers: <http://identifiers.org/>\n"+
                "SELECT ?feature ?dbentry ?other_dbentry ?property WHERE {\n"+
                "?feature rdfs:seeAlso ?dbentry .\n"+
                "?dbentry rdf:type identifiers:ena.embl  .\n"+
                "?feature rdfs:seeAlso ?other_dbentry .\n"+
                "FILTER ( ?dbentry != ?other_dbentry )\n"+
                "}"
            },
            {
                shortname: "Do it like Biomart Query 3",
                description: "Retrieve a list of external references from accessions you know, e.g. Uniprot IDs to all related accessions",
                query:
                "PREFIX ensemblterms: <http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
                "SELECT DISTINCT ?feature ?dbentry ?property ?dbentry2 WHERE { \n"+
                "  ?feature ?property ?dbentry .\n"+
                "  ?feature ?property2 ?dbentry2 .\n"+
                "  ?dbentry a ensemblterms:EnsemblDBEntry .\n"+
                "  ?dbentry2 a ensemblterms:EnsemblDBEntry .\n"+
                "   VALUES ?dbentry { <http://purl.uniprot.org/uniprot/O15409> }\n"+
                "   FILTER ( ?dbentry != ?dbentry2 )\n"+
                "}"




            }
        ]

    },
    {
        category: "Reactome",
        queries: [
            {
                shortname: "Query 1",
                description: "List all pathways",
                query:
                "PREFIX biopax3: <http://www.biopax.org/release/biopax-level3.owl#>\n\n"+
                "SELECT DISTINCT ?pathway ?pathwayname \n"+
                "WHERE\n"+
                "    {\n"+
                "       ?pathway rdf:type biopax3:Pathway .\n"+
                "?pathway biopax3:displayName ?pathwayname\n"+
                "}"

            },
            {
                shortname: "Query 2",
                description: "Pathways that references Insulin (http://purl.uniprot.org/uniprot/P01308)",
                query:
                "PREFIX biopax3: <http://www.biopax.org/release/biopax-level3.owl#>\n"+
                "SELECT DISTINCT ?pathway ?pathwayname \n"+
                "WHERE\n"+
                "{?pathway rdf:type biopax3:Pathway .\n"+
                "?pathway biopax3:displayName ?pathwayname .\n"+
                "?pathway biopax3:pathwayComponent ?reaction .\n"+
                "?reaction rdf:type biopax3:BiochemicalReaction .\n"+
                "{\n"+
                "{?reaction ?rel ?protein .}\n"+
                "UNION\n"+
                "{\n"+
                "?reaction  ?rel  ?complex .\n"+
                "?complex rdf:type biopax3:Complex .\n"+
                "?complex ?comp ?protein .\n"+
                "}}\n"+
                "?protein rdf:type biopax3:Protein .\n"+
                "?protein biopax3:entityReference <http://purl.uniprot.org/uniprot/P01308> \n"+
                "}\n"+
                "LIMIT 100\n"

            }

        ]

    },
 {
        category: "ExpressionAtlas",
        queries: [
            {
                shortname: "Query 1",
                description: "Show expression for the CYP51 gene",
                query:
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/expressionatlas/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/resource/expressionatlas/>\n\n"+
                "SELECT distinct ?diffValue ?expUri ?propertyType ?propertyValue ?pvalue\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/expressionatlas>\n"+
                "WHERE {\n"+
                " ?expUri atlasterms:hasPart ?analysis .\n"+
                " ?analysis atlasterms:hasOutput ?value .\n"+
                " ?analysis atlasterms:hasFactorValue ?factor .\n"+
                " ?factor atlasterms:propertyType ?propertyType .\n"+
                " ?factor atlasterms:propertyValue ?propertyValue .\n"+
                " ?value rdfs:label ?diffValue .\n"+
                " ?value atlasterms:pValue ?pvalue .\n"+
                " ?value atlasterms:refersTo  <http://rdf.ebi.ac.uk/resource/ensembl/ENSG00000001630> .\n"+
                "}"
            },
            {
              shortname: "Query 2",
              description: "What human protein coding genes are expressed where the experimental factor is asthma (EFO_0000270)?",
              query:
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/expressionatlas/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/resource/expressionatlas/>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
                "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n\n"+              
                "SELECT distinct ?expUri ?diffValue ?gene ?pvalue\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/expressionatlas>\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/homo_sapiens>\n"+
                "WHERE {            \n"+
                " ?expUri atlasterms:hasPart ?analysis.\n"+
                " ?analysis atlasterms:hasOutput ?value .\n"+
                " ?analysis atlasterms:hasFactorValue ?factor .\n"+
                " ?value rdfs:label ?diffValue .\n"+
                " ?value atlasterms:pValue ?pvalue .\n"+
                " ?value atlasterms:refersTo ?gene . \n"+
                " ?gene a ensembl:protein_coding .\n"+
                " ?factor a efo:EFO_0000270 .\n"+
                "}"
            },
            {
              shortname: "Query 3",
              description: "What human protein coding genes are expressed where the experimental factor is asthma (EFO_0000270)?",
              query:
              "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
              "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
              "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n"+
              "PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/expressionatlas/>\n"+
              "PREFIX atlas: <http://rdf.ebi.ac.uk/resource/expressionatlas/>\n"+
              "PREFIX ensembl:<http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
              "PREFIX biopax3:<http://www.biopax.org/release/biopax-level3.owl#>\n\n"+
              "SELECT distinct ?expUri ?diffValue ?gene ?pvalue ?pathwayname\n"+
              "FROM <http://rdf.ebi.ac.uk/dataset/expressionatlas>\n"+
              "FROM <http://rdf.ebi.ac.uk/dataset/homo_sapiens>\n"+
              "FROM <http://rdf.ebi.ac.uk/dataset/reactome>\n"+
              "WHERE {     \n"+
              "?expUri atlasterms:hasPart ?analysis .     \n"+
              "?analysis atlasterms:hasOutput ?value . \n"+
              "?analysis atlasterms:hasFactorValue ?factor .   \n"+
              "?value rdfs:label ?diffValue .\n"+
              "?value atlasterms:pValue ?pvalue . \n"+
              "?value atlasterms:refersTo ?gene .\n"+
              "?gene a ensembl:protein_coding .\n"+
              "?factor a efo:EFO_0000270 .\n"+
              "# get gene to protein from ensembl\n"+
              "?gene ensembl:DEPENDENT ?dbXref .\n\n"+
              "# query reactome for protein \n"+
              "?protein rdf:type biopax3:Protein .\n"+
              "    ?protein biopax3:memberPhysicalEntity \n"+
              "             [biopax3:entityReference ?dbXref] .\n"+
              "    ?pathway rdf:type biopax3:Pathway .\n"+
              "    ?pathway biopax3:displayName ?pathwayname .\n"+
              "    ?pathway biopax3:pathwayComponent ?reaction .\n"+
              "    ?reaction rdf:type biopax3:BiochemicalReaction .\n"+
              "    {\n"+
              "      {?reaction biopax3:left ?protein .}\n"+
              "      UNION \n"+
              "      {?reaction biopax3:right ?protein .}\n"+
              "      UNION \n"+
              "      {?reaction biopax3:left\n"+
              "                  [a biopax3:Complex ; biopax3:component ?protein ].}\n"+
              "      UNION\n"+
              "      {?reaction biopax3:right\n"+
              "                 [a biopax3:Complex ; biopax3:component ?protein ].}\n"+
              "    }\n"+
              "}"
            },
            {
              shortname: "Query 4",
              description: "Show baseline expression in liver for Illumina body map data",
              query:
              "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
              "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
              "PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/expressionatlas/>\n"+
              "PREFIX atlas: <http://rdf.ebi.ac.uk/resource/expressionatlas/>\n"+
              "PREFIX dcterms: <http://purl.org/dc/terms/>\n"+
              "PREFIX obo: <http://purl.obolibrary.org/obo/>\n\n"+
              "SELECT distinct ?desc ?diffValue ?gene ?fpkm\n"+
              "FROM <http://rdf.ebi.ac.uk/dataset/expressionatlas>\n"+
              "WHERE {\n"+
              "atlas:E-MTAB-513 atlasterms:hasPart ?analysis .\n"+
              "atlas:E-MTAB-513 dcterms:description ?desc .\n"+
              "?analysis atlasterms:hasOutput ?value . \n"+
              "?analysis atlasterms:hasFactorValue ?factor .  \n"+
              "?value rdfs:label ?diffValue .\n"+
              "?value atlasterms:fpkm ?fpkm . \n"+
              "?value atlasterms:refersTo ?gene . \n"+
              "?factor rdf:type obo:UBERON_0002107 .    \n"+
              "}"
            }
        ]
    },
    {
        category: "OntologyLookupService",
        queries: [
            {
                shortname: "Gene Ontology Query",
                description: "Get all terms and labels from the Gene Ontology",
                query:
                "SELECT ?class ?label\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/go>\n"+
                "   WHERE {\n"+
                "       ?class a owl:Class .\n"+
                "       ?class rdfs:label ?label .\n"+
                "   }"
            },

            {
                shortname: "EFO Query",
                description: "Get all terms and labels from the Gene Ontology",
                query:
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
                "SELECT ?child ?childLabel ?parent ?parentLabel\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/efo> \n"+
                "   WHERE {\n"+
                "   ?child rdfs:subClassOf ?parent .\n"+
                "   ?child rdfs:label ?childLabel .\n"+
                "   ?parent rdfs:label ?parentLabel .\n"+
                "   }"

            },



            {
                shortname: "Children of GO",
                description: "Get all children of “cellular process” from the Gene Ontology ",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n\n"+
                "SELECT ?child ?childLabel\n"+
                "FROM <http://rdf.ebi.ac.uk/dataset/go> \n"+
                "       WHERE {\n"+
                "       ?child rdfs:subClassOf* obo:GO_0009987 .\n"+
                "       ?child rdfs:label ?childLabel .\n"+
                "}"
            },

            {
                shortname: "Print a list of all subClasses",
                description: "Show all children for the term 'cell' from the cell ontology (cl)",
                query:
                "SELECT ?subject ?label ?altTerm \n" +
                "from <http://rdf.ebi.ac.uk/dataset/cl> \n" +
                "    WHERE { \n" +
                "    ?subject rdfs:subClassOf* <http://purl.obolibrary.org/obo/CL_0000000> . \n"+
                "    ?subject rdfs:label ?label. \n"+
                "optional{\n"+
                "    ?subject <http://www.geneontology.org/formats/oboInOwl#hasExactSynonym> ?altTerm}\n" +
                "}"
            },
            {
                shortname : "Query for a Label in all terms",
                description:"Find all terms that mention 'alzheimer' in the label",
                query:
                "SELECT DISTINCT  ?class ?label\n"+
                "WHERE {\n"+
                "<http://rdf.ebi.ac.uk/dataset/ols> dcterms:hasPart  ?allOlsOntologiesGraph  . \n"+
                "GRAPH ?allOlsOntologiesGraph {\n"+
                "?class a owl:Class .\n"+
                "?class rdfs:label ?label .\n"+
                "filter regex(?label, 'Alzheimer', 'i')\n"+
                "}\n" +
                "}\n"

},
            {


            shortname : "Searching for cross references",
            description:"  Get cross references to DOID_10652 (Alzheimer's disease) from the disease ontology",
            query:
            "PREFIX obo: <http://purl.obolibrary.org/obo/>\n\n"+
            "PREFIX oboInOWL: <http://www.geneontology.org/formats/oboInOwl#> \n"+
            "SELECT DISTINCT  ?xref\n"+
            "WHERE {\n"+
            "obo:DOID_10652 oboInOWL:hasDbXref ?xref .\n"+
            "}"

}
        ]

    },
/* {
        category: "GWAS",
        queries:[
            {
                shortname: "Query 1",
                description: "Get all the studies about lymphoma (EFO_0000574)",
                query:
                "SELECT DISTINCT ?a ?b \n" +
                "from <http://rdf.ebi.ac.uk/dataset/gwas> \n" +
                "where {?a ?b <http://www.ebi.ac.uk/efo/EFO_0000574>  }"

            }
        ]


    },
    {
        category: "Textmining",
        queries: [
            {
                shortname: "Query 1",
                description: "Just a stupid test query: Give me all articles that are related to disodium cromoglycate (CHEBI_128458) ",
                query:
                "SELECT DISTINCT ?a\n"+
                "from <http://rdf.ebi.ac.uk/dataset/pub>\n"+
                "{?a ?b <http://purl.obolibrary.org/obo/CHEBI_128458> \n"+
                "}"

            }

        ]

    }* /


{   category: "FederatedQuery",
    queries: [
        {
            shortname: "Query connecting Ensemble and Uniprot endpoints",
            description: "Get protein information from Uniprot that Ensembl has associated with ENSG00000139618 via a federated query",
            query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n"+
                "PREFIX identifiers: <http://identifiers.org/>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/resource/ensembl/>\n"+
                "PREFIX ensembltranscript: <http://rdf.ebi.ac.uk/resource/ensembl.transcript/>\n"+
                "PREFIX ensemblexon: <http://rdf.ebi.ac.uk/resource/ensembl.exon/>\n"+
                "PREFIX ensemblprotein: <http://rdf.ebi.ac.uk/resource/ensembl.protein/>\n"+
                "PREFIX ensemblterms: <http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
                "PREFIX core: <http://purl.uniprot.org/core/>\n"+
                "SELECT ?uniprot_id ?uniprot_uri ?isoform ?seq {\n"+
                "   ensembl:ENSG00000128573 ensemblterms:DEPENDENT ?uniprot_uri .\n"+
                "    ?uniprot_uri dc:identifier ?uniprot_id .\n"+
                "    SERVICE <http://sparql.uniprot.org/sparql> {\n"+
                " ?uniprot_uri core:sequence ?isoform .\n"+
                "?isoform rdf:value ?seq . \n"+
                "  } \n"+
                "}"

        }
    ]

}*/

]
