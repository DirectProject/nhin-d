USE [NHINDConfig]
GO
/****** Object:  Table [dbo].[Accounts]    Script Date: 08/09/2010 18:54:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Accounts](
	[AccountID] [bigint] IDENTITY(1,1) NOT NULL,
	[AccountName] [nvarchar](255) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
 CONSTRAINT [PK_Accounts] PRIMARY KEY CLUSTERED 
(
	[AccountID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Domains]    Script Date: 08/09/2010 18:54:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Domains](
	[DomainID] [bigint] IDENTITY(1,1) NOT NULL,
	[DomainName] [varchar](255) NOT NULL,
	[AccountID] [bigint] NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Domains] PRIMARY KEY CLUSTERED 
(
	[DomainID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE UNIQUE NONCLUSTERED INDEX [IX_Domains_Name] ON [dbo].[Domains] 
(
	[DomainName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Certificates]    Script Date: 08/09/2010 18:54:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Certificates](
	[Owner] [varchar](400) NOT NULL,
	[Thumbprint] [nvarchar](64) NOT NULL,
	[CertificateID] [bigint] IDENTITY(1,1) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[CertificateData] [varbinary](max) NOT NULL,
	[ValidStartDate] [datetime] NOT NULL,
	[ValidEndDate] [datetime] NOT NULL,
 CONSTRAINT [PK_Certificates] PRIMARY KEY CLUSTERED 
(
	[Owner] ASC,
	[Thumbprint] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_Certificates_Thumbprint] ON [dbo].[Certificates] 
(
	[Thumbprint] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Anchors]    Script Date: 08/09/2010 18:54:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Anchors](
	[Owner] [varchar](400) NOT NULL,
	[Thumbprint] [nvarchar](64) NOT NULL,
	[CertificateID] [bigint] IDENTITY(1,1) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[CertificateData] [varbinary](max) NOT NULL,
	[ValidStartDate] [datetime] NOT NULL,
	[ValidEndDate] [datetime] NOT NULL,
	[ForIncoming] [bit] NOT NULL,
	[ForOutgoing] [bit] NOT NULL,
 CONSTRAINT [PK_Anchors_1] PRIMARY KEY CLUSTERED 
(
	[Owner] ASC,
	[Thumbprint] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Addresses]    Script Date: 08/09/2010 18:54:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Addresses](
	[AddressID] [bigint] IDENTITY(1,1) NOT NULL,
	[DomainID] [bigint] NOT NULL,
	[EndpointName] [varchar](64) NOT NULL,
	[DisplayName] [varchar](64) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Addresses] PRIMARY KEY CLUSTERED 
(
	[AddressID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE UNIQUE NONCLUSTERED INDEX [IX_Addresses_DomainEndpoint] ON [dbo].[Addresses] 
(
	[DomainID] ASC,
	[EndpointName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Default [DF_Accounts_CreateDate]    Script Date: 08/09/2010 18:54:48 ******/
ALTER TABLE [dbo].[Accounts] ADD  CONSTRAINT [DF_Accounts_CreateDate]  DEFAULT (getdate()) FOR [CreateDate]
GO
/****** Object:  Default [DF_Anchors_ForIncoming]    Script Date: 08/09/2010 18:54:48 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForIncoming]  DEFAULT ((1)) FOR [ForIncoming]
GO
/****** Object:  Default [DF_Anchors_ForOutgoing]    Script Date: 08/09/2010 18:54:48 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForOutgoing]  DEFAULT ((1)) FOR [ForOutgoing]
GO
/****** Object:  Default [DF_Certificates_CreateDate]    Script Date: 08/09/2010 18:54:48 ******/
ALTER TABLE [dbo].[Certificates] ADD  CONSTRAINT [DF_Certificates_CreateDate]  DEFAULT (getdate()) FOR [CreateDate]
GO
/****** Object:  ForeignKey [FK_Addresses_DomainID]    Script Date: 08/09/2010 18:54:48 ******/
ALTER TABLE [dbo].[Addresses]  WITH CHECK ADD  CONSTRAINT [FK_Addresses_DomainID] FOREIGN KEY([DomainID])
REFERENCES [dbo].[Domains] ([DomainID])
GO
ALTER TABLE [dbo].[Addresses] CHECK CONSTRAINT [FK_Addresses_DomainID]
GO
