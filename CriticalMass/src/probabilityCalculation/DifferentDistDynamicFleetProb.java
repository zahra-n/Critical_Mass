package probabilityCalculation;

import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import firstExample.ZahraUtility;

public class DifferentDistDynamicFleetProb {
	
	private static class Passenger
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		int interest;
		
		
		public Passenger(int id, Point coordinate, double utility, int neighbour, int interest) {
			super();
			this.id = id;
			this.coordinate = coordinate;
			this.utility = utility;
			this.neighbour = neighbour;
			this.interest = interest;
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Point getCoordinate() {
			return coordinate;
		}
		public void setCoordinate(Point coordinate) {
			this.coordinate = coordinate;
		}
		public double getUtility() {
			return utility;
		}
		public void setUtility(double utility) {
			this.utility = utility;
		}

		public int getNeighbour() {
			return neighbour;
		}

		public void setNeighbour(int neighbour) {
			this.neighbour = neighbour;
		}

		public int getInterest() {
			return interest;
		}

		public void setInterest(int interest) {
			this.interest = interest;
		}

		@Override
		public String toString() {
			return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + neighbour + "," + interest;
		}
				
	}

	private static class Vehicle
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		int capacity;
		
		
		public Vehicle(int id, Point coordinate, double utility, int neighbour, int capacity) {
			super();
			this.id = id;
			this.coordinate = coordinate;
			this.utility = utility;
			this.neighbour = neighbour;
			this.capacity = capacity;
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Point getCoordinate() {
			return coordinate;
		}
		public void setCoordinate(Point coordinate) {
			this.coordinate = coordinate;
		}
		public double getUtility() {
			return utility;
		}
		public void setUtility(double utility) {
			this.utility = utility;
		}

		public int getNeighbour() {
			return neighbour;
		}

		public void setNeighbour(int neighbour) {
			this.neighbour = neighbour;
		}
		
		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		@Override
		public String toString() {
			return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + capacity + "," + neighbour;
		}

//		public double compareTo(Vehicle compareVeh) {
//			// TODO Auto-generated method stub
//			
//			double compareUtil = ((Vehicle) compareVeh) .getUtility();
//			return (this.utility-compareUtil);
//		}
				
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		for (int p = 10 ; p < 21 ; p+=10 )
		{
		for (int v = 510 ; v < 511 ; v+=10)	
		{
		
		int probability = 0; //counting successful instances
		int allIterations = 0; // counting all instances
		double xLimit = 5000; //in meter
		double yLimit = 5000; //in meter
		int passengerNumber = p * 25 ;
		int vehAddedInIteration = v ;
		String passDist = "N";
		String vehDist = "U";
		int area = (int) (xLimit*yLimit/Math.pow(10, 6));
		
		
//		File plog = new File("final\\"+ area + "sqkm\\" + area + "sqkm_probabilities.csv" );
//		FileWriter pFileWriter = new FileWriter(plog, true);
//		BufferedWriter pBufferedWriter = new BufferedWriter(pFileWriter);
//		pBufferedWriter.write("Area_sqKm,Population,Population_distribution,Vehicles_added_each_iteration,Vehicles_distribution,Success_probability\n");
		
		for (int probIteration = 0 ; probIteration < 10 ; probIteration++)
		{
//			String dir = "final\\"+ area + "sqkm\\P" + passDist + passengerNumber + "\\V" + vehDist + vehAddedInIteration + "\\" + probIteration + "\\" ;
			double reachMeasure = 100; // in meter
			double potentialUtil = 5.0;
			double vehUtilThres = 2.0;
			double passUtilThres = 2.0;
			int iterations = 100;
			int vehicleCapacity = 1;
			int passInterestThres = 5;
			int iterationWrite = 50;
			int interestedPassengers = 0;
//			Files.createDirectories(Paths.get(dir));
//			File log = new File(dir + "aggregated-P" + passengerNumber + passDist + "-V" + vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv" );
//			FileWriter fileWriter = new FileWriter(log, false);
//			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//			bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,"
//					+ "% Interested passengers,% Matched passengers,% Matched Vehicles,"
//					+ "Interested passengers,Matched passengers,Matched Vehicles,Total Vehicles\n");
			
			
			//========================================================================================
			
			ArrayList<Passenger> passengers = new ArrayList <Passenger>();
			ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
			
			//generating the population with a normal distribution
			for (int i = 0 ; i < passengerNumber; i++ )
			{
				Random rnd = new Random();
				Point passengerCoord = new Point();
				passengerCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
				Passenger tempPassenger = new Passenger (i, passengerCoord, 0.0, 0, 0);
				passengers.add(tempPassenger);
			}
			
	
			//iterations
			for (int k = 1 ; k <= iterations ; k++)
			{
//				System.out.println("Iteration: " + k);
				
				interestedPassengers = 0;
				double passengerUtilSum = 0.0;
				double vehicleUtilSum = 0.0;
				int matchedPassengers = 0 ;
				int unmatchedVehicles = 0;
				
				//generating the vehicles to add in this iteration with a uniform distribution
				int startingID = -1; // this is necessary to have unique id for vehicles through all iterations
				if (vehicles.size() > 0)
					startingID = vehicles.get(vehicles.size()- 1).id;
	
				for (int i = startingID + 1 ; i <= startingID + vehAddedInIteration ; i++ )
				{
					Random rnd = new Random();
					Point vehicleCoord = new Point();
					vehicleCoord.setLocation((rnd.nextDouble() * xLimit), (rnd.nextDouble() * yLimit));
					Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
					vehicles.add(tempVehicle);
				}
				
				
	//			System.out.println("passengers:" + passengers.size());
	//			System.out.println("vehicles:" + vehicles.size());
				
				
				//Neighbours: counting the number of available vehicles around each passenger
				for (int i = 0 ; i < passengers.size() ; i++ )
				{
					passengers.get(i).setNeighbour(0);
					Point checkPoint = passengers.get(i).coordinate;
					for (int j = 0 ; j < vehicles.size() ; j++ )
					{
						double distance = checkPoint.distance( vehicles.get(j).coordinate);
						if (distance < reachMeasure)//) && vehicles.get(j).getCapacity() > 0)
						{
							passengers.get(i).neighbour++;
						}
					}
//					System.out.println("Pass" + passengers.get(i).id + ": " + passengers.get(i).neighbour);
				}
				
				//counting interested passengers
				for (int i = 0 ; i < passengers.size() ; i++)
				{
					if (passengers.get(i).neighbour > passInterestThres || passengers.get(i).utility > passUtilThres )
					{
						passengers.get(i).setInterest(1);
						interestedPassengers++;
					}
					else
						passengers.get(i).setInterest(0);
				}
				System.out.println("interested Passengers: " + interestedPassengers);
				
				
				//Neighbours: counting the number of interested passenger around each vehicles 
				for (int i = 0 ; i < vehicles.size() ; i ++)
				{
					vehicles.get(i).setCapacity(vehicleCapacity);
					vehicles.get(i).setNeighbour(0);
					vehicles.get(i).setUtility(0.0);
					Point checkPoint = vehicles.get(i).coordinate;
					for (int j = 0 ; j < passengers.size() ; j ++)
					{
						double distance = checkPoint.distance( passengers.get(j).coordinate);
						if (passengers.get(j).interest == 1 && distance < reachMeasure)
						{
								vehicles.get(i).neighbour++;
						}
					}
				}
					
			
				///=================================================================================================================
				
				//		calculating utility according to distance
				for (int i = 0 ; i < passengers.size() ; i++ )
				{
					passengers.get(i).setUtility(0.0);
					if (passengers.get(i).getInterest() == 1)
					{
						double finalDist = xLimit;
						int matchedVehID = -1;
						Point checkPoint = passengers.get(i).coordinate;
		
						for (int j = 0 ; j < vehicles.size() ; j++ )
						{
							double distance = checkPoint.distance( vehicles.get(j).coordinate);
							if (distance < reachMeasure)
							{
								double vehUtil = (reachMeasure - distance) / reachMeasure * potentialUtil;
								if (distance < finalDist && vehicles.get(j).capacity > 0 )
								{
									vehicles.get(j).setUtility(vehUtil);
									matchedVehID = vehicles.get(j).id;
									finalDist = distance;
								}
								
							}// end of checking distance
							
						}// end of vehicle loop
						
						if (matchedVehID > -1 ) //it means that a vehicles has been assigned to passenger i, so the matchedVehID has been changed from -1 
						{
							double passUtil = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
							passengers.get(i).setUtility(passUtil);
							vehicles.get(matchedVehID).capacity-- ;
								matchedPassengers++;
						}
						
		//				System.out.println("passenger: " + i + " ,matched vehicle: " + matchedVehID);
					}
					
	//				//passengers with utility lower than a certain threshold loose interest
	//				if (passengers.get(i).utility < passUtilThres)
	//					passengers.get(i).setInterest(0);
		
				}// end of passenger loop
				
				for (int i = 0 ; i < vehicles.size() ; i++)
				{
					if (vehicles.get(i).capacity == vehicleCapacity)
					{
						vehicles.get(i).utility = 0.0;
						unmatchedVehicles++;
					}
				}
				
				
				
				
				//====================calculating the mean utility
				
				for (int i = 0 ; i < passengers.size() ; i++)
				{
					passengerUtilSum += passengers.get(i).utility;
				}
				double meanPassengersUtil = passengerUtilSum / passengers.size();
				
				for (int i = 0 ; i < vehicles.size() ; i++)
				{
					vehicleUtilSum += vehicles.get(i).utility;
				}
				double meanVehUtil = vehicleUtilSum / (vehicles.size() );
				
				double matchedPassPercent = (double) matchedPassengers/passengers.size() * 100;
				double matchedVehPercent = (double) (vehicles.size() - unmatchedVehicles)/vehicles.size() * 100;
			
				
	//			System.out.println("Passengers' mean utility = " + meanPassengersUtil);
	//			System.out.println("matched passengers = " + matchedPassPercent + "%" );
	//			System.out.println("Vehicles' mean utility = " + meanVehUtil);
	//			System.out.println("matched vehicles = " + matchedVehPercent + "%");
				
//				if (k % iterationWrite == 1000 )
//				{
//					StringBuilder fileContentP = new StringBuilder();
//					StringBuilder fileContentV = new StringBuilder();
//					
//					fileContentP.append("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
//					for (int i = 0 ; i < passengers.size(); ++i)
//						fileContentP.append(k + "," + passengers.get(i).toString() + "\n");
//					
//					fileContentV.append("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
//					for (int i = 0 ; i < vehicles.size(); ++i)
//						fileContentV.append(k + "," +vehicles.get(i).toString() + "\n");
					
//					ZahraUtility.write2File(fileContentP.toString(), dir + k + "-passengers-P" + passengerNumber + passDist 
//							+ "-V" + vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//					ZahraUtility.write2File(fileContentV.toString(), dir + k + "-vehicles-P" + passengerNumber + passDist + "-V" 
//							+ vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//				}
				
//				bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," 
//						+ (double)interestedPassengers/passengers.size() * 100 + "%," 
//						+  matchedPassPercent + "%," + matchedVehPercent + "%," + interestedPassengers 
//						+ "," + matchedPassengers + "," + (vehicles.size() - unmatchedVehicles) + "," 
//						+ vehicles.size() + "\n");//fileContentAggregated.toString());
			
				//===============reseting the available numbers================
	
	//				if (meanVehUtil >= vehUtilThres)
	//					vehicleNumber *= 1.1;
	//				else
	//					vehicleNumber *= 0.9;
	//				{
	//				if (meanPassengersUtil >= passUtilThres )
	//					passengerNumber *= 1.1;
	//				else
	//					passengerNumber *= 0.9;
	//				}
				    
				    
				    //vehicles with utility lower than a certain threshold leave
				    for (int i = 0 ; i < vehicles.size(); i ++)
				    {
				    	if (vehicles.get(i).utility < vehUtilThres)
				    	{
				    		vehicles.remove(i);
				    		i--;
				    	}
				    }
				    
				    //resetting veh id
				    for (int i = 0 ; i < vehicles.size(); i ++)
				    {
				    	vehicles.get(i).setId(i);
				    } 
				    
							
			}// end of iterations
//			System.out.println(interestedPassengers);
			
			if (interestedPassengers > 0)
		    {
		    	probability++;
//		    	System.out.println("p:"+ probability);
		    }
			allIterations++;
//			System.out.println("total:" + allIterations);
//		    bufferedWriter.write(probability);
//			System.out.println(probIteration);
			
//			bufferedWriter.close();
		    
		    
		}//end of probability iteration
		double successProb = (double) probability/allIterations * 100; 
		System.out.println(p + " passengers and " + v + " vehicles: " + successProb + "%");
//		pBufferedWriter.write(area + "," + passengerNumber + "," + passDist + "," + vehAddedInIteration + "," + vehDist + "," + successProb + "\n");
//		pBufferedWriter.close();
//		
		System.out.println("DONE");
		Toolkit.getDefaultToolkit().beep();
	}
		
		}}
}
