/*
 * MSS301 Practical Exam sample database
 * Target: Microsoft SQL Server 2016+
 *
 * Hibernate uses ddl-auto=none, so this script must be executed before the
 * UserService and PostService are started.
 */

IF DB_ID(N'MSS301_2026_PE_MEDIA') IS NULL
BEGIN
    CREATE DATABASE [MSS301_2026_PE_MEDIA];
END
GO

USE [MSS301_2026_PE_MEDIA];
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[users] (
        [user_id]         INT IDENTITY(1,1) NOT NULL,
        [username]        NVARCHAR(30) NOT NULL,
        [email]           NVARCHAR(100) NOT NULL,
        [role]            NVARCHAR(15) NOT NULL,
        [rate_per_post]   DECIMAL(18,2) NOT NULL,
        [max_daily_posts] INT NOT NULL,
        [level]           INT NOT NULL,
        [status]          NVARCHAR(15) NOT NULL,

        CONSTRAINT [PK_Users] PRIMARY KEY CLUSTERED ([user_id] ASC),
        CONSTRAINT [UQ_Users_Username] UNIQUE NONCLUSTERED ([username] ASC),
        CONSTRAINT [CK_Users_Role]
            CHECK ([role] IN ('USER', 'CREATOR', 'ADMIN')),
        CONSTRAINT [CK_Users_Rate]
            CHECK ([rate_per_post] > 0),
        CONSTRAINT [CK_Users_MaxDaily]
            CHECK ([max_daily_posts] BETWEEN 1 AND 10),
        CONSTRAINT [CK_Users_Level]
            CHECK ([level] >= 1),
        CONSTRAINT [CK_Users_Status]
            CHECK ([status] IN ('ACTIVE', 'SUSPENDED', 'DEACTIVATED'))
    );
END
GO

IF OBJECT_ID(N'dbo.posts', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[posts] (
        [post_id]          INT IDENTITY(1,1) NOT NULL,
        [advertiser_name]  NVARCHAR(100) NOT NULL,
        [advertiser_email] NVARCHAR(100) NOT NULL,
        [advertiser_phone] NVARCHAR(20) NOT NULL,
        [start_date]       DATE NOT NULL,
        [end_date]         DATE NOT NULL,
        [number_of_posts]  INT NOT NULL,
        [total_cost]       DECIMAL(18,2) NOT NULL,
        [status]           NVARCHAR(15) NOT NULL,
        [user_id]          INT NOT NULL,

        CONSTRAINT [PK_Posts] PRIMARY KEY CLUSTERED ([post_id] ASC),
        CONSTRAINT [CK_Posts_Dates]
            CHECK ([end_date] > [start_date]),
        CONSTRAINT [CK_Posts_Number]
            CHECK ([number_of_posts] >= 1),
        CONSTRAINT [CK_Posts_Cost]
            CHECK ([total_cost] >= 0),
        CONSTRAINT [CK_Posts_Status]
            CHECK ([status] IN ('DRAFT', 'SCHEDULED', 'PUBLISHED', 'CANCELLED')),
        CONSTRAINT [FK_Posts_User]
            FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([user_id])
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[users])
BEGIN
    INSERT INTO [dbo].[users]
        ([username], [email], [role], [rate_per_post],
         [max_daily_posts], [level], [status])
    VALUES
        (N'creator_a', N'a@example.com', N'CREATOR', 500000, 3, 1, N'ACTIVE'),
        (N'creator_b', N'b@example.com', N'CREATOR', 300000, 1, 1, N'ACTIVE'),
        (N'creator_c', N'c@example.com', N'CREATOR', 800000, 4, 2, N'SUSPENDED');
END
GO
