package net.matlux.coherence.tools.backingmap;

import org.apache.log4j.Logger;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;

public class VerboseBackingMapListener extends AbstractMultiplexingBackingMapListener {
	 
	private static final Logger log = Logger.getLogger(VerboseBackingMapListener.class);

	private String member = System.getProperty("tangosol.coherence.member");

	private BackingMapManagerContext context;
	
    public VerboseBackingMapListener(BackingMapManagerContext context) {
        super(context);
    	this.context = context;
    }
     
    @Override
    protected void onBackingMapEvent(MapEvent mapEvent, Cause cause) {
    	String str = mapEvent.toString();
    	String str2send=null;
    	if(str.length() > 1000) str2send = str.substring(0, 1000);
    	str2send=str;
    	
    	Object objKey = mapEvent.getKey();
    	Object binaryKey = context.getKeyToInternalConverter().convert(objKey);
    	int partitionNb = context.getKeyPartition(binaryKey);
    	
    	log.debug(String.format(member + " [part=" + partitionNb +"]: Thread: %s Cause: %s Event: %s\n", Thread.currentThread().getName(), cause, str2send));
       //System.out.printf("Thread: %s Cause: %s Event: %s\n", Thread.currentThread().getName(), cause, mapEvent);
         
    }
}