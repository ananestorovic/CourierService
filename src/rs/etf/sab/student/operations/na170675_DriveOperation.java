package rs.etf.sab.student.operations;

import javafx.util.Pair;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.student.enums.CourierStatus;
import rs.etf.sab.student.enums.FuelType;
import rs.etf.sab.student.enums.PackageStatus;
import rs.etf.sab.student.helpers.Util;
import rs.etf.sab.student.sql_helper.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_DriveOperation implements DriveOperation {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_DriveOperation.class.getName());

    private final HashMap<String, DriveInfo> driveMap;
    private String currentUserName;


    public na170675_DriveOperation() {
        driveMap = new HashMap<>();
    }

    @Override
    public boolean planingDrive(String username) {
        boolean retVal = false;
        int userId = Util.getUserId(username);
        if (userId != -1 && !Util.isCourierInDrive(userId)) {
            int cityId = Util.getUserCity(userId);
            int vehicleId = findFreeVehicle(cityId);
            currentUserName = username;
            driveMap.put(username, new DriveInfo());
            driveMap.get(username).idVehicle = vehicleId;
            driveMap.get(username).idUser = userId;
            driveMap.get(username).idCity = cityId;
            driveMap.get(username).wareHousePackages = new HashMap<>();
            driveMap.get(username).currentCapacity = BigDecimal.ZERO;
            driveMap.get(username).idWareHouse = new na170675_StockroomOperations().getWareHouse(cityId);
            getDriveInfo();
            if (vehicleId != -1) {
                findPackagesForDelivery(cityId);
                if (!driveMap.get(username).route.isEmpty()) {
                    changeCourierStatus(userId, CourierStatus.DRIVE);
                    assignVehicleToUser(vehicleId, userId);
                }
            }
        }
        return retVal;
    }

    private void getDriveInfo() {
        getVehicleInfo(driveMap.get(currentUserName).idVehicle);
    }


    Pair<Integer, Integer> getWareHouseCoordinates(int cityId) {
        try (PreparedStatement statement = mConnection.prepareStatement("SELECT  xCoord, yCoord FROM WarehouseLocation INNER JOIN Address A on A.IdAddress = WarehouseLocation.idAddress " +
                " WHERE idCity = ?")) {
            statement.setInt(1, cityId);
            try (ResultSet rs = statement.executeQuery();) {
                if (rs.next()) {
                    return new Pair<>(rs.getInt(1), rs.getInt(2));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return null;
    }

    private void getVehicleInfo(int vehicleId) {
        try (PreparedStatement statement = mConnection.prepareStatement("SELECT  capacity, fuelConsumption, fuelType FROM Vehicle WHERE IdVehicle = ?");) {
            statement.setInt(1, vehicleId);
            try (ResultSet rs = statement.executeQuery();) {
                if (rs.next()) {
                    driveMap.get(currentUserName).capacity = rs.getBigDecimal(1);
                    driveMap.get(currentUserName).fuelConsumption = rs.getBigDecimal(2);
                    driveMap.get(currentUserName).fuelType = FuelType.values()[rs.getInt(3)];

                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void changeCourierStatus(int userId, CourierStatus status) {
        try (PreparedStatement ps = mConnection.prepareStatement("Update Courier SET status = ? where idUser = ?")) {
            ps.setInt(1, status.ordinal());
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void findPackagesForDelivery(int idCity) {


        Pair<List<Package>, List<Package>> packages = pickUpAllPackagesFromCity(idCity);


        List<Package> forDelivery = new LinkedList<>();
        int by = packages.getKey().size();
        if (!packages.getValue().isEmpty()) {
            by -= 1;
        }
        for (int i = 0; i < by; i++) {
            Package packageForDelivery = packages.getKey().get(i).clone();
            packageForDelivery.pathPartType = Package.PATH_PART_TYPE.DELIVER;
            forDelivery.add(packageForDelivery);
        }

        setPathPartType(packages.getKey(), Package.PATH_PART_TYPE.PICK_UP_AND_SET_TO_DELIVER_LATER);

        if (!packages.getValue().isEmpty()) {
            packages.getValue().forEach(onePackage -> {
                Package packageForDelivery = onePackage.clone();
                packageForDelivery.pathPartType = Package.PATH_PART_TYPE.DELIVER;
                forDelivery.add(packageForDelivery);
            });
            driveMap.get(currentUserName).wareHousePackages.put(idCity, packages.getValue());
        }

        setPackagesForPickUp(forDelivery);

        driveMap.get(currentUserName).packagesForDelivery = forDelivery;
        driveMap.get(currentUserName).packagesForPickUp = packages.getKey();

        makeRouteForPickUpAndDeliver();
    }

    private void makeRouteForPickUpAndDeliver() {
        List<Package> forPickupStartCityPath = driveMap.get(currentUserName).packagesForPickUp;

        // Making path for delivery

        List<Package> packagesForDelivery = driveMap.get(currentUserName).packagesForDelivery;
        List<Package> sortedPackageForDelivery = new LinkedList<>();
        Package startPackage = makeStartPackage();

        Package currentPackage = startPackage;
        while (!packagesForDelivery.isEmpty()) {

            Package closestOne = findClosestOne(packagesForDelivery, currentPackage);
            packagesForDelivery.remove(closestOne);
            sortedPackageForDelivery.add(closestOne);
            currentPackage = closestOne;
        }


        List<Package> routeAfterPickUpFromFirstCity = new LinkedList<>();
        int currentCity = sortedPackageForDelivery.get(0).idCityDelivery;
        for (Package onePackage : sortedPackageForDelivery) {
            onePackage.pathPartType = Package.PATH_PART_TYPE.DELIVER;
            driveMap.get(currentUserName).currentCapacity = driveMap.get(currentUserName).currentCapacity.subtract(onePackage.weight);
            routeAfterPickUpFromFirstCity.add(onePackage);

            if (onePackage.idCityDelivery != currentCity) {
                putPackagesFromCityInRoute(routeAfterPickUpFromFirstCity, currentCity);
            }
            currentCity = onePackage.idCityDelivery;
        }


        putPackagesFromCityInRoute(routeAfterPickUpFromFirstCity, currentCity);
        routeAfterPickUpFromFirstCity.add(startPackage); // return to start point

        List<Package> route = new LinkedList<>();
        route.addAll(forPickupStartCityPath);
        route.addAll(routeAfterPickUpFromFirstCity);

        driveMap.get(currentUserName).route = route;

    }

    private void putPackagesFromCityInRoute(List<Package> routeAfterPickUpFromFirstCity, int currentCity) {
        Pair<List<Package>, List<Package>> packages = pickUpAllPackagesFromCity(currentCity);
        if (!packages.getKey().isEmpty()) {
            setPathPartType(packages.getKey(), Package.PATH_PART_TYPE.PICK_UP_NO_DELIVERY);

            routeAfterPickUpFromFirstCity.addAll(packages.getKey());
            if (!packages.getValue().isEmpty()) {
                driveMap.get(currentUserName).wareHousePackages.put(currentCity, packages.getValue());
            }
        }
    }


    private BigDecimal calculateProfit() {
        BigDecimal distance = calculateDistance();
        BigDecimal income = calculateIncome();

        BigDecimal priceByL = BigDecimal.valueOf(driveMap.get(currentUserName).fuelType.getPriceByL());
        BigDecimal consumptionByKm = driveMap.get(currentUserName).fuelConsumption.multiply(priceByL);
        BigDecimal los = consumptionByKm.multiply(distance);

        return income.subtract(los);

    }

    private BigDecimal calculateIncome() {
        BigDecimal profit = BigDecimal.ZERO;
        for (Package current : driveMap.get(currentUserName).route) {
            if (current.pathPartType == Package.PATH_PART_TYPE.DELIVER) {
                profit = profit.add(current.price);
            }
        }
        return profit;
    }

    private BigDecimal calculateDistance() {
        BigDecimal distamce = BigDecimal.ZERO;
        Package current = driveMap.get(currentUserName).route.get(driveMap.get(currentUserName).route.size() - 1);
        for (Package next : driveMap.get(currentUserName).route) {

            int xCoordFirst = current.xCoordDelivery;
            int yCoordFirst = current.yCoordDelivery;

            if (current.isWareHouse) {
                xCoordFirst = current.xCoordWareHouse;
                yCoordFirst = current.yCoordWareHouse;
            } else {
                if (current.pathPartType != Package.PATH_PART_TYPE.DELIVER) {
                    xCoordFirst = current.xCoordPickUp;
                    yCoordFirst = current.yCoordPickUp;
                }
            }

            int xCoordSecond = next.xCoordDelivery;
            int yCoordSecond = next.yCoordDelivery;

            if (next.isWareHouse) {
                xCoordSecond = next.xCoordWareHouse;
                yCoordSecond = next.yCoordWareHouse;
            } else {
                if (next.pathPartType != Package.PATH_PART_TYPE.DELIVER) {
                    xCoordSecond = next.xCoordPickUp;
                    yCoordSecond = next.yCoordPickUp;
                }
            }


            distamce = distamce.add(Util.calculateDistance(xCoordFirst, yCoordFirst, xCoordSecond, yCoordSecond));
            current = next;
        }
        return distamce;
    }

    private Pair<List<Package>, List<Package>> pickUpAllPackagesFromCity(int idCity) {
        List<Package> forPickUpPath = new LinkedList<>();

        List<Package> forPickUpFromCity = getAllNotTakenPackagesFromCity(idCity);

        final BigDecimal[] currentCapacity = {driveMap.get(currentUserName).currentCapacity};
        BigDecimal maxCapacity = driveMap.get(currentUserName).capacity;


        forPickUpFromCity.forEach(onePackage -> {
            BigDecimal tempCapacity = currentCapacity[0].add(onePackage.weight);
            if (tempCapacity.compareTo(maxCapacity) <= 0) {
                forPickUpPath.add(onePackage.clone());
                currentCapacity[0] = tempCapacity;
            }
        });

        List<Package> wareHousePickUp = new LinkedList<>();
        if (currentCapacity[0].compareTo(maxCapacity) < 0) {
            int idWareHouse = new na170675_StockroomOperations().getWareHouse(idCity);
            List<Package> forPickUpFromWareHouse = getAllPackagesFromWareHouse(idWareHouse);
            forPickUpFromWareHouse.forEach(onePackage -> {
                BigDecimal tempCapacity = currentCapacity[0].add(onePackage.weight);
                if (tempCapacity.compareTo(maxCapacity) <= 0) {
                    Package newPackage = onePackage.clone();
                    wareHousePickUp.add(newPackage);
                    currentCapacity[0] = tempCapacity;
                }
            });

            if (!wareHousePickUp.isEmpty()) {
                Package warehouse = makePackageWareHouse(idCity);
                warehouse.idCityDelivery = idCity;
                forPickUpPath.add(warehouse);
            }
        }
        return new Pair<>(forPickUpPath, wareHousePickUp);
    }

    private Package findClosestOne(List<Package> packagesForDelivery, Package currentPackage) {
        Package closestOne = packagesForDelivery.get(0);
        BigDecimal minDistance = null;
        for (Package onePackage : packagesForDelivery) {
            if (!onePackage.equals(currentPackage)) {
                BigDecimal distance = calculateEuclidDistance(onePackage, currentPackage);
                if (minDistance == null || minDistance.compareTo(distance) >= 0) {

                    minDistance = distance;
                    closestOne = onePackage;
                }
            }
        }
        return closestOne;
    }

    private BigDecimal calculateEuclidDistance(Package firstPackage, Package secondPackage) {
        return Util.calculateDistance(firstPackage.xCoordDelivery, firstPackage.yCoordDelivery, secondPackage.xCoordDelivery, secondPackage.yCoordDelivery);
    }

    private Package makeStartPackage() {
        Package startPackage = new Package();
        Pair<Integer, Integer> coordinates = getWareHouseCoordinates(driveMap.get(currentUserName).idCity);
        startPackage.xCoordWareHouse = startPackage.xCoordDelivery = startPackage.xCoordPickUp = coordinates.getKey();
        startPackage.yCoordWareHouse = startPackage.yCoordDelivery = startPackage.yCoordPickUp = coordinates.getValue();
        startPackage.isWareHouse = true;
        startPackage.idEnd = true;
        return startPackage;
    }

    private Package makePackageWareHouse(int idCity) {
        Package wareHouse = new Package();
        Pair<Integer, Integer> coordinates = getWareHouseCoordinates(idCity);
        wareHouse.xCoordWareHouse = wareHouse.xCoordDelivery = wareHouse.xCoordPickUp = coordinates.getKey();
        wareHouse.yCoordWareHouse = wareHouse.yCoordDelivery = wareHouse.yCoordPickUp = coordinates.getValue();
        wareHouse.isWareHouse = true;
        wareHouse.idCityDelivery = idCity;
        return wareHouse;
    }

    private List<Package> getAllNotTakenPackagesFromCity(int idCity) {
        List<Package> packages = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("" +
                "SELECT  weight, RTD.idRequest, " +
                " PickUp.xCoord, PickUp.yCoord, " +
                " Delivery.xCoord, Delivery.yCoord, " +
                " Delivery.idCity, Delivery.IdAddress, " +
                " price, Package.idAddress" +
                " FROM Package " + "INNER JOIN RequestToDelivery RTD on RTD.idRequest = Package.idRequest " + "INNER JOIN Address PickUp on PickUp.IdAddress = RTD.pickUpAdress " + "INNER JOIN Address Delivery on Delivery.IdAddress = RTD.deliveryAdress" + " WHERE deliveryStatus = ? AND PickUp.idCity = ? AND markForPickUp = ? " + " ORDER BY acceptanceTime")
        ) {
            ps.setInt(1, PackageStatus.ACCEPTED.ordinal());
            ps.setInt(2, idCity);
            ps.setInt(3, 0);
            try (
                    ResultSet rs = ps.executeQuery()
            ) {
                while (rs.next()) {
                    packages.add(new Package(rs));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    private List<Package> getAllPackagesFromWareHouse(int idWareHouse) {
        List<Package> packages = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("" +
                "SELECT  weight, PackageInWareHouse.idPackage," +
                " PickUp.xCoord, PickUp.yCoord, " +
                " DeliveryA.xCoord, DeliveryA.yCoord, " +
                " DeliveryA.idCity, DeliveryA.IdAddress, " +
                " price, Package.idAddress " +
                " FROM PackageInWareHouse  " + " INNER JOIN RequestToDelivery RTD on RTD.idRequest = PackageInWareHouse.idPackage  " + " INNER JOIN WarehouseLocation WHL on WHL.idWarehouse = PackageInWareHouse.idWareHouse " + " INNER JOIN Address PickUp on PickUp.IdAddress = WHL.idAddress " + " INNER JOIN Address DeliveryA on DeliveryA.IdAddress = RTD.deliveryAdress" + " INNER JOIN Package ON RTD.idRequest = Package.idRequest" + " WHERE PackageInWareHouse.idWareHouse = ?  AND markForPickUp = ?" + " ORDER BY acceptanceTime")) {
            ps.setInt(1, idWareHouse);
            ps.setInt(2, 0);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    packages.add(new Package(rs));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }

    private void setPackagesForPickUp(List<Package> packages) {
        try (PreparedStatement ps = mConnection.prepareStatement("UPDATE Package SET markForPickUp = ? WHERE idRequest = ?");) {
            packages.forEach(onePackage -> {
                try {
                    ps.setInt(1, 1);
                    ps.setInt(2, onePackage.idPackage);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    mLogger.log(Level.WARNING, null, ex);
                }
            });

        } catch (SQLException e) {
            mLogger.log(Level.WARNING, null, e);
        }
    }


    private void setPathPartType(List<Package> packages, Package.PATH_PART_TYPE newType) {
        for (Package onePackages : packages) {
            onePackages.pathPartType = newType;
        }
    }


    private void assignVehicleToUser(int vehicleId, int userId) {
        try (PreparedStatement statement = mConnection.prepareStatement("INSERT INTO VehicleInDrive (idUser, IdVehicle) VALUES (?,?)");) {
            statement.setInt(1, userId);
            statement.setInt(2, vehicleId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private int findFreeVehicle(int cityId) {
        int retVal = -1;
        try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT idVehicle FROM VehicleInWareHouse " + "INNER JOIN  WarehouseLocation WL on VehicleInWareHouse.idWarehouse = WL.idWarehouse " + "INNER JOIN Address A on A.IdAddress = WL.idAddress WHERE  idCity = ?")) {
            ps1.setInt(1, cityId);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    retVal = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    @Override
    public int nextStop(String userName) {
        int retVal = -3;
        if (driveMap.containsKey(userName)) {
            currentUserName = userName;
            int currentPackageIndex = driveMap.get(userName).packageIndex;
            Package currentPackage = driveMap.get(userName).route.get(currentPackageIndex);
            currentPackageIndex += 1;
            driveMap.get(userName).packageIndex = currentPackageIndex;
            if (currentPackageIndex == driveMap.get(userName).route.size()) {
                handleEndOfDrive();
                return -1;
            } else {
                retVal = handlePackageStop(currentPackage);
            }
        }
        return retVal;
    }


    private int handlePackageStop(Package currentPackage) {
        int retVal = -3;
        switch (currentPackage.pathPartType) {
            case PICK_UP_AND_SET_TO_DELIVER_LATER:
                retVal = handlePickUp(currentPackage, false);
                break;
            case PICK_UP_NO_DELIVERY:
                retVal = handlePickUp(currentPackage, true);
                break;
            case DELIVER:
                retVal = handlePackageDeliver(currentPackage);
                break;
        }
        return retVal;
    }

    private int handlePickUp(Package currentPackage, boolean shouldTransferToWareHouse) {
        int retVal;
        if (currentPackage.isWareHouse) {
            handleWarehousePickUp(currentPackage, shouldTransferToWareHouse);
        } else {
            handlePackagePickUp(shouldTransferToWareHouse, currentPackage);
        }
        retVal = -2;
        return retVal;
    }

    private int handlePackageDeliver(Package currentPackage) {
        dropPackage(currentPackage.idPackage, currentPackage.idAddressDelivery);
        increaseCourierDeliveredPackageCount();
        return currentPackage.idPackage;
    }

    private void increaseCourierDeliveredPackageCount() {
        int idUser = Util.getUserId(currentUserName);
        try (
                PreparedStatement ps = mConnection.prepareStatement("UPDATE Courier SET numOfdelivery =  numOfdelivery + 1 WHERE idUser = ?");
        ) {
            ps.setInt(1, idUser);
            ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
    }

    private void dropPackage(int idPackage, int idAddress) {
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PackageInVehicle WHERE idVehicle = ? AND idPackage = ?");
        ) {
            ps.setInt(1, driveMap.get(currentUserName).idVehicle);
            ps.setInt(2, idPackage);
            ps.executeUpdate();
            changePackageDeliveryStatus(idPackage, PackageStatus.DELIVERED.ordinal());
            changeCurrentLocation(idPackage, idAddress);
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private int handlePackagePickUp(boolean shouldTransferToWareHouse, Package... packages) {
        try (
                PreparedStatement ps = mConnection.prepareStatement("INSERT INTO PackageInVehicle(idPackage, idVehicle) VALUES(?,?)");
        ) {
            ps.setInt(2, driveMap.get(currentUserName).idVehicle);
            for (Package onePackage : packages) {
                ps.setInt(1, onePackage.idPackage);
                ps.executeUpdate();
                changePackageDeliveryStatus(onePackage.idPackage, PackageStatus.TAKEN.ordinal());
                changeCurrentLocation(onePackage.idPackage, -1);
                if (shouldTransferToWareHouse) {
                    driveMap.get(currentUserName).forWareHouse.add(onePackage);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return 0;
    }

    private void changeCurrentLocation(int idPackage, int newLocation) {
        String sql = "UPDATE Package SET idAddress = ? WHERE idRequest= ?";
        if (newLocation == -1) {
            sql = "UPDATE Package SET idAddress = NULL WHERE idRequest= ?";
        }
        try (
                PreparedStatement ps = mConnection.prepareStatement(sql);
        ) {
            int indexNext = 1;
            if (newLocation != -1) {
                ps.setInt(indexNext, newLocation);
                indexNext++;
            }
            ps.setInt(indexNext, idPackage);
            ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
    }

    private void changePackageDeliveryStatus(int idPackage, int newStatus) {

        try (
                PreparedStatement ps = mConnection.prepareStatement("UPDATE Package SET deliveryStatus = ? WHERE idRequest= ?");
        ) {
            ps.setInt(1, newStatus);
            ps.setInt(2, idPackage);
            ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
    }


    private int getWareHouseIdByAddress(int wareHouseAddress) {
        int retVal = 0;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT idWarehouse FROM WarehouseLocation WHERE idAddress = ?");
        ) {
            ps.setInt(1, wareHouseAddress);
            try (
                    ResultSet rs = ps.executeQuery();
            ) {
                if (rs.next()) {
                    retVal = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    private int handleWarehousePickUp(Package currentPackage, boolean shouldTransferToWareHouse) {
        int idCity = currentPackage.idCityDelivery;
        if (driveMap.get(currentUserName).wareHousePackages.containsKey(idCity)) {
            List<Package> packages = driveMap.get(currentUserName).wareHousePackages.get(idCity);
            removeFromWareHouse(packages.toArray(new Package[0]));

            handlePackagePickUp(shouldTransferToWareHouse, packages.toArray(new Package[0]));
        }
        return 0;
    }

    private void handleEndOfDrive() {
        transferPackageFromVehicleToWareHouse();
        updateCourierProfit();
        removeAssignedVehicleFromCourier();
        changeCourierStatus(driveMap.get(currentUserName).idUser, CourierStatus.NOT_DRIVE);
        driveMap.remove(currentUserName);
    }

    private void removeAssignedVehicleFromCourier() {
        try (PreparedStatement statement = mConnection.prepareStatement("DELETE FROM VehicleInDrive WHERE IdVehicle= ?")) {
            statement.setInt(1, driveMap.get(currentUserName).idVehicle);
            statement.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void updateCourierProfit() {
        BigDecimal newProfit = calculateProfit();
        try (PreparedStatement ps = mConnection.prepareStatement("UPDATE Courier SET income = income + ? where idUser = ?")) {
            ps.setBigDecimal(1, newProfit);
            ps.setInt(2, driveMap.get(currentUserName).idUser);
            ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void transferPackageFromVehicleToWareHouse() {
        putInWareHouse();
        removeFromVehicle();
        changePackagesLocationToStartWareHouse();
    }


    private int getWareHouseAddress(int idWareHouse) {
        int retVal = -1;
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT  idAddress FROM WarehouseLocation WHERE idWarehouse = ?");
        ) {
            ps.setInt(1, idWareHouse);
            try (
                    ResultSet rs = ps.executeQuery();
            ) {
                if (rs.next()) {
                    retVal = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private void changePackagesLocationToStartWareHouse() {
        int wareHouse = getWareHouseAddress(driveMap.get(currentUserName).idWareHouse);
        for (Package onePackage : driveMap.get(currentUserName).forWareHouse) {
            changeCurrentLocation(onePackage.idPackage, wareHouse);
        }
    }

    private void removeFromVehicle() {
        List<Package> packages = driveMap.get(currentUserName).forWareHouse;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PackageInVehicle WHERE idVehicle = ? AND idPackage = ?");
        ) {
            ps.setInt(1, driveMap.get(currentUserName).idVehicle);
            for (Package onePackage : packages) {
                ps.setInt(2, onePackage.idPackage);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void putInWareHouse() {
        List<Package> packages = driveMap.get(currentUserName).forWareHouse;
        try (
                PreparedStatement ps = mConnection.prepareStatement("INSERT INTO PackageInWareHouse(idWareHouse, idPackage) VALUES(?, ?)");
        ) {
            ps.setInt(1, driveMap.get(currentUserName).idWareHouse);
            for (Package onePackage : packages) {
                ps.setInt(2, onePackage.idPackage);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }


    private void removeFromWareHouse(Package... packages) {
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PackageInWareHouse WHERE idWareHouse = ? AND idPackage = ?");
        ) {
            for (Package onePackage : packages) {
                ps.setInt(1, getWareHouseIdByAddress(onePackage.wareHouseAddress));
                ps.setInt(2, onePackage.idPackage);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    @Override
    public List<Integer> getPackagesInVehicle(String s) {
        List<Integer> packages = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("Select idPackage FROM PackageInVehicle")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int packageId = rs.getInt(1);
                    packages.add(packageId);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return packages;
    }


    private static class DriveInfo {

        public List<Package> packagesForPickUp;
        public List<Package> route;
        public int packageIndex;
        public int idCity;
        public FuelType fuelType;
        private int idUser;
        private int idVehicle;
        private BigDecimal fuelConsumption;
        private int idWareHouse;
        private BigDecimal capacity;
        private BigDecimal currentCapacity;
        private List<Package> packagesForDelivery;
        private HashMap<Integer, List<Package>> wareHousePackages;

        private List<Package> forWareHouse = new LinkedList<>();
    }


    private static class Package implements Cloneable {

        public boolean idEnd = false;
        public BigDecimal price;

        public enum PATH_PART_TYPE {
            PICK_UP_NO_DELIVERY, PICK_UP_AND_SET_TO_DELIVER_LATER, DELIVER
        }

        public boolean isWareHouse;
        public PATH_PART_TYPE pathPartType;

        public int idCityDelivery;

        public int wareHouseAddress;
        public int idAddressDelivery;
        private int idPackage;
        private BigDecimal weight;
        private int xCoordPickUp;
        private int yCoordPickUp;
        private int xCoordDelivery;
        private int yCoordDelivery;
        private int xCoordWareHouse;
        private int yCoordWareHouse;

        public Package() {

        }

        public Package(ResultSet rs) {
            try {
                weight = rs.getBigDecimal(1);
                idPackage = rs.getInt(2);
                xCoordPickUp = rs.getInt(3);
                yCoordPickUp = rs.getInt(4);
                xCoordDelivery = rs.getInt(5);
                yCoordDelivery = rs.getInt(6);
                idCityDelivery = rs.getInt(7);
                idAddressDelivery = rs.getInt(8);
                price = rs.getBigDecimal(9);
                wareHouseAddress = rs.getInt(10);
            } catch (SQLException e) {

            }

        }




        @Override
        public Package clone() {
            try {
                return (Package) super.clone();

            } catch (CloneNotSupportedException e) {
               return new Package();
            }
        }


        @Override
        public int hashCode() {
            return this.idPackage;
        }

        @Override
        public boolean equals(Object obj) {
            boolean retVal = false;
            if (obj instanceof Package) {
                Package onePackage = (Package) obj;
                retVal = onePackage.idPackage == this.idPackage;
            }
            return retVal;
        }
    }


}
