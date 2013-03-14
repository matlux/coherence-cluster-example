package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.IMapEntry;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class IMapEntryPofSerializer extends AbstractEvolvablePofSerializer<IMapEntry> {

    @Override
    protected IMapEntry createInstance(PofReader reader) throws IOException {
        Object key = reader.readObject(0);
        Object val = reader.readObject(1);

        return new MapEntry(key, val);
    }

    @Override
    protected void deserializeAttributes(IMapEntry obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(IMapEntry mapEntry, PofWriter writer) throws IOException {
        writer.writeObject(0, mapEntry.key());
        writer.writeObject(1, mapEntry.val());
    }


}
