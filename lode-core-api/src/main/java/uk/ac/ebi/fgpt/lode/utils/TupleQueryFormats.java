package uk.ac.ebi.fgpt.lode.utils;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public enum TupleQueryFormats{


    XML ("XML", "application/sparql-results+xml"),
    CSV ("CSV", "text/csv"),
    TSV ("TSV", "text/tab-separated-values"),
    JSON ("JSON", "application/sparql-results+json");


    private final String format;
    private final String mimetype;

    private TupleQueryFormats(String format, String mimetype) {
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
