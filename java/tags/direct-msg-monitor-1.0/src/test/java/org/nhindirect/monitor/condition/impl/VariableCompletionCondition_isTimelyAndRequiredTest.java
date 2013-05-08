package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.condition.TxCompletionCondition;

public class VariableCompletionCondition_isTimelyAndRequiredTest 
{

	@Test
	public void testIsTimelyAndRequired_nullMessage_assertFalse()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
			
		assertFalse(cond.isRelAndTimelyRequired(null));
	}
	
	@Test
	public void testIsTimelyAndRequired_emptyDetails_assertFalse()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
			
		Tx msg = new Tx(TxMessageType.IMF, new HashMap<String, TxDetail>());
		
		assertFalse(cond.isRelAndTimelyRequired(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_NoMNDOptionDetails_assertFalse()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
			
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertFalse(cond.isRelAndTimelyRequired(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_MDNOptionNotForTimely_assertFalse()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
			
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, "X-NOT-TIMELY"));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertFalse(cond.isRelAndTimelyRequired(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_MDNOptionForTimely_assertTrue()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
			
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, MDNStandard.DispositionOption_TimelyAndReliable));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertTrue(cond.isRelAndTimelyRequired(msg));
	}
}
