CREATE TABLE [dbo].[DnsRecords]
(
    [RecordID] [BIGINT] IDENTITY(1,1) NOT NULL
        CONSTRAINT [PK_DnsRecords] PRIMARY KEY CLUSTERED,

    [DomainName] [VARCHAR](255) NOT NULL,

    [TypeID] [int] NOT NULL
        CONSTRAINT [DF_DnsRecords_TypeID] DEFAULT 0,

    [RecordData] [VARBINARY](MAX) NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_DnsRecords_CreateDate] DEFAULT GETDATE(),

    [UpdateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_DnsRecords_UpdateDate]  DEFAULT GETDATE(),

    [Notes] [VARCHAR](500) NOT NULL
        CONSTRAINT [DF_DnsRecords_Notes] DEFAULT ''
)

GO

CREATE NONCLUSTERED INDEX [IX_DnsRecords_DomainName] ON [dbo].[DnsRecords]
(
    [DomainName] ASC
)
