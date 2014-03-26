package org.nhindirect.ldap;

import javax.naming.NamingException;

import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapProducer;
import org.apache.directory.server.core.schema.bootstrap.BootstrapRegistries;
import org.apache.directory.server.core.schema.bootstrap.ProducerCallback;
import org.apache.directory.server.core.schema.bootstrap.ProducerTypeEnum;

public class PrivkeyMatchingRuleUseProducer extends AbstractBootstrapProducer
{
    public PrivkeyMatchingRuleUseProducer()
    {
        super( ProducerTypeEnum.MATCHING_RULE_USE_PRODUCER );
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
    }
}
