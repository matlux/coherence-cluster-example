
package net.matlux.coherence.tools.partitions;

import net.matlux.logging.log4j.ChainsawTest;

import org.apache.log4j.Logger;

import com.tangosol.net.partition.PartitionEvent;
import com.tangosol.net.partition.PartitionListener;


public class PartitionListenerImp implements PartitionListener {
	
	private static final Logger log = Logger.getLogger(ChainsawTest.class);

	@Override
	public void onPartitionEvent(PartitionEvent partitionEvent) {
		final int eventType = partitionEvent.getId();
		
		switch (eventType) {
		case PartitionEvent.PARTITION_LOST: // the one we really need
		case PartitionEvent.PARTITION_TRANSMIT_ROLLBACK: // just in case
		case PartitionEvent.PARTITION_TRANSMIT_COMMIT: // just in case
			
			log.debug("Partition Event=" + eventType);
		}
	}
	
}
