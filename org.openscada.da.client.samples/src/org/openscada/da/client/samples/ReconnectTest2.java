/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.samples;

import org.apache.log4j.Logger;
import org.openscada.core.client.AutoReconnectController;

/**
 * Sample showing how to subscribe for events only
 * <br>
 * The example shows how to create a new connection, connect, and listen for events coming
 * in for a period of 10 seconds.
 * <br>
 * We will listen to the <em>time</em> data item of the test server. The item is an input
 * item and will provided the current unix timestamp every second.
 * 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class ReconnectTest2 extends SampleBase
{

    private static Logger logger = Logger.getLogger ( ReconnectTest2.class );

    private final AutoReconnectController controller;

    public ReconnectTest2 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
        this.controller = new AutoReconnectController ( this.connection, 2000 );
    }

    @Override
    public void connect () throws Exception
    {
        this.controller.connect ();
    }

    @Override
    public void disconnect ()
    {
        this.controller.disconnect ();
    }

    @Override
    protected void finalize () throws Throwable
    {
        logger.info ( "Finalized" );
        super.finalize ();
    }

    public static void main ( final String[] args ) throws Exception
    {
        String uri = null;
        String className = null;

        if ( args.length > 0 )
        {
            uri = args[0];
        }
        if ( args.length > 1 )
        {
            className = args[1];
        }

        ReconnectTest2 s = null;
        try
        {
            s = new ReconnectTest2 ( uri, className );
            s.connect ();
            SampleBase.sleep ( 2000 );
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
        finally
        {
            if ( s != null )
            {
                s.disconnect ();
            }
        }
        s = null;

        while ( true )
        {
            logger.info ( "Sleep" );
            SampleBase.sleep ( 2000 );
            System.gc ();
        }

        // logger.info ( "Finished" );
    }
}