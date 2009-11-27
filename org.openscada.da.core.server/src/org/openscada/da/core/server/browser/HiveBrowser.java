/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.core.server.browser;

import org.openscada.core.InvalidSessionException;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.Session;
import org.openscada.utils.concurrent.NotifyFuture;

public interface HiveBrowser
{
    public abstract void subscribe ( Session session, Location location ) throws NoSuchFolderException, InvalidSessionException;

    public abstract void unsubscribe ( Session session, Location location ) throws NoSuchFolderException, InvalidSessionException;

    public NotifyFuture<Entry[]> startBrowse ( Session session, Location location ) throws InvalidSessionException;
}