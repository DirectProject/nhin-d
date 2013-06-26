package org.nhindirect.config.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhind.config.CertPolicyGroupDomainReltn;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.PolicyLexicon;
import org.nhindirect.config.manager.printers.PolicyGroupPrinter;
import org.nhindirect.config.manager.printers.PolicyPrinter;
import org.nhindirect.config.manager.printers.PolicyUsagePrinter;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyParseException;


public class PolicyCommands 
{
    private static final String LIST_POLICIES_USAGE = "Lists policies in the system";
    
    private static final String IMPORT_POLICY_USAGE = "Imports a policy from a file with an optional lexicon definition." +
    		"\r\n  policyName policyDefFile [lexicon]" +
            "\r\n\t policyName: Name of the policy.  Place the policy name in quotes (\"\") if there are spaces in the name."  +   
            "\r\n\t policyDefFile: Fully qualified path and file name of the policy definition file.  Place the file name in quotes (\"\") if there are spaces in the path or name." +
            "\r\n\t [lexicon]: Optional lexicon of the policy definition.  Default to SIMPLE_TEXT_V1 if not supplied.";   
   
    private static final String DELETE_POLICY_USAGE = "Deletes a policy from the system by policy name." +
    		"\r\n  policyName " +
            "\r\n\t policyName: Name of the policy.  Place the policy name in quotes (\"\") if there are spaces in the name.";
    
    private static final String LIST_POLICY_GROUPS_USAGE = "Lists policy groups in the system";
    
    private static final String ADD_POLICY_GROUP_USAGE = "Adds policy group to the system" +
    		"\r\n  groupName " +
            "\r\n\t groupName: Name of the policy group.  Place the policy group name in quotes (\"\") if there are spaces in the name.";
    
    private static final String DELETE_POLICY_GROUP_USAGE = "Deletes a policy group from the system by policy group name." +
    		"\r\n  groupName " +
            "\r\n\t groupName: Name of the policy group.  Place the policy group name in quotes (\"\") if there are spaces in the name.";
    
    private static final String LIST_GROUP_POLICIES_USAGE = "List policies and usage within a policy group." +
    		"\r\n  groupName " +
            "\r\n\t groupName: Name of the policy group.  Place the policy group name in quotes (\"\") if there are spaces in the name.";
   
    private static final String ADD_POLICY_TO_GROUP_USAGE = "Adds an existing policy to a group with a provided usage." +
    		"\r\n  policyName groupNames policyUse incoming outgoing" +
            "\r\n\t policyName: Name of the policy to add to the group.  Place the policy name in quotes (\"\") if there are spaces in the name." +
    		"\r\n\t groupName: Name of the policy group to add the policy to.  Place the policy group name in quotes (\"\") if there are spaces in the name." +
    		"\r\n\t policyUse: Usage name of the policy in the group.  Must be one of the following values: TRUST, PRIVATE_RESOLVER, PUBLIC_RESOLVER." +
    		"\r\n\t incoming: Indicates if policy is used for incoming messages.  Must be one of the following values: true, false" +
    		"\r\n\t outgoing: Indicates if policy is used for outgoing messages.  Must be one of the following values: true, false";
    
    private static final String DELETE_POLICY_FROM_GROUP_USAGE = "Deletes an existing policy from a group." +
    		"\r\n  policyName groupName" +
            "\r\n\t policyName: Name of the policy to delete from the group.  Place the policy name in quotes (\"\") if there are spaces in the name." +
    		"\r\n\t groupName: Name of the policy group to delete the policy from.  Place the policy group name in quotes (\"\") if there are spaces in the name.";
    
    private static final String LIST_DOMAIN_POLICY_GROUPS = "List policy groups within a domain" +
    		"\r\n  domainName" +
    		"\r\n\t domainName: Name of the domain.";
    
    private static final String ADD_GROUP_TO_DOMAIN_USAGE = "Adds an existing policy group to an existing domain." +
    		"\r\n  groupName domainName" +
    		"\r\n\t groupName: Name of the policy group to add to the domain.  Place the policy group name in quotes (\"\") if there are spaces in the name." +
			"\r\n\t domainName: Name of the domain to add the group to."; 
    
