package be.kuleuven.noiseapp.auth;



public class UserProfile {
	  private long userID;
	  private long firstName;
	  private long lastName;
	  private long points;
	  
	  
	  public long getUserID() {
	    return userID;
	  }

	  public void setUserID(long id) {
	    this.userID = id;
	  }
	
	@Override
	public String toString(){
		return "ID: " + getUserID() + "\nFirst name: " + getFirstName() + "\nLast name: " + getLastName() + "\nPoints: " + getPoints();
	}

	/**
	 * @return the firstName
	 */
	public long getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(long firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public long getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(long lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the points
	 */
	public long getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(long points) {
		this.points = points;
	}
}
