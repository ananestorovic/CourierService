
USE courier_service;
GO


DROP FUNCTION IF EXISTS  [dbo].getBasePrice
GO

CREATE FUNCTION [dbo].getBasePrice(
@type int
)
RETURNS int
AS 
BEGIN
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

GO


DROP FUNCTION IF EXISTS  [dbo].getPriceByKilo
GO

CREATE FUNCTION [dbo].getPriceByKilo(
@type int
)
RETURNS int
AS 
BEGIN
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
GO

DROP FUNCTION IF EXISTS  [dbo].calculateEuclideDistance
GO

CREATE FUNCTION [dbo].calculateEuclideDistance (
@startX int,
@startY int, 
@endX int,
@endY int
)
RETURNS DECIMAL (10, 3)
AS 
BEGIN
	RETURN SQRT(SQUARE(@startX - @endX) + SQUARE(@startY - @endY))
END
GO

DROP FUNCTION IF EXISTS  [dbo].calculatePackagePrice
GO

CREATE FUNCTION [dbo].calculatePackagePrice(
		@type int,
        @pickUpAddress int,
        @deliveryAddress int,
        @weight decimal(10, 3)
    )
    RETURNS DECIMAL(10, 3)
    AS
    BEGIN
  
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
GO


DROP PROCEDURE IF EXISTS dbo.makeNewPackage
GO

CREATE PROCEDURE dbo.makeNewPackage (
	@idRequest int, 
	@price decimal(10, 3),
	@location int
)
AS 
BEGIN
  INSERT INTO [dbo].Package(idRequest, deliveryStatus, price, acceptanceTime, creationTime, idAddress)
  VALUES(@idRequest, 0, @price, NULL, CURRENT_TIMESTAMP, @location);
END
GO

DROP TRIGGER IF EXISTS dbo.createPackageOffer
GO

CREATE TRIGGER [dbo].createPackageOffer 
ON [dbo].RequestToDelivery
AFTER INSERT
AS 
    BEGIN
        SET NOCOUNT ON;
		DECLARE @insertedRows CURSOR;
        DECLARE @idRequest int
		DECLARE @packageType int
		DECLARE @pickUpAddress int
        DECLARE @deliveryAddress int
        DECLARE @weight decimal(10, 3)


        SET @insertedRows = CURSOR FOR
        SELECT idRequest, type, pickUpAdress, deliveryAdress, weight
        FROM INSERTED

        OPEN @insertedRows

        FETCH NEXT
        FROM @insertedRows
        INTO @idRequest, @packageType, @pickUpAddress, @deliveryAddress, @weight
        
        WHILE @@FETCH_STATUS = 0
            BEGIN
                DECLARE @price int;
                SET @price = [dbo].calculatePackagePrice(@packageType, @pickUpAddress, @deliveryAddress, @weight)

				EXEC dbo.makeNewPackage @idRequest, @price, @pickUpAddress

				FETCH NEXT
				FROM @insertedRows
				INTO @idRequest, @packageType, @pickUpAddress, @deliveryAddress, @weight
            END;
    END
 GO