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

package org.openscada.ae.slave.inject;

import java.sql.SQLException;

import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.jdbc.DataSourceFactoryTracker;
import org.openscada.utils.osgi.jdbc.DataSourceHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private DataSourceFactoryTracker tracker;

    private EventInjector injector;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        final String driver = DataSourceHelper.getDriver ( "org.openscada.ae.slave.inject", DataSourceHelper.DEFAULT_PREFIX );
        this.tracker = new DataSourceFactoryTracker ( bundleContext, driver, new SingleServiceListener<DataSourceFactory> () {

            @Override
            public void serviceChange ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                setService ( service );
            }
        } );
        this.tracker.open ();
    }

    protected void setService ( final DataSourceFactory service )
    {
        if ( this.injector != null )
        {
            this.injector.dispose ();
            this.injector = null;
        }

        if ( service != null )
        {
            try
            {
                this.injector = new EventInjector ( service, DataSourceHelper.getDataSourceProperties ( "org.openscada.ae.slave.inject", DataSourceHelper.DEFAULT_PREFIX ), Integer.getInteger ( "org.openscada.ae.slave.inject.loopDelay", 10 * 1000 ) );
            }
            catch ( final SQLException e )
            {
                logger.warn ( "Failed to start event injector", e ); //$NON-NLS-1$
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.tracker.close ();

        if ( this.injector != null )
        {
            this.injector.dispose ();
            this.injector = null;
        }

        Activator.context = null;
    }

}
