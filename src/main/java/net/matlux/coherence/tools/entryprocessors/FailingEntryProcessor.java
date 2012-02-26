package net.matlux.coherence.tools.entryprocessors;


import net.matlux.training.coherence.mat.Position;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.InvocableMap.Entry;

public class FailingEntryProcessor extends TransactionalEntryProcessor {

	private static final Logger log = Logger.getLogger(FailingEntryProcessor.class);

	private String member = System.getProperty("tangosol.coherence.member");


	final private double percentageChange;
	
	public FailingEntryProcessor(double percentageChange) {
		super();
		this.percentageChange = percentageChange;
	}

	public FailingEntryProcessor(String transactionID,double percentageChange) {
		this(percentageChange);
		setTxId(transactionID);
	}

	@Override
	public Object process(Entry entry) {

		Throwable error=null;
		Stopwatch stopwatch = new Stopwatch().start();
		//meaningfull job
		
		try {
			BackingMapManagerContext context = getContext(entry);
			Object binaryValue = entry.getValue();
			Position position = (Position)context.getValueFromInternalConverter().convert(binaryValue);
			Position newPosition = new Position(position.ID,position.getSymbol(),position.getPrice() * (1+percentageChange));
			Object newBinaryPosition = context.getValueToInternalConverter().convert(newPosition);
			entry.setValue(newBinaryPosition);			
			stopwatch.stop();
		} catch(RuntimeException e) {
			long millis = stopwatch.elapsedMillis();
			log.error(member + " [tx="+getTxId()+"] ModifyEntryProcessor  entry="+entry+" raised an exception:"+e+" lasted: "+millis +" ms");
			throw e;
		} finally {
			long millis = stopwatch.elapsedMillis();
			
			log.info(member + " [tx="+getTxId()+"] ModifyEntryProcessor  entry="+entry+" lasted: "+millis +" ms");
		}


		return null;
	}

	public double getPercentageChange() {
		return percentageChange;
	}

	//public void setPercentageChange(double percentageChange) {
	//	this.percentageChange = percentageChange;
	//}



}
