package rs.etf.sab.student.operations;

import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.student.sql_helper.DB;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_GeneralOperations implements GeneralOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_GeneralOperations.class.getName());

    public na170675_GeneralOperations() {
        mLogger.setLevel(Level.WARNING);
    }

    @Override
    public void eraseAll() {

        deleteAllWareHouseVehicles();
        deleteAllVehiclesInDrive();

        deleteAllPackagesInVehicle();
        deleteAllPackagesWareHouse();

        deleteAllPackages();
        deleteAllRequestToDeliver();

        deleteAllPackagesWareHouse();
        deleteAllVehiclesInDrive();
        deleteAllVehicles();
        deleteAllWareHouses();

        deleteCourierRequests();
        deleteCouriers();
        deleteAllAdmins();
        deleteAllUsers();

        deleteAddress();
        deleteAllCities();
    }

    private void deleteAllRequestToDeliver() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM RequestToDelivery")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllPackages() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Package")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllPackagesInVehicle() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PackageInVehicle")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllPackagesWareHouse() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PackageInWareHouse")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }


    private void deleteAllVehicles() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Vehicle")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllVehiclesWareHouse() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM VehicleInWareHouse")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllVehiclesInDrive() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM VehicleInDrive")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllWareHouseVehicles() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM VehicleInWareHouse")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }


    private void deleteAllWareHouses() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM WarehouseLocation")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllCities() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM City")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAddress() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Address")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteCourierRequests() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM CourierRequest")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteCouriers() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Courier")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllUsers() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PERSON")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

    private void deleteAllAdmins() {
        try (PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Administrator")) {
            ps.execute();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
    }

}
