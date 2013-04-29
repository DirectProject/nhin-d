package org.nhindirect.policy;

public class PolicyLexiconParserFactory 
{
	public static PolicyLexiconParser getInstance(PolicyLexicon lexicon) throws PolicyParseException
	{
		try
		{
			final PolicyLexiconParser parser = lexicon.getParserClass().newInstance();
		
			return parser;
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new PolicyParseException("Failed to create policy parser.", e);
		}
		///CLOVER:ON
	}
}
