package net.matlux.coherence.tools.entryprocessors;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.cache.LocalCache;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

public abstract class TransactionalEntryProcessor extends AbstractProcessor {

	private static final Logger log = Logger.getLogger(TransactionalEntryProcessor.class);

	private String member = System.getProperty("tangosol.coherence.member");

	private String txId;
	

	public TransactionalEntryProcessor(String transactionID) {
		txId=transactionID;
	}

	public TransactionalEntryProcessor() {
		txId="undef";
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxId() {
		return txId;
	}
	
	protected BackingMapManagerContext getContext(Entry entry) {
        BinaryEntry binaryEntry = (BinaryEntry) entry;
        BackingMapManagerContext context = binaryEntry.getContext();
        return context;
    }

	@Override
	public Object process(Entry arg0) {
		Stopwatch stopwatch = new Stopwatch().start();
		//TODO:
		stopwatch.stop();

		long millis = stopwatch.elapsedMillis();
		
		log.debug(member + " [tx="+getTxId()+"] TransactionalEntryProcessor  arg="+arg0+" lasted: "+millis +" ms");
		return null;
	}
	
    /*protected LocalCache getCache(Entry entry) {
        BackingMapManagerContext context = getContext(entry);
        Map backingMap = context.getBackingMap(getCacheName());
        LocalCache cache = (LocalCache) backingMap;
        return cache;
    }*/
	
	
}
