CREATE TABLE [dbo].[CertPolicyGroupMap]
(    
    [MapId] [BIGINT] IDENTITY(1,1) NOT NULL,

    [CertPolicyGroupId] [BIGINT] NOT NULL
        CONSTRAINT [FK_CertPolicyGroupMap_CertPolicyGroups] FOREIGN KEY
        REFERENCES [dbo].[CertPolicyGroups]
        (
            [CertPolicyGroupId]
        ),

    [CertPolicyId] [BIGINT] NOT NULL
        CONSTRAINT [FK_CertPolicyGroupMap_CertPolicies] FOREIGN KEY
        REFERENCES [dbo].[CertPolicies]
        (
            [CertPolicyId]
        ),

    [PolicyUse] [TINYINT] NOT NULL,

    [ForIncoming] [BIT] NOT NULL
        CONSTRAINT [DF_CertPolicyGroupMap_ForIncoming] DEFAULT 1,

    [ForOutgoing] [BIT] NOT NULL
        CONSTRAINT [DF_CertPolicyGroupMap_ForOutgoing] DEFAULT 1,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_CertPolicyGroupMap_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_CertPolicyGroupMap_UpdateDate] DEFAULT GETDATE(),

    CONSTRAINT [PK_CertPolicyGroupMap] PRIMARY KEY CLUSTERED
    (
        [CertPolicyGroupId] ASC,
        [ForOutgoing] ASC,
        [CertPolicyId] ASC,
        [PolicyUse] ASC,
        [ForIncoming] ASC
    )
)
