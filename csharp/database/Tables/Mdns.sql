CREATE TABLE [Mdns]
(
    [MdnIdentifier] [char](32) NOT NULL
        CONSTRAINT [IX_Mdns_MdnIdentifier] UNIQUE NONCLUSTERED,

    [MdnId] [bigint] IDENTITY(1,1) NOT NULL
        CONSTRAINT [PK_Mdns] PRIMARY KEY CLUSTERED,

    [MessageId] [nvarchar](255) NOT NULL,

    [RecipientAddress] [nvarchar](400) NOT NULL,

    [SenderAddress] [nvarchar](400) NOT NULL,

    [Subject] [nvarchar] (998) NULL,

    -- Longest known disposition-type is 10 characters, 15 may provide for unknown
    [Status] [nvarchar](15) NOT NULL,

    [NotifyDispatched] [bit] NOT NULL,

    [CreateDate] [datetime] NOT NULL
        CONSTRAINT [DF_Mdns_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_Mdns_UpdateDate] DEFAULT GETDATE(),
)

GO

CREATE NONCLUSTERED INDEX [CreateDate] ON [dbo].[Mdns]
(
    [CreateDate] ASC
)
