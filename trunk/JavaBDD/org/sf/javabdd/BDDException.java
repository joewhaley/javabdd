package org.sf.javabdd;

/**
 * An exception caused by an invalid BDD operation.
 * 
 * @author John Whaley
 * @version $Id$
 */
public class BDDException extends RuntimeException {
    public BDDException() {
        super();
    }
    public BDDException(String s) {
        super(s);
    }
}
