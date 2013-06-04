package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

public class VariableTimeoutCondition_getTimeoutTest 
{
	@SuppressWarnings("unchecked")
	@Test
	public void testGetTimeoutTest_nullMessageToTrack_assertUsesGeneralCondtion()
	{
		TxTimeoutCondition timelyCond = mock(TxTimeoutCondition.class);
		TxTimeoutCondition generalCond = mock(TxTimeoutCondition.class);
		
		VariableTimeoutCondition cond = new VariableTimeoutCondition(timelyCond, generalCond);
		VariableTimeoutCondition spy = spy(cond);
		
		when(spy.getMessageToTrack((Collection<Tx>)any())).thenReturn(null);
		
		assertEquals(0, spy.getTimeout(new ArrayList<Tx>(), 1000));
		
		verify(timelyCond, never()).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
		verify(generalCond, times(1)).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetTimeoutTest_nonTimelyMessage_assertUsesGeneralCondtion()
	{
		TxTimeoutCondition timelyCond = mock(TxTimeoutCondition.class);
		TxTimeoutCondition generalCond = mock(TxTimeoutCondition.class);
		
		VariableTimeoutCondition cond = new VariableTimeoutCondition(timelyCond, generalCond);
		VariableTimeoutCondition spy = spy(cond);
		
		Tx tx = mock(Tx.class);
		
		when(spy.getMessageToTrack((Collection<Tx>)any())).thenReturn(tx);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(false);		
		
		assertEquals(0, spy.getTimeout(new ArrayList<Tx>(), 1000));
		
		verify(timelyCond, never()).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
		verify(generalCond, times(1)).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetTimeoutTest_timelyMessage_assertUsesTimelyCondtion()
	{
		TxTimeoutCondition timelyCond = mock(TxTimeoutCondition.class);
		TxTimeoutCondition generalCond = mock(TxTimeoutCondition.class);
		
		VariableTimeoutCondition cond = new VariableTimeoutCondition(timelyCond, generalCond);
		VariableTimeoutCondition spy = spy(cond);
		
		Tx tx = mock(Tx.class);
		
		when(spy.getMessageToTrack((Collection<Tx>)any())).thenReturn(tx);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(true);		
		
		assertEquals(0, spy.getTimeout(new ArrayList<Tx>(), 1000));
		
		verify(timelyCond, times(1)).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
		verify(generalCond, never()).getTimeout((ArrayList<Tx>)any(), eq((long)1000));
	}
}