    private static final String DELETE_GROUP_FROM_DOMAIN_USAGE = "Deletes an existing policy group from a domain." +
    		"\r\n  groupName domainName " +
            "\r\n\t groupName: Name of the policy group to delete from the domain.  Place the policy group name in quotes (\"\") if there are spaces in the name." +
    		"\r\n\t domainName: Name of the domain to delete the policy group from.";
    
	protected ConfigurationServiceProxy proxy;
    
	protected final PolicyPrinter policyPrinter;
	protected final PolicyGroupPrinter groupPrinter;
	protected final PolicyUsagePrinter policyUsagePrinter;
	
	public PolicyCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
		
		policyPrinter = new PolicyPrinter();
		groupPrinter = new PolicyGroupPrinter();
		policyUsagePrinter = new PolicyUsagePrinter();
	}    
	
	@Command(name = "ListPolicies", usage = LIST_POLICIES_USAGE)
    public void listPolicies(String[] args)
	{
		try
		{
			final org.nhind.config.CertPolicy[] policies = proxy.getPolicies();
			if (policies == null || policies.length == 0)
				System.out.println("No policies found");
			else
			{
				policyPrinter.printRecords(Arrays.asList(policies));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policies: " + e.getMessage());
		}

	}		
	
	@Command(name = "ImportPolicy", usage = IMPORT_POLICY_USAGE)
    public void importPolicy(String[] args)
	{
		final String policyName = StringArrayUtil.getRequiredValue(args, 0);
		final String fileLoc = StringArrayUtil.getRequiredValue(args, 1);
		final String lexicon = StringArrayUtil.getOptionalValue(args, 2, "");
		
		// check if the policy already exists
		try
		{
			org.nhind.config.CertPolicy policy = proxy.getPolicyByName(policyName);
			if (policy != null)
			{
				System.out.println("Policy with name " + policyName + " already exists.");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy: " + e.getMessage());
			return;
		}
		
		PolicyLexicon lex;

		
		if (lexicon.isEmpty())
			lex = PolicyLexicon.SIMPLE_TEXT_V1;
		else
		{
			try
			{
				lex = PolicyLexicon.fromString(lexicon);
			}
			catch (Exception e)
			{
				System.out.println("Invalid lexicon name.");
				return;
			}
		}
		
		// validate the policy syntax
		final org.nhindirect.policy.PolicyLexicon parseLexicon;
		if (lex.equals(org.nhind.config.PolicyLexicon.JAVA_SER))
			parseLexicon = org.nhindirect.policy.PolicyLexicon.JAVA_SER;
		else if (lex.equals(org.nhind.config.PolicyLexicon.SIMPLE_TEXT_V1))
			parseLexicon = org.nhindirect.policy.PolicyLexicon.SIMPLE_TEXT_V1;
		else
			parseLexicon = org.nhindirect.policy.PolicyLexicon.XML;		
		
		byte[] policyBytes;
		InputStream inStr = null;
		try
		{
			policyBytes = FileUtils.readFileToByteArray(new File(fileLoc));
			inStr = new ByteArrayInputStream(policyBytes);
			
			final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(parseLexicon);
			parser.parse(inStr);
		}
		catch (PolicyParseException e)
		{
			System.out.println("Syntax error in policy file " + fileLoc + " : " + e.getMessage());
			return;
		}
		catch (IOException e)
		{
			System.out.println("Error reading file " + fileLoc + " : " + e.getMessage());
			return;
		}
		finally
		{
			IOUtils.closeQuietly(inStr);
		}
		
		
		try
		{			
			org.nhind.config.CertPolicy addPolicy = new org.nhind.config.CertPolicy();
			addPolicy.setPolicyData(policyBytes);
			addPolicy.setPolicyName(policyName);
			addPolicy.setLexicon(lex);

			proxy.addPolicy(addPolicy);
			System.out.println("Successfully imported policy.");
			
		}
		catch (IOException e)
		{
			System.out.println("Error reading file " + fileLoc + " : " + e.getMessage());
			return;
		}
		catch (Exception e)
		{
			System.out.println("Error importing certificate " + fileLoc + " : " + e.getMessage());
		}	
	}		
	
	@Command(name = "DeletePolicy", usage = DELETE_POLICY_USAGE)
    public void deletePolicy(String[] args)
	{
		// make sure the policy exists
		final String policyName = StringArrayUtil.getRequiredValue(args, 0);
		org.nhind.config.CertPolicy policy = null;
		
		try
		{
			policy = proxy.getPolicyByName(policyName);
			if (policy == null)
			{
				System.out.println("No policy with name " + policyName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy: " + e.getMessage());
			return;
		}
		
		// now delete the policy
		try
		{
			proxy.deletePolicies(new Long[] {policy.getId()});
			System.out.println("Policy successfully deleted");
		}
		catch (Exception e)
		{
			System.out.println("Failed to delete policy: " + e.getMessage());
			return;
		}

	}	
	
	@Command(name = "ListPolicyGroups", usage = LIST_POLICY_GROUPS_USAGE)
    public void listPolicyGroups(String[] args)
	{
		try
		{
			org.nhind.config.CertPolicyGroup[] groups = proxy.getPolicyGroups();
			if (groups == null || groups.length == 0)
				System.out.println("No policy groups found");
			else
			{
				groupPrinter.printRecords(Arrays.asList(groups));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policies: " + e.getMessage());
		}
	}
	
	@Command(name = "AddPolicyGroup", usage = ADD_POLICY_GROUP_USAGE)
    public void addPolicyGroup(String[] args)
	{
		final String policyGroupName = StringArrayUtil.getRequiredValue(args, 0);
		
		// check if the group already exists
		try
		{
			org.nhind.config.CertPolicyGroup policyGroup = proxy.getPolicyGroupByName(policyGroupName);
			if (policyGroup != null)
			{
				System.out.println("Policy group with name " + policyGroupName + " already exists.");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy: " + e.getMessage());
			return;
		}
		
		// now add the group
		try
		{
			org.nhind.config.CertPolicyGroup policyGroup = new org.nhind.config.CertPolicyGroup();
			policyGroup.setPolicyGroupName(policyGroupName);
			
			proxy.addPolicyGroup(policyGroup);
			
			System.out.println("Successfully added policy group.");
		}
		catch (Exception e)
		{
			System.out.println("Failed to add policy group: " + e.getMessage());
			return;
		}
	}
	
	@Command(name = "DeletePolicyGroup", usage = DELETE_POLICY_GROUP_USAGE)
    public void deletePolicyGroup(String[] args)
	{
		// make sure the group exists
		final String policyGroupName = StringArrayUtil.getRequiredValue(args, 0);
		org.nhind.config.CertPolicyGroup group = null;
		
		try
		{
			group = proxy.getPolicyGroupByName(policyGroupName);
			if (group == null)
			{
				System.out.println("No policy group with name " + policyGroupName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy group: " + e.getMessage());
			return;
		}
		
		// now delete the policy group
		try
		{
			proxy.deletePolicyGroups(new Long[] {group.getId()});
			System.out.println("Policy groups successfully deleted");
		}
		catch (Exception e)
		{
			System.out.println("Failed to delete policy group: " + e.getMessage());
			return;
		}

	}
	
	@Command(name = "ListGroupPolicies", usage = LIST_GROUP_POLICIES_USAGE)
    public void listGroupPolicies(String[] args)
	{
		// make sure the group exists
		final String policyGroupName = StringArrayUtil.getRequiredValue(args, 0);
		org.nhind.config.CertPolicyGroup group = null;
		
		try
		{
			group = proxy.getPolicyGroupByName(policyGroupName);
			if (group == null)
			{
				System.out.println("No policy group with name " + policyGroupName + " found");
				return;
			}
			else if (group.getCertPolicyGroupReltn() == null || group.getCertPolicyGroupReltn().length == 0)
			{
				System.out.println("Group has no policies associated with it.");
				return;
			}
			
			policyUsagePrinter.printRecords(Arrays.asList(group.getCertPolicyGroupReltn()));
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy group: " + e.getMessage());
			return;
		}
	}	
	
	@Command(name = "AddPolicyToGroup", usage = ADD_POLICY_TO_GROUP_USAGE)
    public void addPolicyToGroup(String[] args)
	{
		// make sure the group exists
		final String policyName = StringArrayUtil.getRequiredValue(args, 0);
		final String groupName = StringArrayUtil.getRequiredValue(args, 1);
		final String policyUse = StringArrayUtil.getRequiredValue(args, 2);
		final boolean incoming = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 3));
		final boolean outgoing = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 3));
		
		// make sure the policy exists
		org.nhind.config.CertPolicy policy = null;
		try
		{
			policy = proxy.getPolicyByName(policyName);
			if (policy == null)
			{
				System.out.println("No policy with name " + policyName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy: " + e.getMessage());
			return;
		}
		
		// make sure the group exists
		org.nhind.config.CertPolicyGroup group = null;
		try
		{
			group = proxy.getPolicyGroupByName(groupName);
			if (group == null)
			{
				System.out.println("No policy group with name " + groupName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy group: " + e.getMessage());
			return;
		}
		
		final org.nhind.config.CertPolicyUse use = org.nhind.config.CertPolicyUse.fromString(policyUse);
		if (use == null)
		{
			System.out.println("Unknow usage type");
			return;
		}
			
		try
		{
			proxy.addPolicyUseToGroup(group.getId(), policy.getId(), use, incoming, outgoing);
			System.out.println("Successfully added policy to group.");
		}
		catch (Exception e)
		{
			System.out.println("Failed to add policy to group: " + e.getMessage());
			return;
		}
	}
	
	@Command(name = "DeletePolicyFromGroup", usage = DELETE_POLICY_FROM_GROUP_USAGE)
    public void deletePolicyFromGroup(String[] args)
	{
		// make sure the group exists
		final String policyName = StringArrayUtil.getRequiredValue(args, 0);
		final String groupName = StringArrayUtil.getRequiredValue(args, 1);
		long policyReltnId = -1;
		
		// make sure the group exists
		org.nhind.config.CertPolicyGroup group = null;
		try
		{
			group = proxy.getPolicyGroupByName(groupName);
			if (group == null)
			{
				System.out.println("No policy group with name " + groupName + " found");
				return;
			}
			else
			{
				if (group.getCertPolicyGroupReltn() == null || group.getCertPolicyGroupReltn().length == 0)
				{
					System.out.println("Policy is not associated with group.");
					return;
				}
				else
				{
					for (org.nhind.config.CertPolicyGroupReltn reltn : group.getCertPolicyGroupReltn())
					{
						if (reltn.getCertPolicy().getPolicyName().compareToIgnoreCase(policyName) == 0)
						{
							policyReltnId = reltn.getId();
							break;
						}
							
					}
					if (policyReltnId == -1)
					{
						System.out.println("Policy is not associated with group.");
						return;
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy group: " + e.getMessage());
			return;
		}
			
		try
		{
			proxy.removePolicyUseFromGroup(policyReltnId);
			System.out.println("Successfully delete policy from group.");
		}
		catch (Exception e)
		{
			System.out.println("Failed to delete policy from group: " + e.getMessage());
			return;
		}
	}
	
	@Command(name = "ListDomainPolicyGroups", usage = LIST_DOMAIN_POLICY_GROUPS)
    public void listDomainPolicyGroups(String[] args)
	{
		final String domainName = StringArrayUtil.getRequiredValue(args, 0);
		
		// make sure the domain exists
		Domain[] domains;
		try
		{
			domains = proxy.getDomains(new String[]{domainName}, null);
			if (domains == null || domains.length == 0)
			{
				System.out.println("No domain with name " + domainName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup domain: " + e.getMessage());
			return;
		}
		
		try
		{
			final CertPolicyGroupDomainReltn[] reltns = proxy.getPolicyGroupsByDomain(domains[0].getId());
			if (reltns == null || reltns.length == 0)
			{
				System.out.println("Domain does not have any policy groups associated with it.");
				return;
			}
			
			List<org.nhind.config.CertPolicyGroup> groups = new ArrayList<org.nhind.config.CertPolicyGroup>();
			for (CertPolicyGroupDomainReltn reltn : reltns)
				groups.add(reltn.getCertPolicyGroup());
			
			groupPrinter.printRecords(groups);
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup domain policy groups: " + e.getMessage());
			return;
		}
	}	
	
	@Command(name = "AddPolicyGroupToDomain", usage = ADD_GROUP_TO_DOMAIN_USAGE)
    public void addGroupToDomain(String[] args)
	{
		// make sure the group exists
		final String groupName = StringArrayUtil.getRequiredValue(args, 0);
		final String domainName = StringArrayUtil.getRequiredValue(args, 1);
		
		
		// make sure the group exists
		org.nhind.config.CertPolicyGroup group = null;
		try
		{
			group = proxy.getPolicyGroupByName(groupName);
			if (group == null)
			{
				System.out.println("No policy group with name " + groupName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup policy group: " + e.getMessage());
			return;
		}
		
		// make sure the domain exists
		Domain[] domains;
		try
		{
			domains = proxy.getDomains(new String[]{domainName}, null);
			if (domains == null || domains.length == 0)
			{
				System.out.println("No domain with name " + domainName + " found");
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup domain: " + e.getMessage());
			return;
		}
		
		// make sure it's not already associated
		try
		{
			final CertPolicyGroupDomainReltn[] reltns = proxy.getPolicyGroupsByDomain(domains[0].getId());
			if (reltns != null && reltns.length > 0)
			{
				boolean reltnExists = false;
				for (CertPolicyGroupDomainReltn reltn : reltns)
				{
					if (reltn.getCertPolicyGroup().getPolicyGroupName().compareToIgnoreCase(groupName) == 0)
					{
						reltnExists = true;
						break;
					}
				}
				if (reltnExists)
				{
					System.out.println("Group " + groupName + " already associated with domain " + domainName);
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup existing group to domain associations: " + e.getMessage());
			return;
		}
		
		// now make the association
		try
		{
			proxy.associatePolicyGroupToDomain(domains[0].getId(), group.getId());
			System.out.println("Successfully added policy to group.");
		}
		catch (Exception e)
		{
			System.out.println("Failed to add group to domain: " + e.getMessage());
			return;
		}
	}
	
	@Command(name = "DeletePolicyGroupFromDomain", usage = DELETE_GROUP_FROM_DOMAIN_USAGE)
    public void deletePolicyGroupFromDomain(String[] args)
	{
		// make sure the group exists
		final String groupName = StringArrayUtil.getRequiredValue(args, 0);
		final String domainName = StringArrayUtil.getRequiredValue(args, 1);
		long policyGroupId = -1;
		
		// make sure the domain exists
		Domain[] domains;
		try
		{
			domains = proxy.getDomains(new String[]{domainName}, null);
			if (domains == null || domains.length == 0)
			{
				System.out.println("No domain with name " + domainName + " found");
				return;
			}
			
			// make sure it's really associated
			final CertPolicyGroupDomainReltn[] reltns = proxy.getPolicyGroupsByDomain(domains[0].getId());
			if (reltns == null || reltns.length == 0)
			{
				System.out.println("Policy group is not associated with domain.");
				return;
			}
			else
			{
				for (org.nhind.config.CertPolicyGroupDomainReltn reltn : reltns)
				{
					if (reltn.getCertPolicyGroup().getPolicyGroupName().compareToIgnoreCase(groupName) == 0)
					{
						policyGroupId = reltn.getCertPolicyGroup().getId();
						break;
					}
						
				}
				if (policyGroupId == -1)
				{
					System.out.println("Policy group is not associated with domain.");
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup domain: " + e.getMessage());
			return;
		}
			
		try
		{
			proxy.disassociatePolicyGroupFromDomain(domains[0].getId(), policyGroupId);
			System.out.println("Successfully delete policy group from domain.");
		}
		catch (Exception e)
		{
			System.out.println("Failed to delete policy group from domain: " + e.getMessage());
			return;
		}
	}
}
