/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.proxy;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executor;

import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryState;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.data.ValueInformation;
import org.openscada.hd.server.storage.common.DataFactory;
import org.openscada.hd.server.storage.common.QueryDataBuffer;

public class ProxyQueryBuffer extends QueryDataBuffer
{

    private static class Data extends QueryDataBuffer.Data
    {

        public Data ( final Date start, final Date end )
        {
            super ( start, end );
        }

        private long entryCount;

        public void setEntryCount ( final long entryCount )
        {
            if ( this.entryCount != entryCount )
            {
                this.changed = true;
                this.entryCount = entryCount;
            }
        }

        @Override
        public long getEntryCount ()
        {
            return this.entryCount;
        }

        public void apply ( final Data data )
        {
            super.apply ( data );
            setEntryCount ( data.entryCount );
        }

    }

    private Data[] data;

    private QueryParameters parameters;

    public ProxyQueryBuffer ( final QueryListener listener, final QueryParameters parameters, final Executor executor )
    {
        super ( listener, executor, null, null );
        setParameters ( parameters );
    }

    @Override
    protected Data[] getData ()
    {
        return this.data;
    }

    public synchronized void close ()
    {
        if ( this.state == QueryState.DISCONNECTED )
        {
            return;
        }
        notifyStateUpdate ( QueryState.DISCONNECTED );
    }

    public synchronized void render ( final List<? extends QueryDataHolder> holders )
    {
        if ( this.state == QueryState.DISCONNECTED )
        {
            return;
        }

        final Data[] data = new Data[this.data.length];
        fillDataCells ( data, this.parameters.getStartTimestamp (), this.parameters.getEndTimestamp (), new DataFactory () {

            @Override
            public QueryDataBuffer.Data create ( final Date start, final Date end )
            {
                return new Data ( start, end );
            }
        } );

        for ( final QueryDataHolder holder : holders )
        {
            final List<ValueInformation> information = holder.getValueInformation ();
            if ( information == null )
            {
                continue;
            }

            final HashMap<String, List<Double>> values = holder.getValues ();
            if ( values == null )
            {
                continue;
            }

            if ( information.size () != data.length )
            {
                continue;
            }

            // FIXME: this should work for all data types

            final List<Double> avg = values.get ( "AVG" );
            if ( avg == null )
            {
                continue;
            }
            final List<Double> max = values.get ( "MAX" );
            if ( max == null )
            {
                continue;
            }
            final List<Double> min = values.get ( "MIN" );
            if ( min == null )
            {
                continue;
            }

            // merge by quality
            for ( int i = 0; i < Math.min ( data.length, information.size () ); i++ )
            {

                final ValueInformation entryInformation = information.get ( i );
                final Double entryAvg = avg.get ( i );
                final Double entryMax = max.get ( i );
                final Double entryMin = min.get ( i );

                if ( entryAvg == null || entryMax == null || entryMin == null || entryInformation == null )
                {
                    // data is not available
                    continue;
                }

                if ( data[i].getQuality () >= entryInformation.getQuality () )
                {
                    // quality is below current best
                    continue;
                }

                data[i].setEntryCount ( entryInformation.getSourceValues () );
                data[i].setQuality ( entryInformation.getQuality () );
                data[i].setManual ( entryInformation.getManualPercentage () );

                data[i].setAverage ( entryAvg );
                data[i].setMax ( entryMax );
                data[i].setMin ( entryMin );
            }
        }

        // apply
        for ( int i = 0; i < Math.min ( data.length, this.data.length ); i++ )
        {
            this.data[i].apply ( data[i] );
        }

        notifyData ( 0, this.data.length );

        notifyStateUpdate ( updateState ( holders ) );
    }

    private QueryState updateState ( final List<? extends QueryDataHolder> holders )
    {
        for ( final QueryDataHolder holder : holders )
        {
            if ( holder.getState () == QueryState.LOADING )
            {
                return QueryState.LOADING;
            }
        }

        return QueryState.COMPLETE;
    }

    public synchronized void changeParameters ( final QueryParameters parameters )
    {
        if ( this.state == QueryState.DISCONNECTED )
        {
            return;
        }

        setParameters ( parameters );
        notifyData ( 0, this.data.length );
    }

    private void setParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;
        this.data = new Data[parameters.getNumberOfEntries ()];

        fillDataCells ( this.data, parameters.getStartTimestamp (), parameters.getEndTimestamp (), new DataFactory () {

            @Override
            public QueryDataBuffer.Data create ( final Date start, final Date end )
            {
                return new Data ( start, end );
            }
        } );

        notifyParameterUpdate ( parameters, new LinkedHashSet<String> ( Arrays.asList ( "AVG", "MIN", "MAX" ) ) );
    }
}
