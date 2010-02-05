package org.openscada.da.master.analyzer;

import java.util.concurrent.Executor;

import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.openscada.da.server.common.osgi.factory.SimpleObjectExporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ConnectionAnalyzer implements ConnectionStateListener
{

    private final DataItemFactory factory;

    private final SimpleObjectExporter<ConnectionAnalyzerStatus> exporter;

    private final ConnectionAnalyzerStatus value;

    private final ConnectionService service;

    public ConnectionAnalyzer ( final Executor executor, final BundleContext context, final ServiceReference reference, final ConnectionService service )
    {
        this.factory = new DataItemFactory ( context, executor, "org.openscada.da.master.analyzer.connectionService." + makeId ( reference ) );
        this.exporter = new SimpleObjectExporter<ConnectionAnalyzerStatus> ( ConnectionAnalyzerStatus.class, this.factory, "state" );

        this.value = new ConnectionAnalyzerStatus ();
        this.exporter.setValue ( this.value );

        this.service = service;
        service.getConnection ().addConnectionStateListener ( this );
    }

    public void dispose ()
    {
        this.service.getConnection ().removeConnectionStateListener ( this );
        this.factory.dispose ();
    }

    private static String makeId ( final ServiceReference reference )
    {
        final Object id = reference.getProperty ( Constants.SERVICE_PID );
        if ( id instanceof String )
        {
            return (String)id;
        }

        return "" + reference.getProperty ( Constants.SERVICE_ID );
    }

    public void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        this.value.setState ( state );
        this.exporter.setValue ( this.value );
    }

}