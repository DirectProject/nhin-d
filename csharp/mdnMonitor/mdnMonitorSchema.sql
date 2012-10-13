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
	    PRIMARY KEY CLUSTERED ([MdnIdentifier] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF),
		UNIQUE NONCLUSTERED ([CreateDate] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY],
		UNIQUE NONCLUSTERED ([MdnProcessedDate] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY]

	) 
End

SET ANSI_PADDING OFF
GO