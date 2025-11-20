package demo;

public class Bike implements TrafficRules{

	@Override
	public void Green() {
		System.out.println("Bike - Start");
	}
	
	@Override
	public void Red() {
		System.out.println("Bike - Stop");
	}
	
	@Override
	public void Yellow() {
		System.out.println("Bike - Ready");
	}
	
}
