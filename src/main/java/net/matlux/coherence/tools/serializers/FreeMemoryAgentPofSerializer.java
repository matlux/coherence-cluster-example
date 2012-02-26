package net.matlux.coherence.tools.serializers;

import java.io.IOException;

import net.matlux.coherence.tools.agents.FreeMemoryAgent;
import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;


public class FreeMemoryAgentPofSerializer extends AbstractEvolvablePofSerializer<FreeMemoryAgent> {

    @Override
    protected FreeMemoryAgent createInstance(PofReader reader) throws IOException {
        String transactionID = reader.readString(0);

        return new FreeMemoryAgent(transactionID);
    }

    @Override
    protected void deserializeAttributes(FreeMemoryAgent obj, PofReader reader)
                                                                                 throws IOException {
        // No extra attributes to read
    }

    @Override
    protected void serializeAttributes(FreeMemoryAgent putEntryProcessor, PofWriter writer) throws IOException {
        writer.writeString(0, putEntryProcessor.getTxId());
    }


}
