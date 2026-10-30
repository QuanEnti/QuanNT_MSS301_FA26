USE [master]
GO

IF DB_ID(N'MSS301_2026_PE') IS NULL
BEGIN
    CREATE DATABASE [MSS301_2026_PE];
END
GO

USE [MSS301_2026_PE]
GO

IF OBJECT_ID(N'dbo.rooms', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[rooms] (
        [room_id]         INT IDENTITY(1,1) NOT NULL,
        [room_number]     NVARCHAR(20) NOT NULL,
        [room_type]       NVARCHAR(10) NOT NULL,
        [price_per_night] DECIMAL(18,2) NOT NULL,
        [capacity]        INT NOT NULL,
        [floor]           INT NOT NULL,
        [status]          NVARCHAR(15) NOT NULL,
        CONSTRAINT [PK_Rooms] PRIMARY KEY CLUSTERED ([room_id]),
        CONSTRAINT [UQ_Rooms_Number] UNIQUE ([room_number]),
        CONSTRAINT [CK_Rooms_Type] CHECK ([room_type] IN ('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE')),
        CONSTRAINT [CK_Rooms_Price] CHECK ([price_per_night] > 0),
        CONSTRAINT [CK_Rooms_Capacity] CHECK ([capacity] BETWEEN 1 AND 10),
        CONSTRAINT [CK_Rooms_Floor] CHECK ([floor] >= 1),
        CONSTRAINT [CK_Rooms_Status] CHECK ([status] IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE'))
    );
END
GO

IF OBJECT_ID(N'dbo.reservations', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[reservations] (
        [reservation_id]   INT IDENTITY(1,1) NOT NULL,
        [guest_name]       NVARCHAR(100) NOT NULL,
        [guest_email]      NVARCHAR(100) NOT NULL,
        [guest_phone]      NVARCHAR(20) NOT NULL,
        [check_in_date]    DATE NOT NULL,
        [check_out_date]   DATE NOT NULL,
        [number_of_guests] INT NOT NULL,
        [total_amount]     DECIMAL(18,2) NOT NULL,
        [status]           NVARCHAR(15) NOT NULL,
        [room_id]          INT NOT NULL,
        CONSTRAINT [PK_Reservations] PRIMARY KEY CLUSTERED ([reservation_id]),
        CONSTRAINT [CK_Reservations_Dates] CHECK ([check_out_date] > [check_in_date]),
        CONSTRAINT [CK_Reservations_Guests] CHECK ([number_of_guests] BETWEEN 1 AND 10),
        CONSTRAINT [CK_Reservations_Amount] CHECK ([total_amount] >= 0),
        CONSTRAINT [CK_Reservations_Status]
            CHECK ([status] IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')),
        CONSTRAINT [FK_Reservations_Room]
            FOREIGN KEY ([room_id]) REFERENCES [dbo].[rooms]([room_id])
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[rooms])
BEGIN
    INSERT INTO [dbo].[rooms]
        ([room_number], [room_type], [price_per_night], [capacity], [floor], [status])
    VALUES
        (N'101', N'SINGLE', 500000, 1, 1, N'AVAILABLE'),
        (N'202', N'DOUBLE', 850000, 2, 2, N'AVAILABLE'),
        (N'305', N'SUITE', 1500000, 4, 3, N'AVAILABLE'),
        (N'401', N'DELUXE', 2200000, 6, 4, N'MAINTENANCE');
END
GO
