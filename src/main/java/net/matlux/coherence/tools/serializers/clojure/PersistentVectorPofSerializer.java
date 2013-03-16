package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.APersistentMap;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.PersistentVector;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class PersistentVectorPofSerializer extends AbstractEvolvablePofSerializer<PersistentVector> {

    @Override
    protected PersistentVector createInstance(PofReader reader) throws IOException {
    	Collection<Object> coll = new LinkedList<Object>();
    	coll = reader.readCollection(0, coll);

    	PersistentVector pmap = (PersistentVector) RT.vector(null);
        for (Object entry : coll) {
        	pmap = pmap.cons(entry);
        }
        return pmap;
    }

    @Override
    protected void deserializeAttributes(PersistentVector obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(PersistentVector coll, PofWriter writer) throws IOException {
        writer.writeCollection(0, coll);
    }


}
