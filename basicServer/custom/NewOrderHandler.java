package basicServer.custom;

public interface NewOrderHandler {
	
	
	/**
	 * 
	 * @return true for success false for rejection
	 */
	public boolean newOrder(String customer, String order);
	public String getScheduleAvailability(String cookie);
}
