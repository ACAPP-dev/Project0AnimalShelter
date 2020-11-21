package com.animalshelter.dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.animalshelter.StartAnimalShelter;
import com.animalshelter.exception.UserNotCreatedException;
import com.animalshelter.exception.UserNotFoundException;
import com.animalshelter.model.Role;
import com.animalshelter.model.User;
import com.animalshelter.template.CreateUserTemplate;
import com.animalshelter.util.JDBCUtility;

public class DatabaseUserDAO {

	public ArrayList<User> getAllUsers() throws UserNotFoundException {

		ArrayList<User> users = new ArrayList();
		String sqlQuery = "SELECT * FROM users u INNER JOIN roles r ON u.role_id = r.role_id";

		try (Connection connection = JDBCUtility.getConnection()) {
			ResultSet rs = connection.createStatement().executeQuery(sqlQuery);

			while (rs.next()) {
				int userId = rs.getInt(1);
				int roleId = rs.getInt(2);
				String firstName = rs.getString(3);
				String lastName = rs.getString(4);
				String username = rs.getString(5);
				String roleName = rs.getString(8);

				users.add(new User(userId, firstName, lastName, username, new Role(roleId, roleName)));

			}

			return users;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		throw new UserNotFoundException();

	}

	public User findUserById(String resultKey, int resultValue) throws UserNotFoundException {

		String sqlQuery = "SELECT * FROM users u INNER JOIN roles r ON u.role_id = r.role_id WHERE " + resultKey
				+ " = ? LIMIT 1";

		try (Connection connection = JDBCUtility.getConnection()) {

			PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
			pstmt.setInt(1, resultValue);
			System.out.println(pstmt);

			return createUserFromResultSet(pstmt.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		throw new UserNotFoundException("User id not found.");
	}

	public User findUserByUsername(String resultKey, String resultValue) throws UserNotFoundException {

		String sqlQuery = "SELECT * FROM users u INNER JOIN roles r ON u.role_id = r.role_id WHERE " + resultKey
				+ " = ? LIMIT 1";

		try (Connection connection = JDBCUtility.getConnection()) {

			PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
			pstmt.setString(1, resultValue);
			System.out.println(pstmt);

			return createUserFromResultSet(pstmt.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		throw new UserNotFoundException("Username not found.");
	}

	public User findUserByLastName(String resultKey, String resultValue) throws UserNotFoundException {

		String sqlQuery = "SELECT * FROM users u INNER JOIN roles r ON u.role_id = r.role_id WHERE " + resultKey
				+ " = ? LIMIT 1";

		try (Connection connection = JDBCUtility.getConnection()) {

			PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
			pstmt.setString(1, resultValue);
			System.out.println(pstmt);

			return createUserFromResultSet(pstmt.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		throw new UserNotFoundException("Last name not found.");

	}

	public User createUser(int roleId, String firstName, String lastName, String username, String password)
			throws UserNotFoundException, UserNotCreatedException {
		String sqlQuery = "INSERT INTO users (role_id, first_name, last_name, username, password) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = JDBCUtility.getConnection()) {

			connection.setAutoCommit(false);

			PreparedStatement pstmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, roleId);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			pstmt.setString(4, username);
			pstmt.setString(5, password);

			System.out.println(pstmt);

			if (pstmt.executeUpdate() != 1) {
				throw new UserNotCreatedException("Insert user failed - no rows were affected");
			}

			int userId = 0;

			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				userId = generatedKeys.getInt(1);
			} else {
				throw new UserNotCreatedException("Insert user failed - no ID was generated");
			}

			connection.commit();

			return findUserById("user_id", userId);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new UserNotCreatedException();
	}

	User createUserFromResultSet(ResultSet rs) throws SQLException, UserNotFoundException {

		if (rs.next()) {
			System.out.println("rs getString result: " + rs.getString(3));

			int userId = rs.getInt(1);
			int roleId = rs.getInt(2);
			String firstName = rs.getString(3);
			String lastName = rs.getString(4);
			String username = rs.getString(5);
			String roleName = rs.getString(8);

			return new User(userId, firstName, lastName, username, new Role(roleId, roleName));
		} else {
			throw new UserNotFoundException("User not found.");
		}
	}
}