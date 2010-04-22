/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.net.base.data;

public class Message
{
    public final static int CC_UNKNOWN_COMMAND_CODE = 0x00000001;

    public final static int CC_FAILED = 0x00000002;

    public final static int CC_ACK = 0x00000003;

    public final static int CC_PING = 0x00000010;

    public final static int CC_PONG = 0x00000011;

    public final static String FIELD_ERROR_INFO = "error-info";

    private int _commandCode = 0;

    private long _sequence = 0;

    private long _replySequence = 0;

    private long _timestamp = System.currentTimeMillis ();

    private MapValue _values = null;

    // ctors

    public Message ()
    {
        super ();
        this._values = new MapValue ();
    }

    public Message ( final int commandCode )
    {
        super ();
        this._commandCode = commandCode;
        this._values = new MapValue ();
    }

    public Message ( final int commandCode, final long replySequence )
    {
        super ();
        this._commandCode = commandCode;
        this._replySequence = replySequence;
        this._values = new MapValue ();
    }

    // methods

    public int getCommandCode ()
    {
        return this._commandCode;
    }

    public void setCommandCode ( final int commandCode )
    {
        this._commandCode = commandCode;
    }

    public long getSequence ()
    {
        return this._sequence;
    }

    public void setSequence ( final long sequence )
    {
        this._sequence = sequence;
    }

    public MapValue getValues ()
    {
        return this._values;
    }

    public void setValues ( final MapValue values )
    {
        this._values = values;
    }

    public long getReplySequence ()
    {
        return this._replySequence;
    }

    public void setReplySequence ( final long replySequence )
    {
        this._replySequence = replySequence;
    }

    // tool methods
    public void setValue ( final String name, final Value value )
    {
        this._values.put ( name, value );
    }

    public void setValue ( final String name, final String value )
    {
        this._values.put ( name, new StringValue ( value ) );
    }

    public void unsetValue ( final String name )
    {
        this._values.remove ( name );
    }

    public long getTimestamp ()
    {
        return this._timestamp;
    }

    public void setTimestamp ( final long timestamp )
    {
        this._timestamp = timestamp;
    }

}
