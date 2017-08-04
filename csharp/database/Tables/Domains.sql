CREATE TABLE [dbo].[Domains]
(
    [DomainName] [VARCHAR](255) NOT NULL
        CONSTRAINT [PK_Domains] PRIMARY KEY CLUSTERED,

    [AgentName] [VARCHAR](25) NULL,

    [DomainID] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [IX_Domains_DomainID] UNIQUE NONCLUSTERED,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Domains_CreateDate] DEFAULT GETDATE(),

    [UpdateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Domains_UpdateDate] DEFAULT GETDATE(),

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Domains_Status] DEFAULT 0,

    [SecurityStandard] [TINYINT] NOT NULL DEFAULT 0
)
