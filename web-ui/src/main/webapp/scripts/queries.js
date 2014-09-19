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
        shortname : "Query 1",
        description: "Retrieve the associated concepts and terms for descriptor D015242",
        query: 	"SELECT distinct ?dlabel ?concept ?conlabel ?term ?termlabel ?rn\n" +
				"FROM <http://mor.nlm.nih.gov/mesh2014>\n" +
				"WHERE {\n" +
				"  mesh:D015242 rdfs:label ?dlabel .\n" +
				"  mesh:D015242 meshv:concept ?concept .\n" +
				"  ?concept rdfs:label ?conlabel .\n" +
				"  ?concept meshv:preferredTerm ?term .\n" +
				"  ?term rdfs:label ?termlabel .\n" +
				"  ?concept meshv:registryNumber ?rn .\n" +
				"} \n"
    },
	
	{
        shortname : "Query 2",
        description: "Retrieve all of the descriptors and concepts, where concept labels start with levo but descriptor labels do not start with levo ",
        query:	"SELECT distinct ?desc ?desclabel ?concept ?conlabel\n" +
				"FROM <http://mor.nlm.nih.gov/mesh2014>\n" +
				"WHERE {\n" +
				"  ?desc rdfs:label ?desclabel .\n" +
				"  ?desc rdf:type meshv:Descriptor .\n" +
				"  ?desc meshv:concept ?concept .\n" +
				"  ?concept rdfs:label ?conlabel .\n" +
				"  filter regex(?conlabel, \"levo\", \"i\")\n" +
				"  filter (!regex(?desclabel, \"levo\", \"i\"))\n" +
				"} \n"
    },
	
	{
        shortname : "Query 3",
        description: "Get the number of concepts associated with each given descriptor",
        query:	"SELECT ?descriptor (count(?concept) as ?conceptcount)\n" +
				"FROM <http://mor.nlm.nih.gov/mesh2014>\n" +
				"WHERE {\n" +
				"  ?descriptor rdf:type meshv:Descriptor .\n" +
				"  ?descriptor meshv:concept ?concept .\n" +
				"  ?concept rdf:type meshv:Concept .\n" +
				"} \n" +
				"GROUP BY ?descriptor\n" +
				"ORDER BY desc(?conceptcount) \n"
    },
	
	{
        shortname : "Query 4",
        description: "Get the number of concepts associated with each given SCR ",
        query:	"SELECT ?scr (count(?concept) as ?conceptcount)\n" +
				"FROM <http://mor.nlm.nih.gov/mesh2014>\n" +
				"WHERE {\n" +
				"  ?scr rdf:type meshv:SupplementaryConceptRecord .\n" +
				"  ?scr meshv:concept ?concept .\n" +
				"  ?concept rdf:type meshv:Concept .\n" +
				"} \n" +
				"GROUP BY ?scr\n" +
				"ORDER BY desc(?conceptcount) \n"
    }

];
