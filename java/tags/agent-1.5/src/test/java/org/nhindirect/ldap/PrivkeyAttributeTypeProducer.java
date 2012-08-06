package org.nhindirect.ldap;

import java.util.ArrayList;

import javax.naming.NamingException;

import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapRegistries;
import org.apache.directory.server.core.schema.bootstrap.ProducerCallback;
import org.apache.directory.server.core.schema.bootstrap.ProducerTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;

public class PrivkeyAttributeTypeProducer extends AbstractBootstrapProducer
{

    public PrivkeyAttributeTypeProducer()
    {
        super( ProducerTypeEnum.ATTRIBUTE_TYPE_PRODUCER );
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
        ArrayList<String> names = new ArrayList<String>();
        BootstrapAttributeType attributeType;

        
        // --------------------------------------------------------------------
        // AttributeType 0.9.2342.19200300.100.1.3 Mail
        // --------------------------------------------------------------------

        attributeType = newAttributeType( "0.9.2342.19200300.100.1.3.1", registries );
        attributeType.setDescription( "User Email" );
        attributeType.setCanUserModify( ! false );
        attributeType.setSingleValue( true );
        attributeType.setCollective( false );
        attributeType.setObsolete( false );
        attributeType.setLength( 4096 );
        attributeType.setUsage( UsageEnum.getUsage( "userApplications" ) );
        attributeType.setEqualityId( "caseIgnoreMatch" );
        attributeType.setSyntaxId( "1.3.6.1.4.1.1466.115.121.1.15" );
        names.clear();
        names.add( "email");
        attributeType.setNames( ( String[] ) names.toArray( EMPTY ) );
        cb.schemaObjectProduced( this, "0.9.2342.19200300.100.1.3.1", attributeType );

 
        // --------------------------------------------------------------------
        // AttributeType 1.2.840.113549.1.12 PKCS12 Key Store
        // --------------------------------------------------------------------

        attributeType = newAttributeType( "1.2.840.113549.1.12", registries );
        attributeType.setDescription( "User Private Key Store" );
        attributeType.setCanUserModify( ! false );
        attributeType.setSingleValue( false );
        attributeType.setCollective( false );
        attributeType.setObsolete( false );
        attributeType.setLength( 32 );
        attributeType.setUsage( UsageEnum.getUsage( "userApplications" ) );
        attributeType.setEqualityId( "caseIgnoreIA5Match" );
        attributeType.setSyntaxId( "1.3.6.1.4.1.1466.115.121.1.15" );
        names.clear();
        names.add( "privKeyStore" );
        attributeType.setNames( ( String[] ) names.toArray( EMPTY ) );
        cb.schemaObjectProduced( this, "1.2.840.113549.1.12", attributeType );

 
    }


}
