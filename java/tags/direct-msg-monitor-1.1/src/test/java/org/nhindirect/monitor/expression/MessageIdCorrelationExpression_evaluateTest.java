package org.nhindirect.monitor.expression;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

public class MessageIdCorrelationExpression_evaluateTest 
{
	@Test
	public void testEvaluate_emptyDetails_assertNullId()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		Tx tx = new Tx(TxMessageType.IMF, new HashMap<String, TxDetail>());
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertNull(exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_IMFMessage_noMsgId_assertNullId()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		Tx tx = new Tx(TxMessageType.IMF, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertNull(exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_IMFMessage_msgIdExists_assertMessageIdEvaluated()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		String msgId = UUID.randomUUID().toString();
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		
		Tx tx = new Tx(TxMessageType.IMF, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertEquals(msgId, exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_MDNMessage_noParentMsgId_assertNullId()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		Tx tx = new Tx(TxMessageType.MDN, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertNull(exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_DNSMessage_noParentMsgId_assertNullId()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		Tx tx = new Tx(TxMessageType.MDN, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertNull(exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_MDNMessage_parentMsgIdExists_assertMessageIdEvaluated()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		String msgId = UUID.randomUUID().toString();
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, msgId));
		
		Tx tx = new Tx(TxMessageType.MDN, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertEquals(msgId, exp.evaluate(exchange, String.class));
	}
	
	@Test
	public void testEvaluate_DSNMessage_parentMsgIdExists_assertMessageIdEvaluated()
	{
		MessageIdCorrelationExpression exp = new MessageIdCorrelationExpression();
		
		String msgId = UUID.randomUUID().toString();
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, msgId));
		
		Tx tx = new Tx(TxMessageType.DSN, details);
		
		
		CamelContext context = mock(CamelContext.class);
		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		assertEquals(msgId, exp.evaluate(exchange, String.class));
	}
}
