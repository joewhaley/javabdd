// BDDTestCase.java, created Jul 28, 2004 3:00:14 AM by joewhaley
// Copyright (C) 2004 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package bdd;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import net.sf.javabdd.BDDFactory;

/**
 * BDDTestCase
 * 
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDDTestCase extends TestCase implements Iterator {
    
    static volatile Collection factories;
    static final String[] factoryNames = {
        "net.sf.javabdd.BuDDyFactory",
        "net.sf.javabdd.CUDDFactory",
        "net.sf.javabdd.CALFactory",
        "net.sf.javabdd.JFactory",
        //"net.sf.javabdd.JDDFactory",
    };
    
    static void initFactories(int nodenum, int cachesize) {
        if (factories != null) return;
        synchronized (BDDTestCase.class) {
            if (factories != null) return;
            Collection f = new LinkedList();
            for (int k = 0; k < factoryNames.length; ++k) {
                String bddpackage = factoryNames[k];
                try {
                    Class c = Class.forName(bddpackage);
                    Method m = c.getMethod("init", new Class[] { int.class, int.class });
                    BDDFactory b = (BDDFactory) m.invoke(null, new Object[] { new Integer(nodenum), new Integer(cachesize) });
                    f.add(b);
                }
                catch (Throwable _) {}
            }
            factories = f;
        }
    }
    
    protected Iterator i;
    
    public BDDTestCase(int nodenum, int cachesize) {
        initFactories(nodenum, cachesize);
        reset();
    }
    public BDDTestCase() {
        this(1000, 1000);
    }
    
    public BDDFactory nextFactory() {
        return (BDDFactory) i.next();
    }
    
    public Object next() {
        return i.next();
    }
    
    public boolean hasNext() {
        return i.hasNext();
    }
    
    public void remove() {
        i.remove();
    }
    
    public void reset() {
        i = factories.iterator();
    }
}
