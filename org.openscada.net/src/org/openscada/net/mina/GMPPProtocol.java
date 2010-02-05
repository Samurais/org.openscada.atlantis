/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.net.mina;

public interface GMPPProtocol
{

    public final static int VT_STRING = 0x000000001;

    public final static int VT_LONG = 0x000000002;

    public final static int VT_DOUBLE = 0x000000003;

    public final static int VT_VOID = 0x000000004;

    public final static int VT_INTEGER = 0x000000005;

    public final static int VT_LIST = 0x000000006;

    public final static int VT_MAP = 0x000000007;

    public final static int VT_BOOLEAN = 0x000000008;

    public final int HEADER_SIZE = 4 + 8 + 8 + 8 + 4;

}