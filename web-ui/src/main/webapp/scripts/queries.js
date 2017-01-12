

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

var exampleQueries = [

    {
        category: "Biomodels",
        queries: [
            {
                shortname: "Query 1",
                description: "List all species identifers and their names in model http://identifiers.org/biomodels.db/BIOMD0000000001",
                namedgraph: "http://ebi.rdf.ac.uk/dataset/biomodels.com",
                query:
                "PREFIX sbmlrdf: <http://identifiers.org/biomodels.vocabulary#>\n\n" +
                "SELECT ?speciesid ?name WHERE {\n" +
                "<http://identifiers.org/biomodels.db/BIOMD0000000001> sbmlrdf:species ?speciesid .\n" +
                "?speciesid sbmlrdf:name ?name}"
            },
            {
                shortname: "Query 2",
                description: "Get element annotations of the model http://identifiers.org/biomodels.db/BIOMD0000000001",
                namedgraph: "http://ebi.rdf.ac.uk/dataset/biomodels.com",
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
                namedgraph: "http://ebi.rdf.ac.uk/dataset/biomodels.com",
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
                namedgraph: "XXXX",
                query:
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n"+
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "PREFIX pav: <http://purl.org/pav/2.0/>\n"+
                "PREFIX prov: <http://www.w3.org/ns/prov#>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/terms/atlas/>\n"+
                "PREFIX oac: <http://www.openannotation.org/ns/>\n"+
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
                "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"+
                "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n"+
                "PREFIX biosd-terms: <http://rdf.ebi.ac.uk/terms/biosd/>\n"+
                "PREFIX pav: <http://purl.org/pav/2.0/>\n"+
                "PREFIX prov: <http://www.w3.org/ns/prov#>\n"+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"+
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX atlas: <http://rdf.ebi.ac.uk/terms/atlas/>\n"+
                "PREFIX oac: <http://www.openannotation.org/ns/>\n"+
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
                "PREFIX sio: <http://semanticscience.org/resource/>\n"+
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/resource/ensembl/>\n"+
                "PREFIX ensembltranscript: <http://rdf.ebi.ac.uk/resource/ensembl.transcript/>\n"+
                "PREFIX ensemblexon: <http://rdf.ebi.ac.uk/resource/ensembl.exon/>\n"+
                "PREFIX ensemblprotein: <http://rdf.ebi.ac.uk/resource/ensembl.protein/>\n"+
                "PREFIX ensemblterms: <http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
                "PREFIX identifiers: <http://identifiers.org/>\n"+
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"+
                "PREFIX core: <http://purl.uniprot.org/core/>\n"+
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
                "PREFIX faldo: <http://biohackathon.org/resource/faldo#>\n"+
                "PREFIX ensembl: <http://rdf.ebi.ac.uk/resource/ensembl/>\n"+
                "PREFIX ensembltranscript: <http://rdf.ebi.ac.uk/resource/ensembl.transcript/>\n"+
                "PREFIX ensemblexon: <http://rdf.ebi.ac.uk/resource/ensembl.exon/>\n"+
                "PREFIX ensemblprotein: <http://rdf.ebi.ac.uk/resource/ensembl.protein/>\n"+
                "PREFIX ensemblterms: <http://rdf.ebi.ac.uk/terms/ensembl/>\n"+
                "PREFIX identifiers: <http://identifiers.org/>\n"+
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
    }

]

