USE [master]
GO
/****** Object:  Database [NHINDConfig]    Script Date: 08/26/2010 09:14:10 ******/
CREATE DATABASE [NHINDConfig] ON  PRIMARY 
( NAME = N'NHINDirectConfig', FILENAME = N'c:\Program Files\Microsoft SQL Server\MSSQL10.SQLEXPRESS\MSSQL\DATA\NHINDirectConfig.mdf' , SIZE = 2048KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'NHINDirectConfig_log', FILENAME = N'c:\Program Files\Microsoft SQL Server\MSSQL10.SQLEXPRESS\MSSQL\DATA\NHINDirectConfig_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [NHINDConfig] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [NHINDConfig].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [NHINDConfig] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [NHINDConfig] SET ANSI_NULLS OFF
GO
ALTER DATABASE [NHINDConfig] SET ANSI_PADDING OFF
GO
ALTER DATABASE [NHINDConfig] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [NHINDConfig] SET ARITHABORT OFF
GO
ALTER DATABASE [NHINDConfig] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [NHINDConfig] SET AUTO_CREATE_STATISTICS ON
GO
ALTER DATABASE [NHINDConfig] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [NHINDConfig] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [NHINDConfig] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [NHINDConfig] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [NHINDConfig] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [NHINDConfig] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [NHINDConfig] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [NHINDConfig] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [NHINDConfig] SET  DISABLE_BROKER
GO
ALTER DATABASE [NHINDConfig] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [NHINDConfig] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [NHINDConfig] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [NHINDConfig] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [NHINDConfig] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [NHINDConfig] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [NHINDConfig] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [NHINDConfig] SET  READ_WRITE
GO
ALTER DATABASE [NHINDConfig] SET RECOVERY SIMPLE
GO
ALTER DATABASE [NHINDConfig] SET  MULTI_USER
GO
ALTER DATABASE [NHINDConfig] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [NHINDConfig] SET DB_CHAINING OFF
GO
USE [NHINDConfig]
GO
/****** Object:  Table [dbo].[Domains]    Script Date: 08/26/2010 09:14:11 ******/
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
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE UNIQUE NONCLUSTERED INDEX [IX_Domains_DomainID] ON [dbo].[Domains] 
(
	[DomainID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Certificates]    Script Date: 08/26/2010 09:14:11 ******/
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
CREATE UNIQUE NONCLUSTERED INDEX [IX_Certificates_CertificateID] ON [dbo].[Certificates] 
(
	[CertificateID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Anchors]    Script Date: 08/26/2010 09:14:11 ******/
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
/****** Object:  Table [dbo].[Addresses]    Script Date: 08/26/2010 09:14:11 ******/
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
CREATE UNIQUE NONCLUSTERED INDEX [IX_Addresses_AddressID] ON [dbo].[Addresses] 
(
	[AddressID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Default [DF_Domains_Status]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Domains] ADD  CONSTRAINT [DF_Domains_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_Certificates_CreateDate]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Certificates] ADD  CONSTRAINT [DF_Certificates_CreateDate]  DEFAULT (getdate()) FOR [CreateDate]
GO
/****** Object:  Default [DF_Certificates_Status]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Certificates] ADD  CONSTRAINT [DF_Certificates_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_Anchors_ForIncoming]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForIncoming]  DEFAULT ((1)) FOR [ForIncoming]
GO
/****** Object:  Default [DF_Anchors_ForOutgoing]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_ForOutgoing]  DEFAULT ((1)) FOR [ForOutgoing]
GO
/****** Object:  Default [DF_Anchors_Status]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Anchors] ADD  CONSTRAINT [DF_Anchors_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF_Addresses_Status]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Addresses] ADD  CONSTRAINT [DF_Addresses_Status]  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  ForeignKey [FK_Addresses_DomainID]    Script Date: 08/26/2010 09:14:11 ******/
ALTER TABLE [dbo].[Addresses]  WITH CHECK ADD  CONSTRAINT [FK_Addresses_DomainID] FOREIGN KEY([DomainID])
REFERENCES [dbo].[Domains] ([DomainID])
GO
ALTER TABLE [dbo].[Addresses] CHECK CONSTRAINT [FK_Addresses_DomainID]
GO
