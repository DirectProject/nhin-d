package org.nhindirect.common.audit.impl;

import java.util.Collection;

import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;

public class ExceptionAuditor implements Auditor
{

	@Override
	public void audit(String principal, AuditEvent event)
	{
		audit(principal, event, null);
	}

	@Override
	public void audit(String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{
		throw new RuntimeException("Something bad happened.  Deal with it.");
	}
}
