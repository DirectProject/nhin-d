CREATE TABLE [dbo].[CertPolicyGroupDomainMap]
(
    [MapId] [BIGINT] IDENTITY(1,1) NOT NULL,

    [Owner] [VARCHAR](400) NOT NULL,

    [CertPolicyGroupId] [BIGINT] NOT NULL
        CONSTRAINT [FK_CertPolicyGroupDomainMap_CertPolicyGroups] FOREIGN KEY
        REFERENCES [dbo].[CertPolicyGroups]
        (
            [CertPolicyGroupId]
        ),

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_CertPolicyGroupDomainMap_CreateDate] DEFAULT GETDATE(),

    -- [UpdateDate] [DATETIME] NOT NULL
    --     CONSTRAINT [DF_CertPolicyGroupDomainMap_UpdateDate] DEFAULT GETDATE(),
 
    CONSTRAINT [PK_CertPolicyGroupDomainMap] PRIMARY KEY CLUSTERED
    (
        [Owner] ASC,
        [CertPolicyGroupId] ASC
    )
)
