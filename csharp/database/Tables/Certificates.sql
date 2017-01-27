CREATE TABLE [dbo].[Certificates]
(
    [Owner] [VARCHAR](400) NOT NULL,

    [Thumbprint] [NVARCHAR](64) NOT NULL,

    [CertificateID] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [IX_Certificates_CertificateID] UNIQUE NONCLUSTERED,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Certificates_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_Certificates_UpdateDate] DEFAULT GETDATE(),

    [CertificateData] [VARBINARY](MAX) NOT NULL,

    [ValidStartDate] [DATETIME] NOT NULL,

    [ValidEndDate] [DATETIME] NOT NULL,

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Certificates_Status] DEFAULT 0,

    CONSTRAINT [PK_Certificates] PRIMARY KEY CLUSTERED
    (
        [Owner] ASC,
        [Thumbprint] ASC
    )
)
