package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	public List<User> getUsersWithReviews(int minReviews) {
		// TODO Auto-generated method stub
		String sql = "select u.* "
				+ "from users u, reviews r "
				+ "where u.user_id = r.user_id "
				+ "group by u.user_id "
				+ "having count(*) >= ?";
		
		// oppure (query annidate)
		String sql2 = "select * "
				+ "from users "
				+ "where user_id in (select user_id "
				+ "from reviews "
				+ "group by user_id "
				+ "having count(*) >= ?)";
		
		List<User> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, minReviews);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int calcolaSimilarita(User u1, User u2, int anno) {
		String sql = "select count(*) as similarita "
				+ "from reviews r1, reviews r2 "
				+ "where r1.business_id = r2.business_id "
				+ "and r1.user_id = ? "
				+ "and r2.user_id = ? "
				+ "and year(r1.review_date) = ? "
				+ "and year(r2.review_date) = ?";
		
//		int similarita = 0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, u1.getUserId());
			st.setString(2, u2.getUserId());
			st.setInt(3, anno);
			st.setInt(4, anno);
			ResultSet res = st.executeQuery();
			res.first();
			int similarita = res.getInt("similarita");
			res.close();
			st.close();
			conn.close();
			return similarita;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
}
