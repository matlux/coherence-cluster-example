
package net.matlux.coherence.tools.partitions;

import net.matlux.logging.log4j.ChainsawTest;

import org.apache.log4j.Logger;

import com.tangosol.net.Member;
import com.tangosol.net.partition.PartitionEvent;
import com.tangosol.net.partition.PartitionListener;


public class PartitionListenerImp implements PartitionListener {
	
	private static final Logger log = Logger.getLogger(PartitionListenerImp.class);

	@Override
	public void onPartitionEvent(PartitionEvent partitionEvent) {
		final int eventType = partitionEvent.getId();

		//log.debug(eventType + ", " + partitionEvent.toString());
		
		String evName=null;
		String name= System.getProperty("tangosol.coherence.member");

		switch (eventType) {
		case PartitionEvent.PARTITION_LOST:				evName = "LOST"; break;
		case PartitionEvent.PARTITION_TRANSMIT_BEGIN:	evName = "TRANSMIT_BEGIN"; break;
		case PartitionEvent.PARTITION_TRANSMIT_COMMIT:	evName = "TRANSMIT_COMMIT";  break;
		case PartitionEvent.PARTITION_TRANSMIT_ROLLBACK:evName = "TRANSMIT_ROLLBACK";  break;
		case PartitionEvent.PARTITION_RECEIVE_BEGIN:	evName = "RECEIVE_BEGIN";  break;
		case PartitionEvent.PARTITION_RECEIVE_COMMIT:	evName = "RECEIVE_COMMIT";  break;
		case PartitionEvent.PARTITION_ASSIGNED:			evName = "ASSIGNED";  break;
			
			
		default:
			evName="unkown";
			
		}
		String fromMember = getFromMember(partitionEvent);
		String toMember = getToMember(partitionEvent);
		
		log.info(name + " says "+partitionEvent.getPartitionSet() + " " + evName +
				" from " + fromMember +
				" to " + toMember);
	}

	private String getFromMember(PartitionEvent partitionEvent) {
		Member from = partitionEvent.getFromMember();
		String fromSet = null;
		if (from!=null ) fromSet = from.getMemberName();
		return fromSet;
	}
	private String getToMember(PartitionEvent partitionEvent) {
		Member from = partitionEvent.getToMember();
		String fromSet = null;
		if (from!=null ) fromSet = from.getMemberName();
		return fromSet;
	}
	
}
