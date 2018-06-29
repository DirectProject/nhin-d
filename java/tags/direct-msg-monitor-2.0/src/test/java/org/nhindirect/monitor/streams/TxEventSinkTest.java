package org.nhindirect.monitor.streams;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = TxTestConfiguration.class)
public class TxEventSinkTest extends CamelSpringTestSupport 
{
	@Autowired
	private TxInput channels;
	
	@Autowired 
	private ObjectMapper mapper;
	
	@Autowired 
	private ProducerTemplate producerTemplate;
	
	@Test
	public void testSendTxToSink() throws Exception
	{
		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		final Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "", "");
		
		final String marshedTx = mapper.writeValueAsString(originalMessage);
		
		channels.txInput().send(withPayload(marshedTx).build());
		
		verify(producerTemplate, times(1)).sendBody(any());
	}
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-mock.xml");
    }
    
}
