--
-- Reseed in unit tests acts different depending on whether the database is freshly created.
-- This keeps it consistent.
--

INSERT INTO [dbo].[Domains]
           ([DomainName]
           ,[AgentName]
           ,[CreateDate]
           ,[UpdateDate]
           ,[Status])
     VALUES
           ('seedname'
           ,'noagent'
           ,GETDATE()
           ,GETDATE()
           ,0
           )
GO


INSERT INTO [dbo].[CertPolicies]
           ([Name]
           ,[Description]
           ,[Lexicon]
           ,[Data]
           ,[CreateDate])
     VALUES
           ('seedpolicyname'
           ,'seedpolicyname'
           ,'seedlexicon'
           , CONVERT(varbinary(MAX), '')
           ,GETDATE()
           )
GO


INSERT INTO [dbo].[CertPolicyGroups]
           ([Name]
           ,[Description]
           ,[CreateDate])
     VALUES
           ('seedname'
           ,'seedname'
           ,GETDATE()
           )
GO

INSERT [dbo].[Properties] ([Name], [Value]) VALUES (N'TokenSettings', 
  N'<TokenSettings xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <Library>C:\Program Files\SafeNet\LunaClient\cryptoki.dll</Library>
  <TokenSerial>serialnumber</TokenSerial>
  <TokenLabel>partionname</TokenLabel>
  <ApplicationName>DirectProject</ApplicationName>
  <UserPin>pass</UserPin>
</TokenSettings>')

GO
