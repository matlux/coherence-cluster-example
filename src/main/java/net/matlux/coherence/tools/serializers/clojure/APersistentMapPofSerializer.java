package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.APersistentMap;
import clojure.lang.IMapEntry;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class APersistentMapPofSerializer extends AbstractEvolvablePofSerializer<APersistentMap> {

    @Override
    protected APersistentMap createInstance(PofReader reader) throws IOException {
    	Map<Object,Object> map = new HashMap();
    	map = reader.readMap(0, map);

        IPersistentMap pmap = RT.map(null);
        for (Map.Entry entry : map.entrySet()) {
        	pmap = pmap.assoc(entry.getKey(),entry.getValue());
        }
        return (APersistentMap) pmap;
    }

    @Override
    protected void deserializeAttributes(APersistentMap obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(APersistentMap map, PofWriter writer) throws IOException {
        writer.writeMap(0, map);
    }


}
