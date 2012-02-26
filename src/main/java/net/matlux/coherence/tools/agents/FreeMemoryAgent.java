package net.matlux.coherence.tools.agents;

import java.io.IOException;
import java.io.Serializable;

import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.training.coherence.mat.Position;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.BackingMapManagerContext;

public class FreeMemoryAgent extends AbstractInvocable implements Serializable {

	private static final Logger log = Logger.getLogger(FreeMemoryAgent.class);

	private String member = System.getProperty("tangosol.coherence.member");

	private String txId;
	

	public FreeMemoryAgent(String transactionID) {
		txId=transactionID;
	}

	public FreeMemoryAgent() {
		txId="undef";
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxId() {
		return txId;
	}
	
	@Override
	public void run() {
		Stopwatch stopwatch = new Stopwatch().start();
		//meaningfull job
		
		long freemem;
		try {
			Runtime rt = Runtime.getRuntime();
			freemem = rt.freeMemory();
			setResult(freemem);
			
		} catch(RuntimeException e) {
			stopwatch.stop();
			long millis = stopwatch.elapsedMillis();
			log.error(member + " [tx="+getTxId()+"] FreeMemoryAgent raised an exception:"+e.getMessage()+" lasted: "+millis +" ms");
			throw e;
		}
		stopwatch.stop();
		long millis = stopwatch.elapsedMillis();
		
		log.info(member + " [tx="+getTxId()+"] FreeMemoryAgent freemem="+freemem+" bytes lasted: "+millis +" ms");
	
	}
	
	
	// deserilization
	/*@Override
	public void readExternal(PofReader reader) throws IOException {
		txId = reader.readString(0);
		
	}
	//serialization
	@Override
	public void writeExternal(PofWriter writer) throws IOException {
		writer.writeString(0, this.getTxId());
		
	}*/

}
