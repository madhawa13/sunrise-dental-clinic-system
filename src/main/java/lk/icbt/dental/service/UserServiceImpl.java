package lk.icbt.dental.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.UserDAO;
import lk.icbt.dental.dao.impl.UserDAOImpl;
import lk.icbt.dental.model.User;

/**
 * Implements user-related business operations.
 */
public class UserServiceImpl
        implements UserService {

    private final UserDAO userDAO;

    /**
     * Constructor used by the real application.
     */
    public UserServiceImpl() {
        this(new UserDAOImpl());
    }

    /**
     * Constructor used by automated tests.
     */
    public UserServiceImpl(UserDAO userDAO) {

        if (userDAO == null) {
            throw new IllegalArgumentException(
                    "User DAO cannot be null");
        }

        this.userDAO = userDAO;
    }

    /**
     * Returns active dentists sorted by the DAO query.
     */
    @Override
    public List<User> getActiveDentists()
            throws SQLException {

        List<User> dentists =
                userDAO.findActiveDentists();

        if (dentists == null) {
            return Collections.emptyList();
        }

        return dentists;
    }

    /**
     * Finds an active user by username.
     */
    @Override
    public Optional<User> findByUsername(
            String username)
            throws SQLException {

        if (username == null
                || username.isBlank()) {

            return Optional.empty();
        }

        return userDAO.findByUsername(
                username.trim());
    }

    /**
     * Finds an active user by ID.
     */
    @Override
    public Optional<User> findById(long userId)
            throws SQLException {

        if (userId <= 0) {
            return Optional.empty();
        }

        return userDAO.findById(userId);
    }
}