USE [NHINDConfig]
GO
/****** Object:  Table [dbo].[Domains]    Script Date: 11/03/2010 09:41:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Domains](
	[DomainName] [varchar](255) NOT NULL,
	[DomainID] [bigint] IDENTITY(1,1) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
	[PostmasterAddressID] [bigint] NULL,
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Domains] PRIMARY KEY CLUSTERED 
(
	[DomainName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [DomainID] UNIQUE NONCLUSTERED 
(
	[DomainID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DnsRecords]    Script Date: 11/03/2010 09:41:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[DnsRecords](
	[RecordID] [bigint] IDENTITY(1,1) NOT NULL,
	[DomainName] [varchar](255) NOT NULL,
	[TypeID] [int] NOT NULL,
	[RecordData] [varbinary](max) NULL,
	[ValidStartDate] [datetime] NOT NULL,
	[ValidEndDate] [datetime] NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
	[Notes] [varchar](500) NOT NULL,
 CONSTRAINT [PK_DnsRecords] PRIMARY KEY CLUSTERED 
(
	[RecordID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Certificates]    Script Date: 11/03/2010 09:41:47 ******/
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
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Certificates] PRIMARY KEY CLUSTERED 
(
	[Owner] ASC,
	[Thumbprint] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Anchors]    Script Date: 11/03/2010 09:41:47 ******/
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
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Anchors_1] PRIMARY KEY CLUSTERED 
(
	[Owner] ASC,
	[Thumbprint] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[MXs]    Script Date: 11/03/2010 09:41:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MXs](
	[SMTPDomainName] [varchar](255) NOT NULL,
	[MXID] [bigint] IDENTITY(1,1) NOT NULL,
	[DomainID] [bigint] NOT NULL,
	[Preference] [int] NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
 CONSTRAINT [PK_MXs] PRIMARY KEY CLUSTERED 
(
	[SMTPDomainName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Addresses]    Script Date: 11/03/2010 09:41:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Addresses](
	[EmailAddress] [varchar](400) NOT NULL,
	[AddressID] [bigint] IDENTITY(1,1) NOT NULL,
	[DomainID] [bigint] NOT NULL,
	[DisplayName] [varchar](64) NOT NULL,
	[CreateDate] [datetime] NOT NULL,
	[UpdateDate] [datetime] NOT NULL,
	[Type] [nvarchar](64) NULL,
	[Status] [tinyint] NOT NULL,
 CONSTRAINT [PK_Addresses] PRIMARY KEY CLUSTERED 
(
	[EmailAddress] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Default [DF_Addresses_Status]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Addresses] ADD  CONSTRAINT [DF_Addresses_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_Anchors_ForIncoming]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForIncoming]  DEFAULT ((1)) FOR [ForIncoming]
GO
/****** Object:  Default [DF_Anchors_ForOutgoing]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForOutgoing]  DEFAULT ((1)) FOR [ForOutgoing]
GO
/****** Object:  Default [DF_Anchors_Status]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_Certificates_CreateDate]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Certificates] ADD  CONSTRAINT [DF_Certificates_CreateDate]  DEFAULT (getdate()) FOR [CreateDate]
GO
/****** Object:  Default [DF_Certificates_Status]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Certificates] ADD  CONSTRAINT [DF_Certificates_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_DnsRecords_DomainName]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_DomainName]  DEFAULT ('') FOR [DomainName]
GO
/****** Object:  Default [DF_DnsRecords_TypeID]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_TypeID]  DEFAULT ((0)) FOR [TypeID]
GO
/****** Object:  Default [DF_DnsRecords_ValidStartDate]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_ValidStartDate]  DEFAULT ('1/1/2999') FOR [ValidStartDate]
GO
/****** Object:  Default [DF_DnsRecords_ValidEndDate]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_ValidEndDate]  DEFAULT ('1/1/1900') FOR [ValidEndDate]
GO
/****** Object:  Default [DF_DnsRecords_CreateDate]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_CreateDate]  DEFAULT (getdate()) FOR [CreateDate]
GO
/****** Object:  Default [DF_DnsRecords_UpdateDate]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_UpdateDate]  DEFAULT (getdate()) FOR [UpdateDate]
GO
/****** Object:  Default [DF_DnsRecords_Notes]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[DnsRecords] ADD  CONSTRAINT [DF_DnsRecords_Notes]  DEFAULT ('') FOR [Notes]
GO
/****** Object:  Default [DF_Domains_Status]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Domains] ADD  CONSTRAINT [DF_Domains_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_MXs_SMTPDomainName]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[MXs] ADD  CONSTRAINT [DF_MXs_SMTPDomainName]  DEFAULT ('') FOR [SMTPDomainName]
GO
/****** Object:  Default [DF_MXs_Preference]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[MXs] ADD  CONSTRAINT [DF_MXs_Preference]  DEFAULT ((0)) FOR [Preference]
GO
/****** Object:  ForeignKey [FK_Addresses_DomainID]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[Addresses]  WITH CHECK ADD  CONSTRAINT [FK_Addresses_DomainID] FOREIGN KEY([DomainID])
REFERENCES [dbo].[Domains] ([DomainID])
GO
ALTER TABLE [dbo].[Addresses] CHECK CONSTRAINT [FK_Addresses_DomainID]
GO
/****** Object:  ForeignKey [FK_MXs_DomainID]    Script Date: 11/03/2010 09:41:47 ******/
ALTER TABLE [dbo].[MXs]  WITH CHECK ADD  CONSTRAINT [FK_MXs_DomainID] FOREIGN KEY([DomainID])
REFERENCES [dbo].[Domains] ([DomainID])
GO
ALTER TABLE [dbo].[MXs] CHECK CONSTRAINT [FK_MXs_DomainID]
GO
