package net.matlux.coherence.tools.members;

import org.apache.log4j.Logger;

import com.tangosol.net.MemberEvent;
import com.tangosol.net.MemberListener;
import com.tangosol.util.Base;

public class MemberListenerEventLogger extends Base implements MemberListener {

	private static final Logger log = Logger.getLogger(MemberListenerEventLogger.class);
	private String member = System.getProperty("tangosol.coherence.member");
	

	@Override
	public void memberJoined(MemberEvent event) {
		log.info(member + " says " + event.getMember().getMemberName() + " Joined on " + event.getMember().getMachineName()+ " Port: " + event.getMember().getPort());
	}

	@Override
	public void memberLeaving(MemberEvent event) {
		log.info(member + " says " + event.getMember().getMemberName() + " is Leaving : " + event.getMember().getMachineName() + " Port: " + event.getMember().getPort());
	}

	@Override
	public void memberLeft(MemberEvent event) {
		log.warn(member + " says " + event.getMember().getMemberName() + " has Left : " + event.getMember().getMachineName() + " Port: " + event.getMember().getPort());
	}

}