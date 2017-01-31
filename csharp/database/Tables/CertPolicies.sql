CREATE TABLE [dbo].[CertPolicies]
(
    [CertPolicyId] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [PK_CertPolicies] PRIMARY KEY CLUSTERED,

    [Name] [VARCHAR](255) NOT NULL
        CONSTRAINT [IX_CertPolicies] UNIQUE NONCLUSTERED,

    [Description] [VARCHAR](255) NOT NULL,

    [Lexicon] [VARCHAR](255) NOT NULL,

    [Data] [VARBINARY](MAX) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_CertPolicies_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_CertPolicies_UpdateDate] DEFAULT GETDATE(),
)
