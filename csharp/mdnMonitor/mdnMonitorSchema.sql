USE [$(DBName)]


	SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
	
if not exists (select * from dbo.sysobjects where id = object_id(N'MDNs') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
Begin

	CREATE TABLE [Mdns](
		[MdnIdentifier] [char](32) NOT NULL,
		[MdnId] [bigint] IDENTITY(1,1) NOT NULL,
		[MessageId] [nvarchar](255) NOT NULL,
		[RecipientAddress] [nvarchar](400) NOT NULL,
		[SenderAddress] [nvarchar](400) NOT NULL,
		[Subject] [nvarchar] (998) NULL,
		[Status] [nvarchar](15) NULL,     /* Longest known disposition-type is 10 characters, 15 may provide for unknown */
		[Timedout] [bit] NOT NULL,
		[NotifyDispatched] [bit] NOT NULL,
		[MdnProcessedDate] [datetime] NULL,
		[CreateDate] [datetime] NOT NULL,
		[UpdateDate] [datetime] NOT NULL,
	    PRIMARY KEY CLUSTERED ([MdnIdentifier] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
	) 
	/****** Object:  Index [CreateDate]    Script Date: 09/09/2012 15:03:07 ******/
	CREATE NONCLUSTERED INDEX [CreateDate] ON [dbo].[Mdns] 
	(
		[CreateDate] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	

	/****** Object:  Index [MdnProcessedDate]    Script Date: 09/09/2012 22:32:42 ******/
	CREATE NONCLUSTERED INDEX [MdnProcessedDate] ON [dbo].[Mdns] 
	(
		[MdnProcessedDate] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	
End

SET ANSI_PADDING OFF
GO