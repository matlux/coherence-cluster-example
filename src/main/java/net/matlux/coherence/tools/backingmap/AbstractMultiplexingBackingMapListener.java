package net.matlux.coherence.tools.backingmap;

import com.tangosol.net.BackingMapManager;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.cache.CacheEvent;
import com.tangosol.util.Binary;
import com.tangosol.util.ConverterCollections;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;
import com.tangosol.util.MultiplexingMapListener;
 
/**
 * <p>The {@link AbstractMultiplexingBackingMapListener} provides a simplified 
 * base implementation for backing {@link MapListener}s that provide real objects in a map
 * event (in normal Java representation) rather than those that use the internal 
 * Coherence format (ie: {@link Binary}s.</p>
 * 
 * <p>Backing {@link MapListener}s are embeddable {@link MapListener}s that are 
 * injected into Coherence Cache members (storage-enabled) for the purpose of
 * handling events directly in-process of the primary partitions (of distributed schemes).</p>
 * 
 * <p>They are extremely useful for performing in-process processing of events within 
 * Coherence itself.</p>
 * 
 * @author Brian Oliver (brian.oliver@oracle.com)
 */
public abstract class AbstractMultiplexingBackingMapListener extends MultiplexingMapListener {
     
    /**
     * <p>The possible causes of backing map events.</p>
     */
    public enum Cause {
        /**
         * <p><code>Regular</code> is for regular insert, updates and deletes.</p>
         */
        Regular,    
         
        /**
         * <p><code>Eviction</code> is for deletes that are due to cache
                   * eviction.</p>
         */
        Eviction,
         
        /**
         * <p><code>Distribution</code> is for insert or delete events due to
                   * coherence having 
         * to repartition data due to changes in cluster membership.</p> 
         */
        Distribution
    }
     
     
    /**
     * <p>The {@link BackingMapManagerContext} that owns this listener.  
     * (all Backing {@link MapListener}s require a {@link BackingMapManagerContext})</p>
     */
    private BackingMapManagerContext context;
     
     
    /**
     * <p>Standard Constructor</p>
     * 
     * <p>The {@link BackingMapManagerContext} will be injected by Coherence during
     * initialization and construction of the {@link BackingMapManager}.</p>
     * 
     * @param context
     */
    public AbstractMultiplexingBackingMapListener(BackingMapManagerContext context) {
        this.context = context;
    }
     
     
    /**
     * <p>The {@link BackingMapManagerContext} in which the Backing {@link MapListener}
     * is operating.</p>
     * 
     * @return {@link BackingMapManagerContext}
     */
    public BackingMapManagerContext getContext() {
        return context;
    }
     
     
    /**
     * <p>This is the standard {@link MultiplexingMapListener} event handler.  In here
     * we convert the internally formatted event into something a developer would 
     * expect if using a client-side {@link MapListener}.</p>
     * 
     * <p>After converting the internally formatted event, this method calls 
          * the abstract {@link #onBackingMapEvent(MapEvent, Cause)}
     * method that may be used to handle the actual event.</p>
     */
    protected final void onMapEvent(MapEvent mapEvent) {
         
        // convert the mapEvent (in internal format) into a real event 
                  // we can deal with
        MapEvent realMapEvent = ConverterCollections.getMapEvent(mapEvent.getMap(),  mapEvent,  context.getKeyFromInternalConverter(), context.getValueFromInternalConverter());
 
        //determine the underlying cause of the map event
        Cause cause;
        if (context.isKeyOwned(mapEvent.getKey())) {     
            cause = mapEvent instanceof CacheEvent && ((CacheEvent) mapEvent).isSynthetic() ? Cause.Eviction : Cause.Regular;
        } else {
            cause = Cause.Distribution;
        }
         
        // now call the abstract event handler with the real event 
                  // and underlying cause
        onBackingMapEvent(realMapEvent, cause);
    }
     
     
    /**
     * <p>Override this method to handle real backing map events.</p>
     * 
     * @param mapEvent A standard mapEvent (in Java format)
     * @param cause The underlying cause of the event
     */
    abstract protected void onBackingMapEvent(MapEvent mapEvent, Cause cause);
    
}