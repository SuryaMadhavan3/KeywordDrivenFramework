package demo;

public class Vehicle {
	
	static void engine() {
		System.out.println("Engine Starts Succesfully");
	}
	
	Car c = new Car();
	public static void main(String[] args) {
		Car c = new Car();
		// System.out.println("Static Method is running.");
		c.maruthi();//Cannot make a static reference to the non-static field c
	}
}