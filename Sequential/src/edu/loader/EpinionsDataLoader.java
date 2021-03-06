package edu.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.dal.DBConnectionManager;
import edu.models.UserBo;
import edu.models.UserItemRating;
import edu.models.UserTrustRating;

public class EpinionsDataLoader implements DataLoader {

	private static EpinionsDataLoader epinionsInstance = null;

	private static List<UserTrustRating> userTrustRatings = null;
	private static HashMap<Long, List<UserItemRating>> userItemRatingsHash = new HashMap<Long, List<UserItemRating>>();
	private static Set<Long> users = new TreeSet<Long>();
	private static HashMap<Long, List<Long>> userItemHash = new HashMap<Long, List<Long>>();
	private static HashMap<Long, List<Long>> itemUserHash = new HashMap<Long, List<Long>>();
	private static Map<Long, List<UserTrustRating>> userTrustRatingsHash = new HashMap<Long, List<UserTrustRating>>();

	private static Map<Long, UserBo> userBoMap = new HashMap<Long, UserBo>();

	private EpinionsDataLoader() {
	};

	public static EpinionsDataLoader getInstance() {
		if (epinionsInstance == null) {
			epinionsInstance = new EpinionsDataLoader();
		}
		if (null == userItemRatings) {
			loadUserItemRatings();
		}
		if (null == userTrustRatings) {
			loadUserTrustRatings();
		}
		if (null == userBoMap || userBoMap.isEmpty()) {
			loadUserBos();
		}
		return epinionsInstance;
	}

	private static void loadUserItemRatings() {
		userItemRatings = DBConnectionManager.getUserItemRatings();
		for (UserItemRating uItemRating : userItemRatings) {
			users.add(uItemRating.getUserId());
			addToUserItemRatingsHash(uItemRating);
			addToUserItemHash(uItemRating);
			addToItemUserHash(uItemRating);
		}
	}

	private static void loadUserTrustRatings() {
		userTrustRatings = DBConnectionManager.getUserTrustRatings();
		for (UserTrustRating trustRating : userTrustRatings) {
			addToUserTrustRatingsHash(trustRating);
		}

	}

	private static void loadUserBos() {

		for (UserItemRating itemRating : userItemRatings) {
			UserBo userBo = userBoMap.get(itemRating.getUserId());
			if (userBo == null) {
				userBo = new UserBo();
				userBo.setUserId(itemRating.getUserId());

			}
			userBo.addToRatedItems(itemRating.getItemId());
			userBo.addToItemRatingsMap(itemRating);
			userBoMap.put(itemRating.getUserId(), userBo);

		}

		for (UserTrustRating trustRating : userTrustRatings) {
			UserBo userBo = userBoMap.get(trustRating.getUserId());
			if (userBo == null) {
				userBo = new UserBo();
				userBo.setUserId(trustRating.getUserId());
			}
			userBo.addToUserTrustMap(trustRating);
			userBo.addTrustedUser(trustRating.getOtherUserId());
			userBoMap.put(trustRating.getUserId(), userBo);
		}
	}

	private static void addToUserItemRatingsHash(UserItemRating rating) {

		List<UserItemRating> userRatings = userItemRatingsHash.get(rating
				.getUserId());
		if (userRatings == null) {
			userRatings = new ArrayList<UserItemRating>();
		}
		userRatings.add(rating);
		userItemRatingsHash.put(rating.getUserId(), userRatings);

	}

	private static void addToUserTrustRatingsHash(UserTrustRating rating) {

		List<UserTrustRating> userRatings = userTrustRatingsHash.get(rating
				.getUserId());
		if (userRatings == null) {
			userRatings = new ArrayList<UserTrustRating>();
		}
		userRatings.add(rating);
		userTrustRatingsHash.put(rating.getUserId(), userRatings);

	}

	private static void addToUserItemHash(UserItemRating rating) {

		List<Long> items = userItemHash.get(rating.getUserId());
		if (items == null) {
			items = new ArrayList<Long>();
		}
		items.add(rating.getItemId());
		userItemHash.put(rating.getUserId(), items);

	}

	private static void addToItemUserHash(UserItemRating rating) {

		List<Long> users = itemUserHash.get(rating.getItemId());
		if (users == null) {
			users = new ArrayList<Long>();
		}
		users.add(rating.getUserId());
		itemUserHash.put(rating.getItemId(), users);

	}

	private static List<UserItemRating> userItemRatings = null;

	public List<UserItemRating> getUserItemRatings() {
		return userItemRatings;
	}

	public void setUserItemRatings(List<UserItemRating> userItemRatings) {
		EpinionsDataLoader.userItemRatings = userItemRatings;
	}

	public List<UserTrustRating> getUserTrustRatings() {
		return userTrustRatings;
	}

	public void setUserTrustRatings(List<UserTrustRating> userTrustRatings) {
		EpinionsDataLoader.userTrustRatings = userTrustRatings;
	}

	public HashMap<Long, List<UserItemRating>> getUserItemRatingsHash() {
		return userItemRatingsHash;
	}

	public void setUserItemRatingsHash(
			HashMap<Long, List<UserItemRating>> userItemRatingsHash) {
		EpinionsDataLoader.userItemRatingsHash = userItemRatingsHash;
	}

	public Set<Long> getUsers() {
		return users;
	}

	public void setUsers(Set<Long> users) {
		EpinionsDataLoader.users = users;
	}

	public HashMap<Long, List<Long>> getUserItemHash() {
		return userItemHash;
	}

	public void setUserItemHash(HashMap<Long, List<Long>> userItemHash) {
		EpinionsDataLoader.userItemHash = userItemHash;
	}

	public HashMap<Long, List<Long>> getItemUserHash() {
		return itemUserHash;
	}

	public Map<Long, List<UserTrustRating>> getUserTrustRatingsHash() {
		return userTrustRatingsHash;
	}

	public Map<Long, UserBo> getUserBoMap() {
		return userBoMap;
	}
}
