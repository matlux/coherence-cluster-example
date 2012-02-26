package net.matlux.coherence.tools.entryprocessors;


import net.matlux.training.coherence.mat.Position;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.InvocableMap.Entry;

public class ModifyEntryProcessor extends TransactionalEntryProcessor {

	private static final Logger log = Logger.getLogger(ModifyEntryProcessor.class);

	private String member = System.getProperty("tangosol.coherence.member");


	final private double percentageChange;
	
	public ModifyEntryProcessor(double percentageChange) {
		super();
		this.percentageChange = percentageChange;
	}

	public ModifyEntryProcessor(String transactionID,double percentageChange) {
		this(percentageChange);
		setTxId(transactionID);
	}

	@Override
	public Object process(Entry entry) {

		Stopwatch stopwatch = new Stopwatch().start();
		//meaningfull job
		
		try {
			BackingMapManagerContext context = getContext(entry);
			//Object binaryValue = entry.getValue();
			if(!entry.isPresent()) {
				throw new RuntimeException("Entry not present");
			}
			Position position = (Position) entry.getValue();

			//Position position = (Position)context.getValueFromInternalConverter().convert(binaryValue);
			
			//log.debug(member + " [tx="+getTxId()+"] ModifyEntryProcessor  symbol="+binaryValue+"");
			//log.debug(member + " [tx="+getTxId()+"] ModifyEntryProcessor  symbol="+position.getSymbol()+"");

			Position newPosition = new Position(position.getId(),position.getSymbol(),position.getPrice() * (1+percentageChange));
			
			//Object newBinaryPosition = context.getValueToInternalConverter().convert(newPosition);
			entry.setValue(newPosition);			
			
		} catch(RuntimeException e) {
			stopwatch.stop();
			long millis = stopwatch.elapsedMillis();
			log.error(member + " [tx="+getTxId()+"] ModifyEntryProcessor  entry="+entry+" raised an exception:"+e.getMessage()+" lasted: "+millis +" ms");
			throw e;
		}
		stopwatch.stop();
		long millis = stopwatch.elapsedMillis();
		
		log.info(member + " [tx="+getTxId()+"] ModifyEntryProcessor  entry="+entry+" lasted: "+millis +" ms");
	


		return null;
	}

	public double getPercentageChange() {
		return percentageChange;
	}

	//public void setPercentageChange(double percentageChange) {
	//	this.percentageChange = percentageChange;
	//}



}
