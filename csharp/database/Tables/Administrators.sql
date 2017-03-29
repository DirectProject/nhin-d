CREATE TABLE [dbo].[Administrators]
(
    [AdministratorID] [BIGINT] IDENTITY(1,1) NOT NULL,

    [Username] [VARCHAR](50) NOT NULL
        CONSTRAINT [PK_Administrators] PRIMARY KEY CLUSTERED,

    [PasswordHash] [VARCHAR](50) NOT NULL,

    [CreateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Administrators_CreateDate] DEFAULT GETDATE(),

    [UpdateDate] [DATETIME] NOT NULL
        CONSTRAINT [DF_Administrators_UpdateDate] DEFAULT GETDATE(),

    [Status] [TINYINT] NOT NULL
        CONSTRAINT [DF_Administrators_Status] DEFAULT 0
)
