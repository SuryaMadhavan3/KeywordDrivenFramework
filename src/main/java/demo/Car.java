package demo;

public class Car implements TrafficRules {
	
	@Override
	public void Green() {
		System.out.println("Car - Start");
	}
	
	@Override
	public void Red() {
		System.out.println("Car - Stop");	
	}
	
	@Override
	public void Yellow() {
		System.out.println("Car - Ready");
	}

}
