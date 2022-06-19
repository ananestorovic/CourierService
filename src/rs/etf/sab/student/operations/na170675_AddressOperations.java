package rs.etf.sab.student.operations;

import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.student.sql_helper.DB;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class na170675_AddressOperations implements AddressOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_AddressOperations.class.getName());


    public na170675_AddressOperations() {
        mLogger.setLevel(Level.SEVERE);
    }

    @Override
    public int insertAddress(String streetName, int streetNumber, int idCity, int xCoordinate, int yCoordinate) {
        int retVal = -1;
        try (
                PreparedStatement ps = mConnection.prepareStatement("INSERT INTO Address(StreetName, StreetNumber, idCity, xCoord, yCoord) values(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, streetName);
            ps.setInt(2, streetNumber);
            ps.setInt(3, idCity);
            ps.setInt(4, xCoordinate);
            ps.setInt(5, yCoordinate);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys();) {
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
    public int deleteAddresses(String streetName, int streetNumber) {
        int deletedAdresses = 0;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Address WHERE streetName=? AND streetNumber=?")
        ) {
            ps.setString(1, streetName);
            ps.setInt(2, streetNumber);
            deletedAdresses = ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return deletedAdresses;
    }

    @Override
    public boolean deleteAdress(int idAddress) {
        boolean retVal = false;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Address WHERE IdAddress=?")
        ) {
            ps.setInt(1, idAddress);
            retVal = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    @Override
    public int deleteAllAddressesFromCity(int idCity) {
        int deleteNumber = 0;
        try (
                PreparedStatement ps = mConnection.prepareStatement("DELETE FROM Address Where idCity=?")
        ) {
            ps.setInt(1, idCity);
            deleteNumber = ps.executeUpdate();
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return deleteNumber;
    }

    @Override
    public List<Integer> getAllAddresses() {
        List<Integer> addresses = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT IdAddress FROM Address")
        ) {
            try (
                    ResultSet rs = ps.executeQuery()
            ) {
                while (rs.next()) {
                    addresses.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return addresses;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idCity) {
        List<Integer> idAdrList = null;
        if (getCityName(idCity) != null) {
            idAdrList = new LinkedList<>();
            try (
                    PreparedStatement ps = mConnection.prepareStatement("SELECT IdAddress FROM Address WHERE idCity=?")
            ) {
                ps.setInt(1, idCity);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        idAdrList.add(rs.getInt(1));
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return idAdrList;
    }

    private String getCityName(int idCity) {
        String retVal = null;
        try (
                PreparedStatement ps1 = mConnection.prepareStatement("SELECT name FROM City WHERE idCity=?")
        ) {
            ps1.setInt(1, idCity);
            try (
                    ResultSet rs = ps1.executeQuery()
            ) {
                if (rs.next()) {
                    retVal = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

}

