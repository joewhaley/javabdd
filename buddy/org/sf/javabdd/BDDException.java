package org.sf.javabdd;

/**
 * An exception caused by an invalid BDD operation.
 * 
 * @author John Whaley
 * @version $Id: BDDException.java,v 1.3 2003/07/01 00:10:19 joewhaley Exp $
 */
public class BDDException extends RuntimeException {
    public BDDException() {
        super();
    }
    public BDDException(String s) {
        super(s);
    }
}
