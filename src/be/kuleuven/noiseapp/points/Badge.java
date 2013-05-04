package be.kuleuven.noiseapp.points;

import java.io.Serializable;

public class Badge implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private int point;
	
	public Badge(String name, String description, int point){
		this.name = name;
		this.description = description;
		this.point = point;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getPoint() {
		return point;
	}
}
