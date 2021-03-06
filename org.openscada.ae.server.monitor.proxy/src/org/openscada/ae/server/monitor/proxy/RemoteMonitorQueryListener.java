/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.monitor.proxy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.openscada.ae.client.MonitorListener;
import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.ae.data.MonitorStatusInformation;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.core.connection.provider.ConnectionTracker.Listener;
import org.openscada.core.data.SubscriptionState;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteMonitorQueryListener extends AbstractMonitorQueryListener implements Listener, MonitorListener
{

    private final static Logger logger = LoggerFactory.getLogger ( RemoteMonitorQueryListener.class );

    private final ConnectionIdTracker tracker;

    private ConnectionService connection;

    private final String monitorQueryId;

    public RemoteMonitorQueryListener ( final BundleContext context, final String connectionId, final String monitorQueryId, final ProxyMonitorQuery proxyMonitorQuery, final Lock lock )
    {
        super ( proxyMonitorQuery, lock, connectionId + "#" + monitorQueryId );
        logger.info ( "Creating new listener - connection: {}, query: {}", connectionId, monitorQueryId );

        this.monitorQueryId = monitorQueryId;

        this.tracker = new ConnectionIdTracker ( context, connectionId, this, ConnectionService.class );
        this.tracker.open ();
    }

    @Override
    public void dispose ()
    {
        this.tracker.close ();

        super.dispose ();
    }

    @Override
    public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
    {
        logger.debug ( "Setting connection: {}", connectionService );

        this.lock.lock ();
        try
        {
            if ( this.connection != null )
            {
                this.connection.getConnection ().setMonitorListener ( this.monitorQueryId, null );
                clearAll ();
            }

            this.connection = (ConnectionService)connectionService;

            if ( this.connection != null )
            {
                this.connection.getConnection ().setMonitorListener ( this.monitorQueryId, this );
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    @Override
    public void statusChanged ( final SubscriptionState state )
    {
        logger.info ( "State of {} changed: {}", this.info, state );
        switch ( state )
        {
            case DISCONNECTED:
                //$FALL-THROUGH$
            case GRANTED:
                clearAll ();
                break;
            case CONNECTED:
                break;
        }
    }

    @Override
    public void dataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        handleDataChanged ( addedOrUpdated, removed, full );
    }
}