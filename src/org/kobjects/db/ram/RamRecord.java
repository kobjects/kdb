package org.kobjects.db.ram;

import java.util.*;
import org.kobjects.db.*;

class RamRecord extends DbRecord {

    Vector selection;
    int index = -1;


    RamRecord (Vector selection) {
        this.selection = selection;
    }


    public void refresh () {
        throw new RuntimeException ("NYI");
    }
    
    
    public void update () {
        throw new RuntimeException ("NYI");
    }


    public void delete () {
        throw new RuntimeException ("NYI");
    }

    public Object getId () {
        throw new RuntimeException ("NYI");
    }


    public boolean hasNext () {
        return index < selection.size ()-1;
    }

    public void next () {
        throw new RuntimeException ("NYI");
    }

    public void reset () {
        throw new RuntimeException ("NYI");
    }


    public void destroy () {
        throw new RuntimeException ("NYI");
    }
}

