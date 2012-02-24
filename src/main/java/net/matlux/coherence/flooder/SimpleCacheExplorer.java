
package net.matlux.coherence.flooder;


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

import java.util.Iterator;

/**
* A command line tool utilizing simple cache operations.
*
* @author ag 2003.07.25
* @author jh 2005.06.08
*/
public class SimpleCacheExplorer
    {
    /**
    * Entry point.
    *
    * @param asArg  command line arguments
    */
    public static void main(String[] asArg)
            throws Exception
        {
        // Get the cache implementation
        NamedCache cache = CacheFactory.getCache("perfVirtualCache");

        BufferedReader in  = new BufferedReader(new InputStreamReader(System.in));
        PrintStream    out = System.out;
        
        while (true)
            {
            out.println();
            out.print("Command: ");
            out.flush();

            String sLine = in.readLine();
            if (sLine != null && sLine.trim().length() > 0)
                {
                out.println();

                String[] asPart = Base.parseDelimitedString(sLine.trim(), ' ');
                int      cParts = asPart.length;
                String   sCmd   = asPart[0];
                try
                    {
                    if (   sCmd.equalsIgnoreCase("bye")
                        || sCmd.equalsIgnoreCase("exit")
                        || sCmd.equalsIgnoreCase("q")
                        || sCmd.equalsIgnoreCase("quit"))
                        {
                        CacheFactory.shutdown();
                        out.println(">> all cache services are shut down");
                        break;
                        }
                    else if (sCmd.equalsIgnoreCase("clear"))
                        {
                        cache.clear();
                        out.println(">> cleared");
                        }
                    else if (sCmd.equalsIgnoreCase("get"))
                        {
                        if (cParts < 2)
                            {
                            out.println("get <key>");
                            }
                        else
                            {
                            String sKey = asPart[1];
                            out.println(">> Value is " + cache.get(sKey));
                            }
                        }
                    else if (sCmd.equalsIgnoreCase("info"))
                        {
                        printConfigInfo(cache);
                        }
                    else if (sCmd.equalsIgnoreCase("keys"))
                        {
                        for (Iterator iter = cache.keySet().iterator(); iter.hasNext(); )
                            {
                            out.println(">> " + iter.next());
                            }
                        }
                   else if (sCmd.equalsIgnoreCase("put"))
                        {
                        if (cParts < 3)
                            {
                            out.println("put <key> <value>");
                            }
                        else
                            {
                            String sKey   = asPart[1];
                            String sValue = asPart[2];
                            
                            cache.put(sKey, sValue);
                            out.println(">> Put Complete");
                            }
                        }
                    else if (sCmd.equalsIgnoreCase("remove"))
                        {
                        if (cParts < 2)
                            {
                            out.println("remove <key>");
                            }
                        else
                            {
                            String sKey = asPart[1];

                            cache.remove(sKey);
                            out.println(">> Remove Complete");
                            }
                        }
                    else if (sCmd.equalsIgnoreCase("help"))
                        {
                        out.println("clear");
                        out.println("get <key>");
                        out.println("keys");
                        out.println("info");
                        out.println("put <key> <value>");
                        out.println("quit");
                        out.println("remove <key>");
                        }
                    else
                        {
                        out.println("unknown command: " + sCmd);
                        out.println("try \"help\"");
                        }
                    }
                catch (Throwable e)
                    {
                    // log the exception and continue
                    out.println(">> Exception during cache operation:");
                    out.println(e);
                    }
                }
            }
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
    }
