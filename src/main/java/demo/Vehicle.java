package demo;

public class Vehicle {
	
	public static void main(String[] args) {
		
		TrafficRules t1 = new Car();
		TrafficRules t2 = new Bike();
		
		t1.Green();
		t1.Red();
		t1.Yellow();
		t2.Green();
		t2.Red();
		t2.Yellow();
	}
}