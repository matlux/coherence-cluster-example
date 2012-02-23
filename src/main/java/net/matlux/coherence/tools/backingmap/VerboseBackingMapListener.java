package net.matlux.coherence.tools.backingmap;

import net.matlux.coherence.tools.partitions.PartitionListenerImp;

import org.apache.log4j.Logger;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;

public class VerboseBackingMapListener extends AbstractMultiplexingBackingMapListener {
	 
	private static final Logger log = Logger.getLogger(VerboseBackingMapListener.class);

	
    public VerboseBackingMapListener(BackingMapManagerContext context) {
        super(context);
    }
     
    @Override
    protected void onBackingMapEvent(MapEvent mapEvent, Cause cause) {
         
    	log.debug(String.format("Thread: %s Cause: %s Event: %s\n", Thread.currentThread().getName(), cause, mapEvent));
       //System.out.printf("Thread: %s Cause: %s Event: %s\n", Thread.currentThread().getName(), cause, mapEvent);
         
    }
}