package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsManagerUtils;
import org.nhindirect.stagent.options.OptionsParameter;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_outboundPolicyForInboundTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			if (getUseOutboundPolicy() != null)
				params.put("UseOutgoingPolicyForIncomingNotifications", getUseOutboundPolicy());
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void setupMocks() 
		{
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void tearDownMocks()
		{
			OptionsManagerUtils.clearOptionsManagerOptions();
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions();
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getUseOutboundPolicy()
		{
			return "";
		}
		
		protected void doAssertions() throws Exception
		{
			
		}			
	}
	
	public void testOutboundPolicyForInbound_emptyMailetParamAndNullOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}
	
	public void testOutboundPolicyForInbound_nullMailetParamAndNullOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected String getUseOutboundPolicy()
			{
				return null;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}
	
	public void testOutboundPolicyForInbound_falseMailetParamAndNullOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected String getUseOutboundPolicy()
			{
				return "false";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}	
	
	public void testOutboundPolicyForInbound_invalidMailetParamAndNullOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected String getUseOutboundPolicy()
			{
				return "bogus";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}	
	
	public void testOutboundPolicyForInbound_trueMailetParamAndNullOptions_assertTrue() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			protected String getUseOutboundPolicy()
			{
				return "true";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("true", param.getParamValue());
						
			}				
		}.perform();
	}
	
	public void testOutboundPolicyForInbound_trueMailetParamAndFalseOptions_assertTrue() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			public void setupMocks()
			{
				super.setupMocks();
				
				OptionsManager.getInstance().setOptionsParameter(
						new OptionsParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS, "false"));
			}
			
			@Override
			protected String getUseOutboundPolicy()
			{
				return "true";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("true", param.getParamValue());
						
			}				
		}.perform();
	}	
	
	public void testOutboundPolicyForInbound_falseMailetParamAndTrueOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			public void setupMocks()
			{
				super.setupMocks();
				
				OptionsManager.getInstance().setOptionsParameter(
						new OptionsParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS, "false"));
			}
			
			@Override
			protected String getUseOutboundPolicy()
			{
				return "false";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}	
	
	public void testOutboundPolicyForInbound_nullMailetParamAndTrueOptions_assertTrue() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			public void setupMocks()
			{
				super.setupMocks();
				
				OptionsManager.getInstance().setOptionsParameter(
						new OptionsParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS, "true"));
			}
			
			@Override
			protected String getUseOutboundPolicy()
			{
				return null;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("true", param.getParamValue());
						
			}				
		}.perform();
	}	
	
	public void testOutboundPolicyForInbound_nullMailetParamAndFalseOptions_assertFalse() throws Exception 
	{
		new TestPlan() 
		{	
			@Override
			public void setupMocks()
			{
				super.setupMocks();
				
				OptionsManager.getInstance().setOptionsParameter(
						new OptionsParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS, "false"));
			}
			
			@Override
			protected String getUseOutboundPolicy()
			{
				return null;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS);
				assertNotNull(param);
				assertEquals("false", param.getParamValue());
						
			}				
		}.perform();
	}		
}
