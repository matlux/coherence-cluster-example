package net.matlux.training.coherence.mat;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import sun.rmi.rmic.iiop.CompoundType.Member;

import net.matlux.coherence.tools.agents.FreeMemoryAgent;
import net.matlux.coherence.tools.backingmap.VerboseBackingMapListener;
import net.matlux.coherence.tools.entryprocessors.ModifyEntryProcessor;
import net.matlux.coherence.tools.entryprocessors.TransactionalEntryProcessor;


import com.google.common.base.Stopwatch;
import com.tangosol.io.Serializer;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.CacheService;
import com.tangosol.net.Cluster;
import com.tangosol.net.Invocable;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.MemberListener;
import com.tangosol.net.NamedCache;
import com.tangosol.net.ServiceInfo;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Filter;
import com.tangosol.util.MapListener;
import com.tangosol.util.ServiceListener;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.filter.GreaterEqualsFilter;

public class Client {
	
	private static final Logger log = Logger.getLogger(Client.class);

	private static String member = System.getProperty("tangosol.coherence.member");

	
	
	private static class LoggedNamedCache implements NamedCache,InvocationService {

		private String serviceId=Long.toString(Math.abs(new Random().nextLong()));

		private int taskId=0;
		
		private String getNewTxId() {
			return serviceId + "-" + (taskId++);
		}
		
		private NamedCache positionCache = CacheFactory.getCache("positions");
		private InvocationService invocationService = (InvocationService)CacheFactory.getService("invocation-service");
		
		@Override
		public void addMapListener(MapListener arg0) {
			positionCache.addMapListener(arg0);
		}

		@Override
		public void addMapListener(MapListener arg0, Object arg1, boolean arg2) {
			positionCache.addMapListener(arg0, arg1, arg2);
		}

		@Override
		public void addMapListener(MapListener arg0, Filter arg1, boolean arg2) {
			positionCache.addMapListener(arg0, arg1, arg2);
		}

		@Override
		public void removeMapListener(MapListener arg0) {
			positionCache.removeMapListener(arg0);
		}

		@Override
		public void removeMapListener(MapListener arg0, Object arg1) {
			positionCache.removeMapListener(arg0, arg1);
		}

		@Override
		public void removeMapListener(MapListener arg0, Filter arg1) {
			positionCache.removeMapListener(arg0, arg1);
		}

		@Override
		public int size() {
			
			return positionCache.size();
		}

		@Override
		public boolean isEmpty() {
			return positionCache.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return positionCache.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return positionCache.containsKey(value);
		}

		@Override
		public Object get(Object key) {
			String txId = getNewTxId();
			Stopwatch stopwatch = new Stopwatch().start();
			Object obj = positionCache.get(key);
			stopwatch.stop();

			long millis = stopwatch.elapsedMillis();
			
			log.info(member +" [tx=" +txId +"] get("+key+") lasted: "+millis +" ms");
			return obj;
		}

		@Override
		public Object put(Object key, Object value) {
			String txId = getNewTxId();
			Stopwatch stopwatch = new Stopwatch().start();
			Object obj = positionCache.put(key,value);
			stopwatch.stop();

			long millis = stopwatch.elapsedMillis();
			
			log.info(member +" [tx=" +txId +"] put("+key+", "+value+") lasted: "+millis +" ms");
			return obj;
		}

		@Override
		public Object remove(Object key) {
			return positionCache.remove(key);
		}

		@Override
		public void putAll(Map m) {
			positionCache.putAll(m);
		}

		@Override
		public void clear() {
			positionCache.clear();
			
		}

		@Override
		public Set keySet() {
			return positionCache.keySet();
		}

