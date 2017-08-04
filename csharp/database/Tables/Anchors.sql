CREATE TABLE [dbo].[Anchors]
(
    [Owner] [VARCHAR](400) NOT NULL,

    [Thumbprint] [NVARCHAR](64) NOT NULL,

    [CertificateID] [BIGINT] IDENTITY(1,1) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Anchors_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_Anchors_UpdateDate] DEFAULT GETDATE(),

    [CertificateData] [VARBINARY](MAX) NOT NULL,

    [ValidStartDate] [DATETIME] NOT NULL,

    [ValidEndDate] [DATETIME] NOT NULL,

    [ForIncoming] [BIT] NOT NULL
        CONSTRAINT [DF_Anchors_ForIncoming] DEFAULT 1,

    [ForOutgoing] [BIT] NOT NULL
        CONSTRAINT [DF_Anchors_ForOutgoing] DEFAULT 1,

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Anchors_Status] DEFAULT 0,

    CONSTRAINT [PK_Anchors] PRIMARY KEY CLUSTERED
    (
        [Owner] ASC,
        [Thumbprint] ASC
    )
)
