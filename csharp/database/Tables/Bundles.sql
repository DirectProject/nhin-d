CREATE TABLE [dbo].[Bundles]
(
    [BundleID] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [PK_Bundles] PRIMARY KEY CLUSTERED,

    [Owner] [VARCHAR](400) NOT NULL,

    [Url] [VARCHAR](2048) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Bundles_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_Bundles_UpdateDate] DEFAULT GETDATE(),

    [ForIncoming] [BIT] NOT NULL
        CONSTRAINT [DF_Bundles_ForIncoming] DEFAULT 1,

    [ForOutgoing] [BIT] NOT NULL
        CONSTRAINT [DF_Bundles_ForOutgoing] DEFAULT 1,

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Bundles_Status] DEFAULT 0
)

GO

CREATE NONCLUSTERED INDEX [IX_Bundles_Owner] ON [dbo].[Bundles]
(
    [Owner] ASC
)
