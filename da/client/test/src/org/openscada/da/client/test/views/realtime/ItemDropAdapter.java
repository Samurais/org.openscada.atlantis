/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.test.views.realtime;

import java.net.URISyntaxException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.test.Activator;
import org.openscada.rcp.da.client.browser.HiveConnection;
import org.openscada.rcp.da.client.browser.HiveItem;
import org.openscada.rcp.da.client.dnd.Item;
import org.openscada.rcp.da.client.dnd.ItemTransfer;

public class ItemDropAdapter extends ViewerDropAdapter
{

    public ItemDropAdapter ( final Viewer viewer )
    {
        super ( viewer );
        setFeedbackEnabled ( true );
        setSelectionFeedbackEnabled ( true );
    }

    @Override
    public boolean performDrop ( final Object data )
    {
        final Item[] items = (Item[])data;

        final ListData listData = (ListData)getViewer ().getInput ();
        final TreeViewer viewer = (TreeViewer)getViewer ();

        for ( final Item item : items )
        {
            try
            {
                dropItem ( item, listData, viewer );
            }
            catch ( final URISyntaxException e )
            {
                e.printStackTrace ();
            }
        }

        return true;
    }

    private void dropItem ( final Item item, final ListData listData, final TreeViewer viewer ) throws URISyntaxException
    {
        final ConnectionInformation connectionInformation = ConnectionInformation.fromURI ( item.getConnectionString () );

        final HiveConnection connection = Activator.getRepository ().findConnection ( connectionInformation );
        if ( connection != null )
        {
            final HiveItem hiveItem = new HiveItem ( connection, item.getId () );
            listData.add ( hiveItem );
        }
    }

    @Override
    public boolean validateDrop ( final Object target, final int operation, final TransferData transferType )
    {
        return ItemTransfer.getInstance ().isSupportedType ( transferType );
    }

}