		@Override
		public Collection values() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map getAll(Collection arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean lock(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean lock(Object arg0, long arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean unlock(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void addIndex(ValueExtractor arg0, boolean arg1, Comparator arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Set entrySet(Filter arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set entrySet(Filter arg0, Comparator arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set keySet(Filter arg0) {
			return positionCache.keySet(arg0);
		}

		@Override
		public void removeIndex(ValueExtractor arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object aggregate(Collection arg0, EntryAggregator arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object aggregate(Filter arg0, EntryAggregator arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object invoke(Object key, EntryProcessor proc) {
			final TransactionalEntryProcessor txproc = (TransactionalEntryProcessor) proc;
			
			String txId = getNewTxId();
			
			txproc.setTxId(txId);
			Stopwatch stopwatch = new Stopwatch().start();
			Object obj = null;
			try{
				obj = positionCache.invoke(key, proc);
			} catch(RuntimeException e) {
				stopwatch.stop();
				long millis = stopwatch.elapsedMillis();
				log.error(member + " [tx="+txId+"] invoke("+key+", "+proc+") raised an exception:"+e.getMessage()+" lasted: "+millis +" ms");
				throw e;
			} finally {
			}
			stopwatch.stop();
			long millis = stopwatch.elapsedMillis();
			log.info(member +" [tx=" +txId +"] invoke("+key+", "+proc+") lasted: "+millis +" ms");
			
			

			return obj;
			
		}

		@Override
		public Map invokeAll(final Collection keys, final EntryProcessor proc) {
			final TransactionalEntryProcessor txproc = (TransactionalEntryProcessor) proc;
			
			String txId = getNewTxId();
			
			txproc.setTxId(txId);
			Stopwatch stopwatch = new Stopwatch().start();
			Map obj = positionCache.invokeAll(keys, proc);
			stopwatch.stop();

			long millis = stopwatch.elapsedMillis();
			
			log.info(member +" [tx=" +txId +"] invokeAll("+keys+", "+proc+") lasted: "+millis +" ms");
			return obj;
		}



		@Override
		public Map invokeAll(Filter arg0, EntryProcessor arg1) {
			return positionCache.invokeAll(arg0, arg1);
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getCacheName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CacheService getCacheService() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isActive() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object put(Object arg0, Object arg1, long arg2) {
			return positionCache.put(arg0, arg1, arg2);
		}

		@Override
		public void release() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addMemberListener(MemberListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Cluster getCluster() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServiceInfo getInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Serializer getSerializer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getUserContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeMemberListener(MemberListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setUserContext(Object arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addServiceListener(ServiceListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeServiceListener(ServiceListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void configure(XmlElement arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isRunning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void shutdown() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ClassLoader getContextClassLoader() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setContextClassLoader(ClassLoader arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void execute(Invocable arg0, Set arg1, InvocationObserver arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Map query(Invocable invocable, Set arg1) {
			final FreeMemoryAgent txInvoc = (FreeMemoryAgent) invocable;
			
			String txId = getNewTxId();
			
			txInvoc.setTxId(txId);
			Stopwatch stopwatch = new Stopwatch().start();
			Map obj = null;
			try{
				obj = invocationService.query(txInvoc, arg1);
			} catch(RuntimeException e) {
				stopwatch.stop();
				long millis = stopwatch.elapsedMillis();
				log.error(member + " [tx="+txId+"] invoke("+txInvoc+", "+arg1+") raised an exception:"+e.getMessage()+" lasted: "+millis +" ms");
				throw e;
			} finally {
			}
			stopwatch.stop();
			long millis = stopwatch.elapsedMillis();
			log.info(member +" [tx=" +txId +"] invoke("+txInvoc+", "+arg1+") lasted: "+millis +" ms");
			return obj;
		}
		
	}
	
	private LoggedNamedCache positionCache=new LoggedNamedCache();



	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Client().test();
	}
	
	
	public void test() {
		//positionCache = CacheFactory.getCache("positions");
		

		setData();
		testQuery();
		testEntryProc();
		testInvocationAgent();
	}
	
	public void testInvocationAgent() {
		Map<Member, Integer> map = positionCache.query(new FreeMemoryAgent(),null);
		
		
		for (Map.Entry<Member, Integer> freeMem : map.entrySet()) {
			System.out.println("Member: " + freeMem.getKey() +
								" Free: " + freeMem.getValue());
		}
	}

	
	public void testEntryProc() {
		Collection<Long> collKeys = new LinkedList<Long>();
		
		for(long i=0 ; i<15;i++) {
			collKeys.add(i);
		}
		

		
		
		positionCache.invokeAll(collKeys, new ModifyEntryProcessor(0.50));
		
		//positionCache.invoke(1, new ModifyEntryProcessor(0.20));

	}

	

	public void testQuery() {
		NamedCache positions = CacheFactory.getCache("positions");
		

		setData();

		Filter filter =  new GreaterEqualsFilter(new PofExtractor(Double.class,Position.PRICE),50.0);
		
		Set keys = positions.keySet(filter);
		
		System.out.println("--number of keys="+keys.size());
		
		Set entries = positions.entrySet(filter);
		
	
		for(Iterator iter = entries.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry)iter.next();
			Position pos = (Position)entry.getValue();
			System.out.println(pos);
		}
	}
	
	private void setData() {
		//NamedCache positions = CacheFactory.getCache("positions");
		
		System.out.println("Put new positions:");
		
		long i=0L;
		positionCache.put(i, new Position(i++,"ORCL",2.0));
		positionCache.put(i, new Position(i++,"SUN",44.0));
		positionCache.put(i, new Position(i++,"GOOGLE",150.0));
		positionCache.put(i, new Position(i++,"BNP",250.0));
		positionCache.put(i, new Position(i++,"SL",15.0));
		positionCache.put(i, new Position(i++,"AVIVA",400.0));
		positionCache.put(i, new Position(i++,"FTSE",6000.0));
		positionCache.put(i, new Position(i++,"BA",30.0));
		positionCache.put(i, new Position(i++,"TESCO",47.0));
		positionCache.put(i, new Position(i++,"SOCGEN",345.0));
		positionCache.put(i, new Position(i++,"AVIS",20.0));
		positionCache.put(i, new Position(i++,"RBS",41.0));
		positionCache.put(i, new Position(i++,"BARCAP",456.0));
		positionCache.put(i, new Position(i++,"STANDARD",123.0));
		positionCache.put(i, new Position(i++,"UNILEVER",269.0));
	}
	private void getData() {
		NamedCache positions = CacheFactory.getCache("positions");
		
		
		for (long i=0 ;i< 15 ; i++) positions.get(i);
	}

}
