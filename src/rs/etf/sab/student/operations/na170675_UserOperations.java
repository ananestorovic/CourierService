package rs.etf.sab.student.operations;

import rs.etf.sab.operations.UserOperations;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class na170675_UserOperations implements UserOperations {

    private final Connection mConnection = DB.getInstance().getConnection();
    private final Logger mLogger = Logger.getLogger(na170675_UserOperations.class.getName());

    public na170675_UserOperations() {
        mLogger.setLevel(Level.SEVERE);
    }


    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idAddress) {
        boolean retVal = false;
        if (isFirstNameValid(firstName) && isSecondNameValid(lastName) && isPasswordFormatValid(password)) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("INSERT INTO PERSON (username, name, surname, password, idAddr) VALUES (?, ?, ?, ?, ?)")
            ) {
                ps.setString(1, userName);
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setString(4, password);
                ps.setInt(5, idAddress);
                ps.execute();
                retVal = true;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private boolean isPasswordFormatValid(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*^[a-zA-Z\\d])(?=.*\\d).{8,}$");
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    private boolean isFirstNameValid(String firstName) {
        boolean retVal = false;
        if (firstName != null && !firstName.isEmpty()) {
            retVal = Character.isUpperCase(firstName.charAt(0));
        }
        return retVal;
    }

    private boolean isSecondNameValid(String secondName) {
        boolean retVal = false;
        if (secondName != null && !secondName.isEmpty()) {
            retVal = Character.isUpperCase(secondName.charAt(0));
        }
        return retVal;
    }


    @Override
    public boolean declareAdmin(String userName) {
        boolean retVal = false;
        int userId = Util.getUserId(userName);
        if (userId != -1) {
            try (
                    PreparedStatement statement = mConnection.prepareStatement("SELECT * FROM Administrator WHERE idUser = ?")
            ) {
                statement.setInt(1, userId);
                try (
                        ResultSet resultSet = statement.executeQuery()
                ) {
                    if (!resultSet.next()) {
                        insertAdmin(userId);
                        retVal = true;
                    }
                }
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    private void insertAdmin(int userId) throws SQLException {
        try (
                PreparedStatement statementInsertAdmin = mConnection.prepareStatement("INSERT INTO Administrator(idUser) VALUES (?)")
        ) {
            statementInsertAdmin.setInt(1, userId);
            statementInsertAdmin.executeUpdate();
        }
    }

    @Override
    public int getSentPackages(String... userNames) {
        boolean someUserExist = false;
        int sumOfAllSentPackages = 0;
        for (String userName : userNames) {
            int userId = Util.getUserId(userName);
            if (userId != -1) {
                someUserExist = true;
                sumOfAllSentPackages += getUserSentPackages(userId);
            }
        }
        int retVal = sumOfAllSentPackages;
        if (!someUserExist) {
            retVal = -1;
        }
        return retVal;
    }

    private int getUserSentPackages(int userId) {
        return 0;
    }

    @Override
    public int deleteUsers(String... userNames) {
        int numberDeleted = 0;
        for (String userName : userNames) {
            if (deleteUser(userName)) {
                numberDeleted += 1;
            }
        }
        return numberDeleted;
    }

    private boolean deleteUser(String userName) {
        boolean retVal = false;
        int userId = Util.getUserId(userName);
        if (userId != -1) {
            try (
                    PreparedStatement ps = mConnection.prepareStatement("DELETE FROM PERSON WHERE idUser=?")
            ) {
                ps.setInt(1, userId);
                retVal = ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                mLogger.log(Level.WARNING, null, ex);
            }
        }
        return retVal;
    }

    @Override
    public List<String> getAllUsers() {
        List<String> userList = new LinkedList<>();
        try (
                PreparedStatement ps = mConnection.prepareStatement("SELECT username FROM PERSON")
        ) {
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userList.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            mLogger.log(Level.WARNING, null, ex);
        }
        return userList;
    }


}
