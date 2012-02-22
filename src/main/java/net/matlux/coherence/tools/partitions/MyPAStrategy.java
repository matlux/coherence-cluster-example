package net.matlux.coherence.tools.partitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tangosol.net.Member;
import com.tangosol.net.PartitionedService;
import com.tangosol.net.ServiceInfo;
import com.tangosol.net.partition.DistributionManager;
import com.tangosol.net.partition.Ownership;
import com.tangosol.net.partition.PartitionSet;
import com.tangosol.net.partition.SimpleAssignmentStrategy;

public class MyPAStrategy extends SimpleAssignmentStrategy {

	private static final Logger log = Logger.getLogger(MyPAStrategy.class);
	private String member = System.getProperty("tangosol.coherence.member");
	private  DistributionManager manager;
	
	public void init(DistributionManager manager) {
		this.manager= manager;
		super.init(manager);
		/*this.manager = manager;
		
		super.init(new DistributionManager() {
			
			@Override
			public void suggest(PartitionSet arg0, Ownership arg1) {
				log.debug(String.format("%s suggest(PartitionSet %s, Ownership %s)",member,arg0,arg1));
				manager.suggest(arg0, arg1);
				
			}
			
			@Override
			public PartitionedService getService() {
				return manager.getService();
			}
			
			@Override
			public Ownership getPartitionOwnership(int arg0) {
				return manager.getPartitionOwnership(arg0);
			}
			
			@Override
			public Set<Member> getOwnershipMembers() {
				return manager.getOwnershipLeavingMembers();
			}
			
			@Override
			public Set<Member> getOwnershipLeavingMembers() {
				return manager.getOwnershipLeavingMembers();
			}
			
			@Override
			public PartitionSet getOwnedPartitions(Member arg0, int arg1) {
				return manager.getOwnedPartitions(arg0, arg1);
			}
			
			@Override
			public Member getMember(int arg0) {
				return manager.getMember(arg0);
			}
		});*/
	}
	
	public long analyzeDistribution() {
		
		
		
		long mylong= super.analyzeDistribution();
		String partitionByMembersSnapshot = getPartitionAllocationByMembers(manager);
		log.debug(member + " says "+ partitionByMembersSnapshot);
		return mylong;
	}

	private String getPartitionAllocationByMembers(DistributionManager manager2) {

		Map<Member,PartitionSet> map = getPartitionMapByMembers(manager2);
		ServiceInfo serviceInfo = manager2.getService().getInfo();
		StringBuilder strb = new StringBuilder();
		strb.append("senior="+serviceInfo.getOldestMember().getMemberName()+",");
		for(Map.Entry<Member,PartitionSet> entry : map.entrySet()){
			strb.append(" ").append(entry.getKey().getMemberName()).append(" has ").append(entry.getValue());
		}
		return strb.toString();
	}
	
	
	private Map<Member,PartitionSet> getPartitionMapByMembers(DistributionManager manager2) {
		Map<Member,PartitionSet>  memberPartition = new HashMap<Member, PartitionSet>();
		PartitionedService service = manager2.getService();
		int count = service.getPartitionCount();
		for (int partitionNb=0; partitionNb<count;partitionNb++) {
			Member member = service.getPartitionOwner(partitionNb);
			PartitionSet set = memberPartition.get(member);
			if (set==null) {
				set = new PartitionSet(count);
				memberPartition.put(member, set);
			}
			set.add(partitionNb);
			
		}
		return memberPartition;
	}
	
}
