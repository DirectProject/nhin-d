CREATE TABLE [dbo].[CertPolicyGroups]
(
    [CertPolicyGroupId] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [PK_CertPolicyGroups] PRIMARY KEY CLUSTERED,

    [Name] [VARCHAR](255) NOT NULL
        CONSTRAINT [IX_CertPolicyGroups_Name] UNIQUE NONCLUSTERED,

    [Description] [VARCHAR](255) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_CertPolicyGroups_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_CertPolicyGroups_UpdateDate] DEFAULT GETDATE(),
)
