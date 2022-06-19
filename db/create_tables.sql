
CREATE TABLE [Address]
( 
	[IdAddress]          int  IDENTITY ( 1,1 )  NOT NULL ,
	[streetName]         varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[streetNumber]       int  NOT NULL ,
	[xCoord]             int  NOT NULL ,
	[yCoord]             int  NOT NULL ,
	[idCity]             int  NOT NULL 
)
go

CREATE TABLE [Administrator]
( 
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[idCity]             int  IDENTITY ( 1,1 )  NOT NULL ,
	[name]               varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL ,
	[postalNumber]       int  NULL 
)
go

CREATE TABLE [Courier]
( 
	[numOfdelivery]      int  NULL ,
	[income]             decimal(10,3)  NULL ,
	[status]             int  NULL 
	CONSTRAINT [DF__Courier__status__607251E5]
		 DEFAULT  0,
	[licencePlate]       varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [CourierRequest]
( 
	[licenceNumber]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [CurrentUsedVehicle]
( 
	[IdVehicle]          int  NOT NULL ,
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [Customer]
( 
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [Package]
( 
	[deliveryStatus]     int  NOT NULL 
	CONSTRAINT [DF__Package__deliver__5F7E2DAC]
		 DEFAULT  0,
	[price]              decimal(10,3)  NULL ,
	[acceptanceTime]     datetime  NULL ,
	[creationTime]       datetime  NOT NULL ,
	[idRequest]          int  NOT NULL ,
	[idAddress]          int  NULL ,
	[markForPickUp]      int  NULL 
	CONSTRAINT [DF__Package__markFor__5E8A0973]
		 DEFAULT  0
)
go

CREATE TABLE [PackageInVehicle]
( 
	[idPackage]          int  NOT NULL ,
	[idVehicle]          int  NULL 
)
go

CREATE TABLE [PackageInWareHouse]
( 
	[idPackage]          int  NOT NULL ,
	[idWareHouse]        int  NULL 
)
go

CREATE TABLE [Person]
( 
	[idUser]             int  IDENTITY ( 1,1 )  NOT NULL ,
	[name]               varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[surname]            varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[username]           varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[password]           varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[idAddr]             int  NOT NULL ,
	[idRequest]          int  NULL 
)
go

CREATE TABLE [RequestToDelivery]
( 
	[idRequest]          int  IDENTITY ( 1,1 )  NOT NULL ,
	[type]               int  NOT NULL ,
	[weight]             decimal(10,3)  NOT NULL ,
	[deliveryAdress]     int  NULL ,
	[pickUpAdress]       int  NULL ,
	[idUser]             int  NULL 
)
go

CREATE TABLE [Vehicle]
( 
	[IdVehicle]          int  IDENTITY ( 1,1 )  NOT NULL ,
	[licencePlate]       varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[fuelType]           int  NOT NULL ,
	[fuelConsumption]    decimal(10,3)  NOT NULL ,
	[capacity]           decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [VehicleInDrive]
( 
	[IdVehicle]          int  NOT NULL ,
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [VehicleInWareHouse]
( 
	[IdVehicle]          int  NOT NULL ,
	[idWarehouse]        int  NOT NULL 
)
go

CREATE TABLE [VehicleUseHistory]
( 
	[IdVehicleuserHistory] int  IDENTITY ( 1,1 )  NOT NULL ,
	[IdVehicle]          int  NOT NULL ,
	[idUser]             int  NOT NULL 
)
go

CREATE TABLE [WarehouseLocation]
( 
	[idWarehouse]        int  IDENTITY ( 1,1 )  NOT NULL ,
	[idAddress]          int  NOT NULL 
)
go

ALTER TABLE [Address]
	ADD CONSTRAINT [XPKAdress] PRIMARY KEY  CLUSTERED ([IdAddress] ASC)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([idUser] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([idCity] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([postalNumber]  ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([idUser] ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XAK1Courier] UNIQUE ([licencePlate]  ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XPKCourierRequest] PRIMARY KEY  CLUSTERED ([idUser] ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XAK1CourierRequest] UNIQUE ([licenceNumber]  ASC)
go

ALTER TABLE [CurrentUsedVehicle]
	ADD CONSTRAINT [XPKCurrentUsedVehicle] PRIMARY KEY  CLUSTERED ([IdVehicle] ASC,[idUser] ASC)
go

ALTER TABLE [Customer]
	ADD CONSTRAINT [XPKCustomer] PRIMARY KEY  CLUSTERED ([idUser] ASC)
go

ALTER TABLE [Package]
	ADD CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([idRequest] ASC)
go

ALTER TABLE [PackageInVehicle]
	ADD CONSTRAINT [PackageInVehicle_pk] PRIMARY KEY  CLUSTERED ([idPackage] ASC)
go

ALTER TABLE [PackageInWareHouse]
	ADD CONSTRAINT [PackageInWareHouse_pk] PRIMARY KEY  CLUSTERED ([idPackage] ASC)
go

ALTER TABLE [Person]
	ADD CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([idUser] ASC)
go

ALTER TABLE [Person]
	ADD CONSTRAINT [XAK1User] UNIQUE ([username]  ASC)
go

ALTER TABLE [RequestToDelivery]
	ADD CONSTRAINT [XPKRequestToDelivery] PRIMARY KEY  CLUSTERED ([idRequest] ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([IdVehicle] ASC)
go

CREATE UNIQUE NONCLUSTERED INDEX [LicencePlateUnique] ON [Vehicle]
( 
	[licencePlate]        ASC
)
go

ALTER TABLE [VehicleInDrive]
	ADD CONSTRAINT [VehicleInDrive_pk] PRIMARY KEY  CLUSTERED ([IdVehicle] ASC)
go

ALTER TABLE [VehicleInWareHouse]
	ADD CONSTRAINT [XPKVehicleInWareHouse] PRIMARY KEY  CLUSTERED ([IdVehicle] ASC)
go

ALTER TABLE [VehicleUseHistory]
	ADD CONSTRAINT [XPKVehicleUseHistory] PRIMARY KEY  CLUSTERED ([IdVehicleuserHistory] ASC)
go

ALTER TABLE [WarehouseLocation]
	ADD CONSTRAINT [XPKWarehouseLocation] PRIMARY KEY  CLUSTERED ([idWarehouse] ASC)
go


ALTER TABLE [Address] WITH CHECK 
	ADD CONSTRAINT [R_1] FOREIGN KEY ([idCity]) REFERENCES [City]([idCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Address]
	  WITH CHECK CHECK CONSTRAINT [R_1]
go


ALTER TABLE [Administrator]
	ADD CONSTRAINT [R_30] FOREIGN KEY ([idUser]) REFERENCES [Person]([idUser])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Courier]
	ADD CONSTRAINT [R_29] FOREIGN KEY ([idUser]) REFERENCES [Person]([idUser])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [CourierRequest] WITH CHECK 
	ADD CONSTRAINT [R_18] FOREIGN KEY ([idUser]) REFERENCES [Person]([idUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CourierRequest]
	  WITH CHECK CHECK CONSTRAINT [R_18]
go


ALTER TABLE [CurrentUsedVehicle] WITH CHECK 
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdVehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CurrentUsedVehicle]
	  WITH CHECK CHECK CONSTRAINT [R_8]
go

ALTER TABLE [CurrentUsedVehicle] WITH CHECK 
	ADD CONSTRAINT [R_7] FOREIGN KEY ([idUser]) REFERENCES [Courier]([idUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CurrentUsedVehicle]
	  WITH CHECK CHECK CONSTRAINT [R_7]
go


ALTER TABLE [Customer]
	ADD CONSTRAINT [R_28] FOREIGN KEY ([idUser]) REFERENCES [Person]([idUser])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Package] WITH CHECK 
	ADD CONSTRAINT [R_37] FOREIGN KEY ([idRequest]) REFERENCES [RequestToDelivery]([idRequest])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	  WITH CHECK CHECK CONSTRAINT [R_37]
go

ALTER TABLE [Package] WITH CHECK 
	ADD CONSTRAINT [Package_Address__fk] FOREIGN KEY ([idAddress]) REFERENCES [Address]([IdAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	  WITH CHECK CHECK CONSTRAINT [Package_Address__fk]
go


ALTER TABLE [PackageInVehicle] WITH CHECK 
	ADD CONSTRAINT [PackageInVehicle_Package__fk] FOREIGN KEY ([idPackage]) REFERENCES [Package]([idRequest])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [PackageInVehicle]
	  WITH CHECK CHECK CONSTRAINT [PackageInVehicle_Package__fk]
go

ALTER TABLE [PackageInVehicle] WITH CHECK 
	ADD CONSTRAINT [PackageInVehicle_Vehicle__fk] FOREIGN KEY ([idVehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [PackageInVehicle]
	  WITH CHECK CHECK CONSTRAINT [PackageInVehicle_Vehicle__fk]
go


ALTER TABLE [PackageInWareHouse] WITH CHECK 
	ADD CONSTRAINT [PackageInWareHouse_Package__fk] FOREIGN KEY ([idPackage]) REFERENCES [Package]([idRequest])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [PackageInWareHouse]
	  WITH CHECK CHECK CONSTRAINT [PackageInWareHouse_Package__fk]
go

ALTER TABLE [PackageInWareHouse] WITH CHECK 
	ADD CONSTRAINT [PackageInWareHouse_WarehouseLocation__fk] FOREIGN KEY ([idWareHouse]) REFERENCES [WarehouseLocation]([idWarehouse])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [PackageInWareHouse]
	  WITH CHECK CHECK CONSTRAINT [PackageInWareHouse_WarehouseLocation__fk]
go


ALTER TABLE [Person] WITH CHECK 
	ADD CONSTRAINT [R_3] FOREIGN KEY ([idAddr]) REFERENCES [Address]([IdAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Person]
	  WITH CHECK CHECK CONSTRAINT [R_3]
go

ALTER TABLE [Person] WITH CHECK 
	ADD CONSTRAINT [R_36] FOREIGN KEY ([idRequest]) REFERENCES [RequestToDelivery]([idRequest])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Person]
	  WITH CHECK CHECK CONSTRAINT [R_36]
go


ALTER TABLE [RequestToDelivery] WITH CHECK 
	ADD CONSTRAINT [R_40] FOREIGN KEY ([deliveryAdress]) REFERENCES [Address]([IdAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [RequestToDelivery]
	  WITH CHECK CHECK CONSTRAINT [R_40]
go

ALTER TABLE [RequestToDelivery] WITH CHECK 
	ADD CONSTRAINT [R_41] FOREIGN KEY ([pickUpAdress]) REFERENCES [Address]([IdAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [RequestToDelivery]
	  WITH CHECK CHECK CONSTRAINT [R_41]
go

ALTER TABLE [RequestToDelivery] WITH CHECK 
	ADD CONSTRAINT [R_43] FOREIGN KEY ([idUser]) REFERENCES [Person]([idUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [RequestToDelivery]
	  WITH CHECK CHECK CONSTRAINT [R_43]
go


ALTER TABLE [VehicleInDrive] WITH CHECK 
	ADD CONSTRAINT [VehicleInDrive_Vehicle_IdVehicle_fk] FOREIGN KEY ([IdVehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleInDrive]
	  WITH CHECK CHECK CONSTRAINT [VehicleInDrive_Vehicle_IdVehicle_fk]
go

ALTER TABLE [VehicleInDrive] WITH CHECK 
	ADD CONSTRAINT [VehicleInDrive_Courier_idUser_fk] FOREIGN KEY ([idUser]) REFERENCES [Courier]([idUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleInDrive]
	  WITH CHECK CHECK CONSTRAINT [VehicleInDrive_Courier_idUser_fk]
go


ALTER TABLE [VehicleInWareHouse] WITH CHECK 
	ADD CONSTRAINT [R_22] FOREIGN KEY ([IdVehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleInWareHouse]
	  WITH CHECK CHECK CONSTRAINT [R_22]
go

ALTER TABLE [VehicleInWareHouse] WITH CHECK 
	ADD CONSTRAINT [R_23] FOREIGN KEY ([idWarehouse]) REFERENCES [WarehouseLocation]([idWarehouse])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleInWareHouse]
	  WITH CHECK CHECK CONSTRAINT [R_23]
go


ALTER TABLE [VehicleUseHistory] WITH CHECK 
	ADD CONSTRAINT [R_35] FOREIGN KEY ([IdVehicle]) REFERENCES [Vehicle]([IdVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleUseHistory]
	  WITH CHECK CHECK CONSTRAINT [R_35]
go

ALTER TABLE [VehicleUseHistory] WITH CHECK 
	ADD CONSTRAINT [R_34] FOREIGN KEY ([idUser]) REFERENCES [Courier]([idUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [VehicleUseHistory]
	  WITH CHECK CHECK CONSTRAINT [R_34]
go


ALTER TABLE [WarehouseLocation] WITH CHECK 
	ADD CONSTRAINT [R_2] FOREIGN KEY ([idAddress]) REFERENCES [Address]([IdAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [WarehouseLocation]
	  WITH CHECK CHECK CONSTRAINT [R_2]
go

CREATE FUNCTION [getBasePrice] (@type int )  
  RETURNS int 
  
AS BEGIN
		DECLARE @basePrice int

		SET @basePrice = 0

		IF (@type = 0) 
			BEGIN
				SET @basePrice = 115
			END
		ELSE IF (@type = 1)
			BEGIN
				SET @basePrice = 175
			END
		ELSE IF (@type = 2)
			BEGIN
				SET @basePrice = 250
			END
		ELSE IF (@type = 3) 
			BEGIN
				SET @basePrice = 350
			END

		RETURN @basePrice

END
go

CREATE FUNCTION [getPriceByKilo] (@type int )  
  RETURNS int 
  
AS BEGIN
		DECLARE @priceByKilo int

		SET @priceByKilo = 0

		
		IF (@type = 0) 
            BEGIN
                SET @priceByKilo = 0
            END
        ELSE IF (@type = 1 OR @type = 2)
            BEGIN
                SET @priceByKilo = 100
            END
        ELSE IF (@type = 3) 
            BEGIN
                SET @priceByKilo = 500
            END

		RETURN @priceByKilo
END
go

CREATE FUNCTION [calculateEuclideDistance] (@startX int , @startY int , @endX int , @endY int )  
  RETURNS decimal(10,3) 
  
AS BEGIN
	RETURN SQRT(SQUARE(@startX - @endX) + SQUARE(@startY - @endY))
END
go

CREATE FUNCTION [calculatePackagePrice] (@type int , @pickUpAddress int , @deliveryAddress int , @weight decimal(10,3) )  
  RETURNS decimal(10,3) 
  
AS BEGIN
  
       		DECLARE @priceByKilo int
			DECLARE @basePrice int

			SET @priceByKilo = dbo.getPriceByKilo(@type)
			SET @basePrice = dbo.getBasePrice(@type)

            DECLARE @startX int, @startY int
            DECLARE @endX int , @endY int

            SELECT @startX = xCoord, @startY = yCoord
            FROM Address
            WHERE IdAddress = @pickUpAddress

            SELECT @endX = xCoord, @endY = yCoord
            FROM Address
            WHERE IdAddress = @deliveryAddress

            DECLARE @euclidDistance decimal(10, 3)

            SET @euclidDistance = dbo.calculateEuclideDistance(@startX, @startY, @endX, @endY)

            DECLARE @cenaPaketa decimal(10, 3)

            SET @cenaPaketa = (@basePrice + @weight*@priceByKilo)*@euclidDistance

            RETURN @cenaPaketa
    END
go

CREATE PROCEDURE [makeNewPackage] @idRequest int , @price decimal(10,3) , @location int   
   
 AS BEGIN
  INSERT INTO [dbo].Package(idRequest, deliveryStatus, price, acceptanceTime, creationTime, idAddress)
  VALUES(@idRequest, 0, @price, NULL, CURRENT_TIMESTAMP, @location);
END
go
