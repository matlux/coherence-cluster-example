
package net.matlux.coherence.flooder;


import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.DefaultConfigurableCacheFactory.CacheInfo;
import com.tangosol.net.Member;
import com.tangosol.net.NamedCache;

import com.tangosol.run.xml.XmlElement;

import com.tangosol.util.Base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * A command line tool utilizing simple cache operations.
 *
 * @author ag 2003.07.25
 * @author jh 2005.06.08
 */ 
public class CacheFlooder
{
	/**
	 * Entry point.
	 *
	 * @param asArg  command line arguments
	 */
	public static void main(String[] args)
	throws Exception
	{
		
		JSAP jsap = new JSAP();
		FlaggedOption putNumberArg 		= new FlaggedOption("putNumberArg"	, JSAP.INTEGER_PARSER, "10", true, 'n', "putNumber", "Number of put keys to write"); 
		FlaggedOption cachePayloadArg 		= new FlaggedOption("cachePayloadArg"	, JSAP.INTEGER_PARSER, "10", true, 'p', "cachePayload", "Number of bytes to write inside each cache item"); 

        
		try {


			jsap.registerParameter(putNumberArg);
			jsap.registerParameter(cachePayloadArg);

			
			
		} catch (JSAPException e) {
			e.printStackTrace();
			System.exit(1);

		}

		JSAPResult config = jsap.parse(args);

        if (!config.success()) {
        	
        	for(Iterator it = config.getErrorMessageIterator();it.hasNext();) {
        		String msg = (String)it.next ();
        		System.err.println("Parsing error: "
                        + msg);
        	}
            
            System.err.println();
            System.err.println("Usage: java "
                                + CacheFlooder.class.getName());
            System.err.println("                "
                                + jsap.getUsage());
            System.err.println();
            // show full help as well
            System.err.println(jsap.getHelp());
            

            System.exit(1);
        }
		
		
		int putNumber = config.getInt(putNumberArg.getID());
		int payload = config.getInt(cachePayloadArg.getID());

		
		// Get the cache implementation
		NamedCache cache = CacheFactory.getCache("perfVirtualCache");

		BufferedReader in  = new BufferedReader(new InputStreamReader(System.in));
		PrintStream    out = System.out;

		String sValue = buildPayload( payload );
		PerformanceTimer putClock = new PerformanceTimer();
		for (int i=0 ; i<putNumber;i++)
		{


			try
			{
				

				cache.put(Integer.toString(i), sValue);
				putClock.Tick();
				out.println(">> Put(" + i +","+  sValue.length() +" bytes) Complete");
				//out.println(">> Put(" + i +","+  sValue +") Complete");


			}
			catch (Throwable e)
			{
				// log the exception and continue
				out.println(">> Exception during cache operation:");
				out.println(e);
			}
		}

		CacheFactory.shutdown();
		
		Date now = new Date();

		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat(PerformanceTimer.dateFormatXMLPattern);
        String time = formatter.format(now);

		System.out.print(putClock.getTaskExcelStats());

		System.out.print("Time=" + time + "\n");
		System.out.print("Submission rate=" + putClock.getAvgRate() + " item/s\n");
		System.out.print("Submission avg period=" + putClock.getAvgPeriod() + " ms\n");
		System.out.print("total client Time=" + putClock.getTotalPeriod() + " ms\n");
		System.out.print("total payload=" + putNumber * payload + " bytes\n");
		System.out.print("avg throughput=" + (((long)putNumber * (long)payload * 1000L) / (long)putClock.getTotalPeriod()) + " byte/s\n");


		//System.out.print("Connection Time=" + driverInitClock.getInstantPeriod() + "\n");
		//System.out.print("destroy Time=" + destroyClock.getInstantPeriod() + "\n");
		//System.out.print("total client Time=" + totalClock.getInstantPeriod() + "\n");

		
	}

	/**
	 * Get and print the cache configuration and current cluster membership information.
	 * <p>
	 * These features are a little more advanced then the simple cache manipulation, 
	 * and are included here to make the example more usable and complete.
	 * As you can see - it is rather straightforward, so by all means explore it. 
	 * You have been warned ;-)
	 */
	public static void printConfigInfo(NamedCache cache)
	{
		PrintStream    out = System.out;
		// print the information about the cache configuration
		DefaultConfigurableCacheFactory factory = 
			(DefaultConfigurableCacheFactory) CacheFactory.getConfigurableCacheFactory();
		String     sCacheName = cache.getCacheName();
		CacheInfo  cacheInfo  = factory.findSchemeMapping(sCacheName); 
		XmlElement xmlConf    = factory.resolveScheme(cacheInfo);

		out.println(">> " + sCacheName + " cache is using a cache-scheme named '" 
				+ cacheInfo.getSchemeName() + "' defined as:");
		out.println(xmlConf);
		out.println();

		// print the information about cluster members
		Cluster cluster    = CacheFactory.getCluster();
		Member  memberThis = cluster.getLocalMember();

		out.println(">> The following member nodes are currently active:");
		for (Iterator iter = cluster.getMemberSet().iterator(); iter.hasNext(); )
		{
			Member member = (Member) iter.next();

			out.println(member + 
					(member.equals(memberThis) ? " <-- this node" : ""));
		}
	}
	
    private static String buildPayload(int len) {
    	len = len/2;
    	StringBuffer inputPayloadBuffer = new StringBuffer( len );
    	for ( int i = 0; i < len; ++i ) 
    		inputPayloadBuffer.append( (char) 'A' + ( i % 26 ) );
    	return inputPayloadBuffer.toString();
     }
}
