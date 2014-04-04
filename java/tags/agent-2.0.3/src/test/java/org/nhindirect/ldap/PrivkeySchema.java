package org.nhindirect.ldap;

import java.util.ArrayList;

import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;

public class PrivkeySchema extends AbstractBootstrapSchema 
{
    public PrivkeySchema()
    {
    	super( "uid=admin,ou=system", "privkey", "org.nhindirect.ldap" );
        ArrayList<String> list = new ArrayList<String>();
        list.clear();
        list.add("email");
        list.add("privKeyStore");
        setDependencies( ( String[] ) list.toArray( DEFAULT_DEPS ) );
    }
}
