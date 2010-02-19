package org.openscada.da.server.exporter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.openscada.da.core.server.Hive;
import org.w3c.dom.Node;

/**
 * Create a new hive by creating a new object.
 * @author Jens Reimann
 *
 */
public class NewInstanceHiveFactory implements HiveFactory
{
    private static Logger logger = Logger.getLogger ( NewInstanceHiveFactory.class );

    public Hive createHive ( final String reference, final HiveConfigurationType configuration ) throws ConfigurationException
    {
        Node subNode = null;
        if ( configuration != null )
        {
            for ( int i = 0; i < configuration.getDomNode ().getChildNodes ().getLength (); i++ )
            {
                final Node node = configuration.getDomNode ().getChildNodes ().item ( i );
                if ( node.getNodeType () == Node.ELEMENT_NODE )
                {
                    subNode = node;
                }
            }
        }

        try
        {
            return createInstance ( reference, subNode );
        }
        catch ( final Throwable e )
        {
            throw new ConfigurationException ( "Failed to initialze hive using new instance", e );
        }
    }

    protected static Hive createInstance ( final String hiveClassName, final Node node ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        final Class<?> hiveClass = Class.forName ( hiveClassName );

        Constructor<?> ctor = null;

        if ( node != null )
        {
            logger.debug ( "We have an xml configuration node. try XML-Node ctor" );
            // if we have an xml configuration node try to use the XML ctor
            try
            {
                ctor = hiveClass.getConstructor ( Node.class );
                if ( ctor != null )
                {
                    logger.debug ( "Using XML-Node constructor" );
                    return (Hive)ctor.newInstance ( new Object[] { node } );
                }
                // fall back to standard ctor
                logger.debug ( "No XML-Node ctor found .. fall back to default" );
            }
            catch ( final InvocationTargetException e )
            {
                logger.info ( "Failed to create new instance", e.getTargetException () );
                throw e;
            }
            catch ( final Throwable e )
            {
                logger.info ( String.format ( "No XML node constructor found (%s)", e.getMessage () ) );
            }
        }
        return (Hive)hiveClass.newInstance ();
    }

}