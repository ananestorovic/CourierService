package rs.etf.sab.student.operations;

import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.student.helpers.Util;
import rs.etf.sab.student.sql_helper.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class na170675_CourierRequestOperation implements CourierRequestOperation {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_CourierRequestOperation.class.getName());

    public na170675_CourierRequestOperation() {
        mLogger.setLevel(Level.SEVERE);
    }
    @Override
    public boolean insertCourierRequest(String username, String licenceNumber) {
        boolean retVal = false;
        int idUser = Util.getUserId(username);
        if (idUser != -1 && !requestExist(idUser) && !isCourier(idUser)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("INSERT INTO CourierRequest (idUser, licenceNumber) values (?, ?)")
            ) {
                ps.setInt(1, idUser);
                ps.setString(2, licenceNumber);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isCourier(int idUser) {
        boolean retVal = false;
        try (
                PreparedStatement ps1 = mConnection.prepareStatement("SELECT idUser FROM Courier WHERE idUser=?")
        ) {
            ps1.setInt(1, idUser);
            try (
                    ResultSet rs = ps1.executeQuery()
            ) {
                retVal = rs.next();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    private boolean requestExist(int idUser) {
        boolean retVal = false;
        try (
                PreparedStatement ps1 = mConnection.prepareStatement("SELECT idUser FROM CourierRequest WHERE idUser=?")
        ) {
            ps1.setInt(1, idUser);
            try (
                    ResultSet rs = ps1.executeQuery()
            ) {
                retVal = rs.next();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    @Override
    public boolean deleteCourierRequest(String username) {
        boolean retVal = false;
        int isUser = Util.getUserId(username);
        if (isUser != -1 && requestExist(isUser)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("DELETE FROM CourierRequest WHERE idUser = ?")
            ) {
                ps.setInt(1, isUser);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;

    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String username, String newLicenceNumber) {
        boolean retVal = false;
        int idUser = Util.getUserId(username);
        if (idUser != -1 && requestExist(idUser)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("UPDATE CourierRequest SET licenceNumber = ? WHERE idUser = ?")
            ) {
                ps.setString(1, newLicenceNumber);
                ps.setInt(2, idUser);
                retVal = ps.executeUpdate() == 1;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public List<String> getAllCourierRequests() {
        return getAllCourierRequestsId().stream().map(Util::getUsername).collect(Collectors.toList());
    }

    private List<Integer> getAllCourierRequestsId() {
        List<Integer> courierRequests = new LinkedList<>();
        try (PreparedStatement ps = mConnection.prepareStatement("SELECT idUser FROM CourierRequest ")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cityId = rs.getInt(1);
                    courierRequests.add(cityId);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return courierRequests;
    }

    @Override
    public boolean grantRequest(String username) {
        boolean retVal = false;
        int idUser = Util.getUserId(username);
        if (idUser != -1) {
            String licencePlate = getRequestLicence(idUser);
            retVal = new na170675_CourierOperations().insertCourier(username, licencePlate);
            if (retVal) {
                retVal = deleteCourierRequest(username);
            }
        }
        return retVal;
    }

    private String getRequestLicence(int idUser) {
        String retVal = null;
        if (idUser != -1) {
            try (
                    PreparedStatement ps1 = mConnection.prepareStatement("SELECT licenceNumber FROM CourierRequest WHERE idUser=?")
            ) {
                ps1.setInt(1, idUser);
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
        }
        return retVal;
    }
}
