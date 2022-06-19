package rs.etf.sab.student.helpers;

import kotlin.Pair;
import rs.etf.sab.student.sql_helper.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final Connection mConnection = DB.getInstance().getConnection();
    private static final Logger mLogger = Logger.getLogger(Util.class.getName());


    public static int getUserId(String userName) {
        int retVal = -1;
        if (userName != null) {
            try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT idUser FROM Person WHERE username=?")) {
                ps1.setString(1, userName);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        retVal = rs.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    public static String getUsername(int userId) {
        String retVal = null;
        try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT username FROM Person WHERE idUser = ?")) {
            ps1.setInt(1, userId);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    retVal = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }

    public static int getUserCity(int userId) {
        int retVal = -1;
        try (PreparedStatement ps1 = mConnection.prepareStatement("SELECT idCity FROM PERSON INNER JOIN Address A on A.IdAddress = Person.idAddr WHERE idUser = ?")) {
            ps1.setInt(1, userId);
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

    public static BigDecimal calculateDistance(int startX, int startY, int endX, int endY) {
        return BigDecimal.valueOf(Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2)));
    }

    public Pair<Integer, Integer> getCoordinates(int idAddress) {
        Pair<Integer, Integer> retVal = null;
        try (PreparedStatement ps = mConnection.prepareStatement("SELECT xCoord, yCoord FROM Address WHERE IdAddress = ?")) {
            ps.setInt(1, idAddress);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    retVal = new Pair<>(rs.getInt(1), rs.getInt(2));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    public BigDecimal calculateDistanceBetweenAddress(int addressFrom, int addressTo) {
        Pair<Integer, Integer> coordinatesFrom = getCoordinates(addressFrom);
        Pair<Integer, Integer> coordinatesTo = getCoordinates(addressTo);
        return calculateDistance(coordinatesFrom.component1(), coordinatesFrom.component2(), coordinatesTo.component1(), coordinatesFrom.component2());
    }

    public static boolean isCourierInDrive(int userId) {
        boolean retVal = false;
        try (
                PreparedStatement statement = mConnection.prepareStatement("SELECT * FROM VehicleInDrive WHERE idUser = ?");
        ) {
            statement.setInt(1, userId);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                retVal = rs.next();
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return retVal;
    }
}
