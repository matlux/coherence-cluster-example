package net.matlux.coherence.flooder;

//import gov.lanl.utility.ISODate;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class PerformanceTimer {
	static public String dateFormatXMLPattern = "yyyy-MM-dd'T'H:mm:ss.SSS Z";	


	private long startTime; // start clock cycle        

	private Vector<Long> taskStartClocks = new Vector<Long>(); //long
	private Vector<Double> taskRoundTripTimes = new Vector<Double>(); //double
	private Vector<Double> taskExecutionTimes = new Vector<Double>(); //double
	private Vector<Double> taskSubmissionPeriods = new Vector<Double>(); //double
	private Vector<Double> taskNetworkOverheads = new Vector<Double>(); //double
	private Vector<Properties> engineHostsStats = new Vector<Properties>();//Properties
	private Vector<String> engineHostNames = new Vector<String>(); //String
	private Vector<Date> taskRoundTripTimesStartTime = new Vector<Date>(); //Date
	private Vector<Date> taskRoundTripTimesStopTime = new Vector<Date>(); //Date

	private long previousStartTime; // clock cycle 
	private long nextNewStartTime; // clock cycle 
	private long endTime; // clock cycle 
	private double avgRate=-1; // per seconds
	private double avgPeriod=-1; // milleseconds
	private double instantRate=-1; // per seconds
	private double instantPeriod=-1; // milleseconds
	private double totalPeriod=-1; // milleseconds
	private long tickNumber=0;
	private String m_ssid;
	private Date startDate;

	public PerformanceTimer() {
		startTimer();
	}
	
	void setSsid(String myssid) {
		m_ssid=myssid;

	}
	public void startTimer() {
		startDate = new Date();
		nextNewStartTime = endTime = startTime = startDate.getTime(); // start clock cycle        


		taskRoundTripTimesStartTime.add(startDate);
		taskRoundTripTimesStopTime.add(null);

		taskStartClocks.add(new Long(startTime));
		
		avgRate=-1;
		avgPeriod=-1;
		instantRate=-1;
		instantPeriod=-1;
		//endTime=new Date().getTime(); // end clock cycle
	}

	public void Tack(int taskId, double taskDurationOnEngine, String engine, Properties stats) throws Exception{
		Date tackDate = new Date();
		
		if(taskId > (taskStartClocks.size() - 1)) throw new Exception("tack() cannot handle taskId out of bound. Missing tick().");
		
		taskRoundTripTimesStopTime.set(taskId,tackDate);

		long taskEnd = tackDate.getTime();

		
		long taskStart = ((Long)taskStartClocks.get(taskId)).longValue();
		
		double taskRoundTripTime = (double)(taskEnd - taskStart); // / clocks_per_milli;
		taskRoundTripTimes.set(taskId,new Double(taskRoundTripTime));
		taskExecutionTimes.set(taskId,new Double(taskDurationOnEngine));
		taskNetworkOverheads.set(taskId,new Double(taskRoundTripTime - taskDurationOnEngine));
		engineHostNames.set(taskId,engine);
		engineHostsStats.set(taskId,stats);
		
	}
	public void Tick() {

		Date tickDate = new Date();
		tickNumber++;
		previousStartTime=nextNewStartTime; // previous one at this point

		endTime = nextNewStartTime=tickDate.getTime(); // start clock cycle
		taskStartClocks.add(new Long(endTime));
		
		totalPeriod = (double)(endTime - startTime);

		avgPeriod = ((double)(endTime - startTime))/((double)tickNumber);
		if (avgPeriod==0) {
			avgRate=Long.MAX_VALUE;
		} else {
			avgRate = 1000/(avgPeriod);
		}

		instantPeriod = ((double)(endTime - previousStartTime));
		if (instantPeriod==0) {
			instantRate=Long.MAX_VALUE;
		} else {
			instantRate = 1000/(instantPeriod);
		}
		
		taskSubmissionPeriods.add(new Double(instantPeriod));
		
		taskRoundTripTimes.add(new Double(-1.0));
		taskExecutionTimes.add(new Double(-1.0));
		taskNetworkOverheads.add(new Double(-1.0));
		engineHostsStats.add(new Properties());
		engineHostNames.add(null);


		taskRoundTripTimesStartTime.add(tickDate);
		taskRoundTripTimesStopTime.add(null);
		
		
	}


	public void printHumanReadableStats() {
		if (this.getAvgPeriod()==0) {
			System.out.println(" AVG submission rate=+inf, submission period=" + this.getAvgPeriod() + " millisec");
		} else {
			System.out.println(" AVG submission rate=" + this.getAvgRate() + "/sec, submission period=" + this.getAvgPeriod() + " millisec");
		}
		if (this.getInstantPeriod()==0) {
			System.out.println(" Instant submission rate=+inf, submission period=" + this.getInstantPeriod() + " millisec");
		} else {
			System.out.println(" Instant submission rate=" + this.getInstantRate() + "/sec, submission period=" + this.getInstantPeriod() + " millisec");
		}
	}
	public void getServiceExcelStats(PrintWriter myOutPrintWriter) {

		
		myOutPrintWriter.append("Total_sub_time_(ms)=" 
					+ ( ((Long)taskStartClocks.get((int)tickNumber)).longValue() - ((Long)taskStartClocks.get(0)).longValue() ) + "\t");
		//s1 << "AVGsub(ms)=" << getAvgPeriod() << "\t";

		double taskSubmissionPeriodAVG=0;
		double taskRoundTripTimeAVG=0;
		double taskExecutionTimeAVG=0;
		double noAVG=0;

		int numberOfTasks = taskSubmissionPeriods.size();

		for (int i=0; i<numberOfTasks;i++) {

			taskSubmissionPeriodAVG += ((Double)taskSubmissionPeriods.get(i)).doubleValue();
			taskRoundTripTimeAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue();
			taskExecutionTimeAVG += ((Double)taskExecutionTimes.get(i)).doubleValue();
			noAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue();


		}

		taskSubmissionPeriodAVG = taskSubmissionPeriodAVG / numberOfTasks;
		taskRoundTripTimeAVG = taskRoundTripTimeAVG / numberOfTasks;
		taskExecutionTimeAVG = taskExecutionTimeAVG / numberOfTasks;
		noAVG = noAVG / numberOfTasks;

		myOutPrintWriter.append("AVGsub(ms)=" + taskSubmissionPeriodAVG + "\t");
		myOutPrintWriter.append("AVGsubRate=" + (1000/taskSubmissionPeriodAVG) + "\t");
		myOutPrintWriter.append("AVG_RT(ms)=" + taskRoundTripTimeAVG + "\t");
		myOutPrintWriter.append("AVGexec_time(ms)=" + taskExecutionTimeAVG + "\t");
		myOutPrintWriter.append("NO(ms)=" + noAVG + "\t");
		


		
	}
	
	
	
	
	public void getTaskExcelStats(PrintWriter myOutPrintWriter)
	{




		myOutPrintWriter.append("Task#\tSSID\t");
		myOutPrintWriter.append("taskRoundTripTimesStartTime\t");
		myOutPrintWriter.append("taskRoundTripTimesStartTimeMs\t");
		myOutPrintWriter.append("EngineHost\t");
		myOutPrintWriter.append("submission(ms)\t");
		myOutPrintWriter.append("RT(ms)\t");
		myOutPrintWriter.append("execTime(ms)\t");
		myOutPrintWriter.append("NO+schedul.t(ms)\t");
		myOutPrintWriter.append("tag@qazwsxNewTaskHeader" + "\n");
			
		for (int i=0; i<taskSubmissionPeriods.size();i++) {
			myOutPrintWriter.append(i + "\t");
			myOutPrintWriter.append(m_ssid + "\t");


			//stringSplit(taskRoundTripTimesStartTime[i],".",vecResponse);
			Date myDate = (Date)taskRoundTripTimesStartTime.get(i);
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(dateFormatXMLPattern);
	        String time = formatter.format(myDate);

			

			
			

			myOutPrintWriter.append(time + "\t");
			myOutPrintWriter.append(ISODate.Date2TimeMillis(myDate) + "\t");
			
			//s1 + taskRoundTripTimesStopTime[i] + "\t");
			myOutPrintWriter.append((String)engineHostNames.get(i) + "\t");
			myOutPrintWriter.append(((Double)taskSubmissionPeriods.get(i)).doubleValue() + "\t");
			myOutPrintWriter.append(((Double)taskRoundTripTimes.get(i)).doubleValue() + "\t");
			myOutPrintWriter.append(((Double)taskExecutionTimes.get(i)).doubleValue() + "\t");
			myOutPrintWriter.append(((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue() + "\t");
			
			Properties myProps = (Properties)engineHostsStats.get(i);
			Object myObj = myProps.keys();
			Enumeration myKeys = (Enumeration)myObj;
			for (int j=0; myKeys.hasMoreElements() ; j++) {
				String myKey=(String)myKeys.nextElement();
				myOutPrintWriter.append(myKey + "\t" + myProps.get(myKey) + "\t");
			}
			myOutPrintWriter.append("tag@qazwsxNewTaskInfo" + "\n");
		}


	}






	public void getServiceXmlStats(PrintWriter myOutPrintWriter)
	{



		myOutPrintWriter.append("Total_sub_time=\"" + ( ((Long)taskStartClocks.get((int)tickNumber)).longValue() - ((Long)taskStartClocks.get(0)).longValue() ) + "\"\t");

		double taskSubmissionPeriodAVG=0;
		double taskRoundTripTimeAVG=0;
		double taskExecutionTimeAVG=0;
		double noAVG=0;

		int numberOfTasks = taskSubmissionPeriods.size();

		for (int i=0; i<numberOfTasks;i++) {

			taskSubmissionPeriodAVG += ((Double)taskSubmissionPeriods.get(i)).doubleValue();
			taskRoundTripTimeAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue();
			taskExecutionTimeAVG += ((Double)taskExecutionTimes.get(i)).doubleValue();
			noAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue();


		}

		taskSubmissionPeriodAVG = taskSubmissionPeriodAVG / numberOfTasks;
		taskRoundTripTimeAVG = taskRoundTripTimeAVG / numberOfTasks;
		taskExecutionTimeAVG = taskExecutionTimeAVG / numberOfTasks;
		noAVG = noAVG / numberOfTasks;

		myOutPrintWriter.append("AVGsub=\"" + taskSubmissionPeriodAVG + "\"\t");
		myOutPrintWriter.append("AVGsubRate=\"" + (1000/taskSubmissionPeriodAVG) + "\"\t");
		myOutPrintWriter.append("AVG_RT=\"" + taskRoundTripTimeAVG + "\"\t");
		myOutPrintWriter.append("AVGexec_time=\"" + taskExecutionTimeAVG + "\"\t");
		myOutPrintWriter.append("NO=\"" + noAVG + "\"\t");
		

		
	}
	public void getTaskXmlStats(PrintWriter myOutPrintWriter)
	{




			
		for (int i=0; i<taskSubmissionPeriods.size();i++) {
			myOutPrintWriter.append("\t\t\t<task ");
			myOutPrintWriter.append("id=\"" + i + "\"\t");
			Date myDate = (Date)taskRoundTripTimesStartTime.get(i);

			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(dateFormatXMLPattern);
	        String time = formatter.format(myDate);

			myOutPrintWriter.append("taskRoundTripTimesStartTime=\"" + time + "\"\t");
			myOutPrintWriter.append("taskRoundTripTimesStartTimeMs=\"" + ISODate.Date2TimeMillis(myDate) + "\"\t");
			myOutPrintWriter.append("EngineHost=\"" + (String)engineHostNames.get(i) + "\"\t");
			myOutPrintWriter.append("submissionTime=\"" + ((Double)taskSubmissionPeriods.get(i)).doubleValue() + "\"\t");
			myOutPrintWriter.append("RoundTrip=\"" + ((Double)taskRoundTripTimes.get(i)).doubleValue() + "\"\t");
			myOutPrintWriter.append("execTime=\"" + ((Double)taskExecutionTimes.get(i)).doubleValue() + "\"\t");
			myOutPrintWriter.append("NO=\"" + (((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue()) + "\"\t");


			//Enumeration myKeys = (Enumeration)((Properties)engineHostsStats.get(i)).keys();
			Properties myProps = (Properties)engineHostsStats.get(i);
			Object myObj = myProps.keys();
			Enumeration myKeys = (Enumeration)myObj;
			for (int j=0; myKeys.hasMoreElements() ; j++) {
				String myKey=(String)myKeys.nextElement();
				myOutPrintWriter.append(myKey + "\"" + myProps.get(myKey) + "\"\t");
			}
			/*for (int j=0; myKeys.hasMoreTokens(); j++) {
				String myKey=myKeys.nextToken();
				s1 += myKey + "\t" + ((Properties)engineHostsStats.get(i)).get(myKey) + "\t";
			}*/
			myOutPrintWriter.append(">");
			myOutPrintWriter.append("\n");
		}


	}
	// *******************************************************************
	// *******************************************************************
	public String getServiceXmlStats()
	{

		String s1;

		s1 = "Total_sub_time=\"" + ( ((Long)taskStartClocks.get((int)tickNumber)).longValue() - ((Long)taskStartClocks.get(0)).longValue() ) + "\"\t";

		double taskSubmissionPeriodAVG=0;
		double taskRoundTripTimeAVG=0;
		double taskExecutionTimeAVG=0;
		double noAVG=0;

		int numberOfTasks = taskSubmissionPeriods.size();

		for (int i=0; i<numberOfTasks;i++) {

			taskSubmissionPeriodAVG += ((Double)taskSubmissionPeriods.get(i)).doubleValue();
			taskRoundTripTimeAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue();
			taskExecutionTimeAVG += ((Double)taskExecutionTimes.get(i)).doubleValue();
			noAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue();


		}

		taskSubmissionPeriodAVG = taskSubmissionPeriodAVG / numberOfTasks;
		taskRoundTripTimeAVG = taskRoundTripTimeAVG / numberOfTasks;
		taskExecutionTimeAVG = taskExecutionTimeAVG / numberOfTasks;
		noAVG = noAVG / numberOfTasks;

		s1 += "AVGsub=\"" + taskSubmissionPeriodAVG + "\"\t";
		s1 += "AVGsubRate=\"" + (1000/taskSubmissionPeriodAVG) + "\"\t";
		s1 += "AVG_RT=\"" + taskRoundTripTimeAVG + "\"\t";
		s1 += "AVGexec_time=\"" + taskExecutionTimeAVG + "\"\t";
		s1 += "NO=\"" + noAVG + "\"\t";
		

		return s1;
	}
	public String getTaskXmlStats()
	{

		String s1 = new String();


			
		for (int i=0; i<taskSubmissionPeriods.size();i++) {
			s1 += "\t\t\t<task ";
			s1 += "id=\"" + i + "\"\t";
			Date myDate = (Date)taskRoundTripTimesStartTime.get(i);

			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(dateFormatXMLPattern);
	        String time = formatter.format(myDate);

			s1 += "taskRoundTripTimesStartTime=\"" + time + "\"\t";
			s1 += "EngineHost=\"" + (String)engineHostNames.get(i) + "\"\t";
			s1 += "submissionTime=\"" + ((Double)taskSubmissionPeriods.get(i)).doubleValue() + "\"\t";
			s1 += "RoundTrip=\"" + ((Double)taskRoundTripTimes.get(i)).doubleValue() + "\"\t";
			s1 += "execTime=\"" + ((Double)taskExecutionTimes.get(i)).doubleValue() + "\"\t";
			s1 += "NO=\"" + (((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue()) + "\"\t";


			//Enumeration myKeys = (Enumeration)((Properties)engineHostsStats.get(i)).keys();
			Properties myProps = (Properties)engineHostsStats.get(i);
			Object myObj = myProps.keys();
			Enumeration myKeys = (Enumeration)myObj;
			for (int j=0; myKeys.hasMoreElements() ; j++) {
				String myKey=(String)myKeys.nextElement();
				s1 += myKey + "\"" + myProps.get(myKey) + "\"\t";
			}
			/*for (int j=0; myKeys.hasMoreTokens(); j++) {
				String myKey=myKeys.nextToken();
				s1 += myKey + "\t" + ((Properties)engineHostsStats.get(i)).get(myKey) + "\t";
			}*/
			s1 += ">";
			s1 += "\n";
		}

		return s1;
	}
	public String getServiceExcelStats() {

		
		String s1 = "Total sub time (ms)=" 
					+ ( ((Long)taskStartClocks.get((int)tickNumber)).longValue() - ((Long)taskStartClocks.get(0)).longValue() ) + "\t";
		//s1 << "AVGsub(ms)=" << getAvgPeriod() << "\t";

		double taskSubmissionPeriodAVG=0;
		double taskRoundTripTimeAVG=0;
		double taskExecutionTimeAVG=0;
		double noAVG=0;

		int numberOfTasks = taskSubmissionPeriods.size();

		for (int i=0; i<numberOfTasks;i++) {

			taskSubmissionPeriodAVG += ((Double)taskSubmissionPeriods.get(i)).doubleValue();
			taskRoundTripTimeAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue();
			taskExecutionTimeAVG += ((Double)taskExecutionTimes.get(i)).doubleValue();
			noAVG += ((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue();


		}

		taskSubmissionPeriodAVG = taskSubmissionPeriodAVG / numberOfTasks;
		taskRoundTripTimeAVG = taskRoundTripTimeAVG / numberOfTasks;
		taskExecutionTimeAVG = taskExecutionTimeAVG / numberOfTasks;
		noAVG = noAVG / numberOfTasks;

		s1 += "AVGsub(ms)=" + taskSubmissionPeriodAVG + "\t";
		s1 += "AVGsubRate=" + (1000/taskSubmissionPeriodAVG) + "\t";
		s1 += "AVG_RT(ms)=" + taskRoundTripTimeAVG + "\t";
		s1 += "AVGexec_time(ms)=" + taskExecutionTimeAVG + "\t";
		s1 += "NO(ms)=" + noAVG + "\t";
		

		return s1;
		
	}
	
	
	
	
	public String getTaskExcelStats()
	{

		String s1;


		s1 = "Task#\tSSID\t";
		s1 += "taskRoundTripTimesStartTime\t";
		s1 += "Milliseconds\t";
		s1 += "EngineHost\t";
		s1 += "submission(ms)\t";
		s1 += "RT(ms)\t";
		s1 += "execTime(ms)\t";
		s1 += "NO+schedul.t(ms)\t";
		s1 += "tag@qazwsxNewTaskHeader" + "\n";
			
		for (int i=0; i<taskSubmissionPeriods.size();i++) {
			s1 += i + "\t";
			s1 += m_ssid + "\t";


			//stringSplit(taskRoundTripTimesStartTime[i],".",vecResponse);
			Date myDate = (Date)taskRoundTripTimesStartTime.get(i);
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(dateFormatXMLPattern);
	        String time = formatter.format(myDate);

			

			
			

			s1 += time + "\t";
			//s1 + taskRoundTripTimesStopTime[i] + "\t";
			s1 += (String)engineHostNames.get(i) + "\t";
			s1 += ((Double)taskSubmissionPeriods.get(i)).doubleValue() + "\t";
			s1 += ((Double)taskRoundTripTimes.get(i)).doubleValue() + "\t";
			s1 += ((Double)taskExecutionTimes.get(i)).doubleValue() + "\t";
			s1 += ((Double)taskRoundTripTimes.get(i)).doubleValue() - ((Double)taskExecutionTimes.get(i)).doubleValue() + "\t";
			
			Properties myProps = (Properties)engineHostsStats.get(i);
			Object myObj = myProps.keys();
			Enumeration myKeys = (Enumeration)myObj;
			for (int j=0; myKeys.hasMoreElements() ; j++) {
				String myKey=(String)myKeys.nextElement();
				s1 += myKey + "\t" + myProps.get(myKey) + "\t";
			}
			s1 += "tag@qazwsxNewTaskInfo" + "\n";
		}

		return s1;
	}
	
	public double getAvgPeriod() {
		return avgPeriod;
	}
	public double getAvgRate() {
		return avgRate;
	}
	public double getEndTime() {
		return endTime;
	}
	public double getInstantPeriod() {
		return instantPeriod;
	}
	public double getInstantRate() {
		return instantRate;
	}
	public double getTotalPeriod() {
		return totalPeriod;
	}
	public long getPreviousStartTime() {
		return previousStartTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setSSID(String m_ssid) {
		this.m_ssid = m_ssid;
	}
	public Date getStartDate() {
		return startDate;
	}



}
