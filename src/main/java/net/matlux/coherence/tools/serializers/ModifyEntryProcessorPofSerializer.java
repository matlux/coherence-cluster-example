package net.matlux.coherence.tools.serializers;

import java.io.IOException;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class ModifyEntryProcessorPofSerializer extends AbstractEvolvablePofSerializer<ModifyEntryProcessor> {

    @Override
    protected ModifyEntryProcessor createInstance(PofReader reader) throws IOException {
        String transactionID = reader.readString(0);
        double changePercentage = reader.readDouble(1);

        return new ModifyEntryProcessor(transactionID,changePercentage);
    }

    @Override
    protected void deserializeAttributes(ModifyEntryProcessor obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(ModifyEntryProcessor putEntryProcessor, PofWriter writer) throws IOException {
        writer.writeString(0, putEntryProcessor.getTxId());
        writer.writeDouble(1, putEntryProcessor.getPercentageChange());
    }


}
