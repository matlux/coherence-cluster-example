package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.APersistentMap;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentList;
import clojure.lang.IPersistentMap;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.PersistentList;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class PersistentListPofSerializer extends AbstractEvolvablePofSerializer<PersistentList> {

    @Override
    protected PersistentList createInstance(PofReader reader) throws IOException {
    	List<Object> coll = new LinkedList();
    	coll = (List<Object>) reader.readCollection(0, coll);
    	Collections.reverse(coll);

        ISeq plist = null;
        for (Object entry : coll) {
        	if (plist==null) {
        		plist = RT.list(entry);
        	} else {
        		plist = plist.cons(entry);
        	}
        }
        return (PersistentList) plist;
    }

    @Override
    protected void deserializeAttributes(PersistentList obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(PersistentList list, PofWriter writer) throws IOException {
        writer.writeCollection(0, list);
    }


}
