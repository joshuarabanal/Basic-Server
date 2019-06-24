package basicServer;

import java.io.File;

public interface Scheduler {

	/**
	 * 
	 * @param dateTime_start
	 * @param dateTime_end
	 * @param cookie
	 * @param data contents of the file itself
	 * @return a file to the newly created appointment
	 */
	public File addAppointment(String dateTime_start, String dateTime_end, String cookie, String... data);
	public boolean appointmentIsAvailable(String cookie, String dateTime_start, String dateTime_end );
}
