package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Matchers.any;

import java.util.Collection;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxCompletionCondition;

public class VariableCompletionCondition_isCompleteTest 
{

	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_nullMessageToTrack_assertNull()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);

		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
		VariableCompletionCondition spy = spy(cond);
		
		when(spy.getMessageToTrackInternal((Collection<Tx>)any())).thenReturn(null);

		assertFalse(spy.isComplete(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_isTimely_assertComplete()
	{
		TxCompletionCondition timelyCond = mock(TxCompletionCondition.class);
		when(timelyCond.isComplete((Collection<Tx>)any())).thenReturn(true);
		
		TxCompletionCondition generalCond = mock(TxCompletionCondition.class);
		
		VariableCompletionCondition cond = new VariableCompletionCondition(timelyCond, generalCond);
		VariableCompletionCondition spy = spy(cond);
		
		Tx msgToTrack = mock(Tx.class);
		
		when(spy.getMessageToTrackInternal((Collection<Tx>)any())).thenReturn(msgToTrack);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(true);
		
		assertTrue(spy.isComplete(null));
		
		verify(timelyCond, times(1)).isComplete((Collection<Tx>)any());
		verify(generalCond, never()).isComplete((Collection<Tx>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_isTimely_assertNotComplete()
	{
		TxCompletionCondition timelyCond = mock(TxCompletionCondition.class);
		
		
		TxCompletionCondition generalCond = mock(TxCompletionCondition.class);
		when(generalCond.isComplete((Collection<Tx>)any())).thenReturn(true);
		
		VariableCompletionCondition cond = new VariableCompletionCondition(timelyCond, generalCond);
		VariableCompletionCondition spy = spy(cond);
		
		Tx msgToTrack = mock(Tx.class);
		
		when(spy.getMessageToTrackInternal((Collection<Tx>)any())).thenReturn(msgToTrack);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(true);
		
		assertFalse(spy.isComplete(null));
		
		verify(timelyCond, times(1)).isComplete((Collection<Tx>)any());
		verify(generalCond, never()).isComplete((Collection<Tx>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_isNotTimely_assertComplete()
	{
		TxCompletionCondition timelyCond = mock(TxCompletionCondition.class);
		
		TxCompletionCondition generalCond = mock(TxCompletionCondition.class);
		when(generalCond.isComplete((Collection<Tx>)any())).thenReturn(true);
		
		VariableCompletionCondition cond = new VariableCompletionCondition(timelyCond, generalCond);
		VariableCompletionCondition spy = spy(cond);
		
		Tx msgToTrack = mock(Tx.class);
		
		when(spy.getMessageToTrackInternal((Collection<Tx>)any())).thenReturn(msgToTrack);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(false);
		
		assertTrue(spy.isComplete(null));
		
		verify(timelyCond, never()).isComplete((Collection<Tx>)any());
		verify(generalCond,times(1)).isComplete((Collection<Tx>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_isNotTimely_assertNotComplete()
	{
		TxCompletionCondition timelyCond = mock(TxCompletionCondition.class);
		when(timelyCond.isComplete((Collection<Tx>)any())).thenReturn(true);
		
		TxCompletionCondition generalCond = mock(TxCompletionCondition.class);

		
		VariableCompletionCondition cond = new VariableCompletionCondition(timelyCond, generalCond);
		VariableCompletionCondition spy = spy(cond);
		
		Tx msgToTrack = mock(Tx.class);
		
		when(spy.getMessageToTrackInternal((Collection<Tx>)any())).thenReturn(msgToTrack);
		when(spy.isRelAndTimelyRequired((Tx)any())).thenReturn(false);
		
		assertFalse(spy.isComplete(null));
		
		verify(timelyCond, never()).isComplete((Collection<Tx>)any());
		verify(generalCond,times(1)).isComplete((Collection<Tx>)any());
	}
}
