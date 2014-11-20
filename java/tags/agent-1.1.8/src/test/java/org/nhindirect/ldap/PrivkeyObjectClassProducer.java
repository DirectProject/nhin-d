package org.nhindirect.ldap;

import java.util.ArrayList;

import javax.naming.NamingException;

import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapRegistries;
import org.apache.directory.server.core.schema.bootstrap.ProducerCallback;
import org.apache.directory.server.core.schema.bootstrap.ProducerTypeEnum;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;

public class PrivkeyObjectClassProducer extends AbstractBootstrapProducer
{

    public PrivkeyObjectClassProducer()
    {
        super( ProducerTypeEnum.OBJECT_CLASS_PRODUCER );
    }


    // ------------------------------------------------------------------------
    // BootstrapProducer Methods
    // ------------------------------------------------------------------------


    /**
     * @see BootstrapProducer#produce(BootstrapRegistries, ProducerCallback)
     */
    public void produce( BootstrapRegistries registries, ProducerCallback cb )
        throws NamingException
    {
        ArrayList array = new ArrayList();
        BootstrapObjectClass objectClass;

        
        // --------------------------------------------------------------------
        // ObjectClass 1.3.6.1.4.1.7165.2.2.6
        // --------------------------------------------------------------------

        objectClass = newObjectClass( "1.3.6.1.4.1.7165.2.2.6", registries );
        objectClass.setObsolete( false );

        objectClass.setDescription( "HNIN Direct " );
        // set the objectclass type
        objectClass.setType( ObjectClassTypeEnum.AUXILIARY );
        
        // set superior objectClasses
        array.clear();
        array.add( "top" );
        objectClass.setSuperClassIds( ( String[] ) array.toArray( EMPTY ) );
        
        // set must list
        array.clear();
        array.add( "email" );
        array.add( "privKeyStore" );
        //array.add( "sambaSID" );
        objectClass.setMustListIds( ( String[] ) array.toArray( EMPTY ) );
        
        // set may list
        array.clear();
        /*
        array.add( "cn" );
        array.add( "sambaLMPassword" );
        array.add( "sambaNTPassword" );
        array.add( "sambaPwdLastSet" );
        array.add( "sambaLogonTime" );
        array.add( "sambaLogoffTime" );
        array.add( "sambaKickoffTime" );
        array.add( "sambaPwdCanChange" );
        array.add( "sambaPwdMustChange" );
        array.add( "sambaAcctFlags" );
        array.add( "displayName" );
        array.add( "sambaHomePath" );
        array.add( "sambaHomeDrive" );
        array.add( "sambaLogonScript" );
        array.add( "sambaProfilePath" );
        array.add( "description" );
        array.add( "sambaUserWorkstations" );
        array.add( "sambaPrimaryGroupSID" );
        array.add( "sambaDomainName" );
        array.add( "sambaMungedDial" );
        array.add( "sambaBadPasswordCount" );
        array.add( "sambaBadPasswordTime" );
        array.add( "sambaPasswordHistory" );
        array.add( "sambaLogonHours" );
        */
        objectClass.setMayListIds( ( String[] ) array.toArray( EMPTY ) );
        
        // set names
        array.clear();
        array.add( "userPrivKey" );
        objectClass.setNames( ( String[] ) array.toArray( EMPTY ) );
        cb.schemaObjectProduced( this, "1.3.6.1.4.1.7165.2.2.6", objectClass );

    }
 }
