CREATE TABLE [dbo].[Properties]
(
    [PropertyID] [BIGINT] IDENTITY(1,1) NOT NULL,

    [Name] [NVARCHAR](255) NOT NULL
        CONSTRAINT [PK_Properties] PRIMARY KEY CLUSTERED,

    [Value] [NVARCHAR](512) NOT NULL
)

GO

CREATE NONCLUSTERED INDEX [IX_Properties_ID] ON [dbo].[Properties]
(
    [PropertyID] ASC
)
