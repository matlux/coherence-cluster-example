package net.matlux.coherence.tools.serializers.clojure;

import java.io.IOException;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.serializers.AbstractEvolvablePofSerializer;

import clojure.lang.Keyword;
import clojure.lang.RT;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class KeywordPofSerializer extends AbstractEvolvablePofSerializer<Keyword> {

    @Override
    protected Keyword createInstance(PofReader reader) throws IOException {
        String ns = reader.readString(0);
        String name = reader.readString(1);

        return RT.keyword(ns,name);
    }

    @Override
    protected void deserializeAttributes(Keyword obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(Keyword putEntryProcessor, PofWriter writer) throws IOException {
        writer.writeString(0, putEntryProcessor.getNamespace());
        writer.writeString(1, putEntryProcessor.getName());
    }


}
