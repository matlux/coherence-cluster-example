package net.matlux.training.coherence.mat;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.filter.GreaterEqualsFilter;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}
	
	
	public static void test() {
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
	

	public static void testQuery() {
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
	
	private static void setData() {
		NamedCache positions = CacheFactory.getCache("positions");
		
		long i=0L;
		positions.put(i, new Position(i++,"ORCL",2.0));
		positions.put(i, new Position(i++,"SUN",44.0));
		positions.put(i, new Position(i++,"GOOGLE",150.0));
		positions.put(i, new Position(i++,"BNP",250.0));
		positions.put(i, new Position(i++,"SL",15.0));
		positions.put(i, new Position(i++,"AVIVA",400.0));
		positions.put(i, new Position(i++,"FTSE",6000.0));
		positions.put(i, new Position(i++,"BA",30.0));
		positions.put(i, new Position(i++,"TESCO",47.0));
		positions.put(i, new Position(i++,"SOCGEN",345.0));
		positions.put(i, new Position(i++,"AVIS",20.0));
		positions.put(i, new Position(i++,"RBS",41.0));
		positions.put(i, new Position(i++,"BARCAP",456.0));
		positions.put(i, new Position(i++,"STANDARD",123.0));
		positions.put(i, new Position(i++,"UNILEVER",269.0));
	}

}
