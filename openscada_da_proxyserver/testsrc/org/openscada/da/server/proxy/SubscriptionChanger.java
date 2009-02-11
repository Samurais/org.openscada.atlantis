/**
 * 
 */
package org.openscada.da.server.proxy;

import java.util.concurrent.Callable;

import org.openscada.core.subscription.SubscriptionState;

final class SubscriptionChanger implements Callable<Object>
{

    private final ProxySubConnectionId connectionId;

    private final ProxyValueHolder pvh;

    SubscriptionChanger ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh )
    {
        this.connectionId = connectionId;
        this.pvh = pvh;
    }

    public Object call () throws Exception
    {
        final SubscriptionState[] states = SubscriptionState.values ();

        for ( int i = 0; i < TestApplication1.getCount (); i++ )
        {
            TestApplication1.operations++;
            this.pvh.updateSubscriptionState ( this.connectionId, states[i % states.length], null );
        }
        this.pvh.updateSubscriptionState ( this.connectionId, SubscriptionState.CONNECTED, null );

        return null;
    }
}