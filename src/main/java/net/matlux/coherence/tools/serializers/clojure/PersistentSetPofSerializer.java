package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.APersistentMap;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentList;
import clojure.lang.PersistentVector;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class PersistentSetPofSerializer extends AbstractEvolvablePofSerializer<PersistentHashSet> {

    @Override
    protected PersistentHashSet createInstance(PofReader reader) throws IOException {
    	Collection<Object> coll = new LinkedList();
    	coll = reader.readCollection(0, coll);

    	IPersistentSet pvec = PersistentHashSet.EMPTY;
        for (Object entry : coll) {
        	pvec = (IPersistentSet) pvec.cons(entry);
        	
        }
        return (PersistentHashSet) pvec;
    }

    @Override
    protected void deserializeAttributes(PersistentHashSet obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(PersistentHashSet pvec, PofWriter writer) throws IOException {
        writer.writeCollection(0, pvec);
    }


}
