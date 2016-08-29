package uk.ac.ebi.fgpt.lode.utils;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public enum GraphQueryFormats {

    RDFXML ("RDF/XML", "application/rdf+xml"),
    N3 ("N3", "application/rdf+n3"),
    JSON ("JSON-LD", "application/rdf+json"),
    TURTLE ("TURTLE", "text/turtle");

    private final String format;
    private final String mimetype;

    private GraphQueryFormats(final String format, final String mimetype) {
	this.format = format;
	this.mimetype = mimetype;
    }

    @Override
    public String toString() {
        return format;
    }

    public String toMimeType() {
        return mimetype;
    }
}
