package net.matlux.coherence.tools.serializers;

import java.io.IOException;

import com.tangosol.io.Evolvable;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.Binary;

public abstract class AbstractEvolvablePofSerializer<T> implements PofSerializer {

    protected abstract void serializeAttributes(T obj, PofWriter writer) throws IOException;
    
    protected abstract void deserializeAttributes(T obj, PofReader reader) throws IOException;
    
    protected abstract T createInstance(PofReader reader) throws IOException;
    
    @SuppressWarnings("unchecked")
    public void serialize(PofWriter writer, Object obj) throws IOException {
        T instance = (T) obj;
        boolean isEvolvable = obj instanceof Evolvable;
        Evolvable evolvable = null;
        
        if (isEvolvable) {
            evolvable = (Evolvable) obj;
            int dataVersion = Math.max(evolvable.getDataVersion(),
                                       evolvable.getImplVersion());
            writer.setVersionId(dataVersion);
        }

        serializeAttributes(instance, writer);
        
        Binary futureData = isEvolvable ? evolvable.getFutureData() : null;
        
        writer.writeRemainder(futureData);
    }
    
    public Object deserialize(PofReader reader) throws IOException {
        T instance = createInstance(reader);
        
        Evolvable evolvable = null;
        boolean isEvolvable = instance instanceof Evolvable;
        if (isEvolvable) {
            evolvable = (Evolvable) instance;
            evolvable.setDataVersion(reader.getVersionId());
        }
        
        deserializeAttributes(instance, reader);
        
        Binary futureData = reader.readRemainder();
        if (isEvolvable) {
            evolvable.setFutureData(futureData);
        }
        
        return instance;
    }
}