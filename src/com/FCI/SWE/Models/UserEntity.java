package com.FCI.SWE.Models;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * <h1>User Entity class</h1>
 * <p>
 * This class will act as a model for user, it will holds user data
 * </p>
 *
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 */
public class UserEntity {
	private String name;
	private String email;
	private String password;

	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */
	public UserEntity(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;

	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}

	/**
	 * 
	 * This static method will form UserEntity class using json format contains
	 * user data
	 * 
	 * @param json
	 *            String in json format contains user data
	 * @return Constructed user entity
	 */
	public static UserEntity getUser(String json) {

		JSONParser parser = new JSONParser();
		try {
			JSONObject object = (JSONObject) parser.parse(json);
			return new UserEntity(object.get("name").toString(), object.get(
					"email").toString(), object.get("password").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * This static method will form UserEntity class using user name and
	 * password This method will serach for user in datastore
	 * 
	 * @param name
	 *            user name
	 * @param pass
	 *            user password
	 * @return Constructed user entity
	 */

	public static UserEntity getUser(String name, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query("users");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			System.out.println(entity.getProperty("name").toString());
			if (entity.getProperty("name").toString().equals(name)
					&& entity.getProperty("password").toString().equals(pass)) {
				UserEntity returnedUser = new UserEntity(entity.getProperty(
						"name").toString(), entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());
				return returnedUser;
			}
		}

		return null;
	}

	/**
	 * This method will be used to save user object in datastore
	 * 
	 * @return boolean if user is saved correctly or not
	 */
	public Boolean saveUser() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query query = new Query("users");
		PreparedQuery pq = datastore.prepare(query);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

		Entity employee = new Entity("users", list.size() + 1);

		employee.setProperty("name", this.name);
		employee.setProperty("email", this.email);
		employee.setProperty("password", this.password);
		datastore.put(employee);

		return true;

	}
	
	/**
	 * This method will be used to save friend request object in datastore
	 * 
	 * @return if request is saved correctly or not
	 */
	
	public static int sendFriendRequest(String toUser, String currentUser) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		boolean flag = false;
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			
			if (entity.getProperty("name").toString().equals(toUser)) {
				flag = true;
				break;
				}
		}
		
		if (!flag)
			return 0;
		
		
		gaeQuery = new Query("friends");
		pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			
			if ((entity.getProperty("friend1").toString().equals(toUser) && entity.getProperty("friend2").toString().equals(currentUser)) || (entity.getProperty("friend1").toString().equals(currentUser) && entity.getProperty("friend2").toString().equals(toUser))) {
				flag = false;
				break;
				}
		}
		
		if(!flag)
			return 1;
		
		gaeQuery = new Query("requests");
		pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		Entity request = new Entity("requests", list.size() + 1);
		request.setProperty("currentUser", currentUser);
		request.setProperty("toUser", toUser);
		request.setProperty("status", 1);
		datastore.put(request);
		
		return 2;
	}
	
	/**
	 * This method will be used to save friend request object in datastore
	 * 
	 * @return boolean if accepted request is saved correctly or not
	 */
	
	public static boolean acceptFriendRequest(String toUser, String currentUser) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query gaeQuery = new Query("requests");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if(entity.getProperty("currentUser").equals(toUser) && entity.getProperty("toUser").equals(currentUser)){
				entity.setProperty("status", 0);
				datastore.put(entity);
				
				gaeQuery = new Query("friends");
				pq = datastore.prepare(gaeQuery);
				List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

				Entity friends = new Entity("friends", list.size() + 1);

				friends.setProperty("friend1", currentUser);
				friends.setProperty("friend2", toUser);
				datastore.put(friends);
				
				return true;
			}
		}
		return false;
	}



//////////////////////////////////  message
public static int sendMessageService(String toUser, String currentUser , String message_text) {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	boolean flag = false;
	Query gaeQuery = new Query("users");
	PreparedQuery pq = datastore.prepare(gaeQuery);
	for (Entity entity : pq.asIterable()) {
		
		if (entity.getProperty("name").toString().equals(toUser)) {
			flag = true;
			break;
			}
	}
	
	if (!flag)
		return 0;
	
	
	gaeQuery = new Query("friends");
	pq = datastore.prepare(gaeQuery);
	for (Entity entity : pq.asIterable()) {
		
		if ((entity.getProperty("friend1").toString().equals(toUser) && entity.getProperty("friend2").toString().equals(currentUser)) || (entity.getProperty("friend1").toString().equals(currentUser) && entity.getProperty("friend2").toString().equals(toUser))) {
			flag = false;
			break;
			}
	}
	
	if(!flag)
		return 1;
	
	gaeQuery = new Query("message");
	pq = datastore.prepare(gaeQuery);
	List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
	Entity message = new Entity("message", list.size() + 1);
	message.setProperty("currentUser", currentUser);
	message.setProperty("toUser", toUser);
	message.setProperty("message", 1);
	datastore.put(message);
	
	return 2;
}
}
