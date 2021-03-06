package edu.yale.library.ladybird.engine.oai;

import org.w3c.dom.Node;

/**
 * MarcReader supertype
 *
 * @author Osman Din
 */
public interface MarcReader {
    Record readMarc(Node arg) throws MarcReadingException;
}
