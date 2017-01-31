CREATE TABLE [dbo].[Addresses]
(
    [EmailAddress] [VARCHAR](400) NOT NULL
        CONSTRAINT [PK_Addresses] PRIMARY KEY CLUSTERED,

    [AddressID] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [IX_Addresses_AddressID] UNIQUE NONCLUSTERED,

    [DomainID] [BIGINT] NOT NULL
        CONSTRAINT [FK_Addresses_DomainID] FOREIGN KEY
        REFERENCES [dbo].[Domains]
        (
            [DomainID]
        ),

    [DisplayName] [VARCHAR](64) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Addresses_CreateDate] DEFAULT GETDATE(),

    [UpdateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Addresses_UpdateDate] DEFAULT GETDATE(),

    [Type] [NVARCHAR](64) NULL,

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Addresses_Status] DEFAULT 0
)
