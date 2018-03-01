package realWorldScenarios;
/*
 * in this class the passenger loop happens only once and everything is calculate there
 */
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.opengis.feature.simple.SimpleFeature;

import firstExample.ZahraUtility;
import population.RandomPopulation;

public class YarraRanges10percentpop {
	
	public static class Passenger
	{
		int id;
		public Point coordinate;
		public double utility;
		public int neighbour;
		public int interest;
		public int mtcheVehID;
		
		
		public Passenger(int id, Point coordinate, double utility, int neighbour, int interest, int mtcheVehID) {
			super();
			this.id = id;
			this.coordinate = coordinate;
			this.utility = utility;
			this.neighbour = neighbour;
			this.interest = interest;
			this.mtcheVehID = mtcheVehID;
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
		
		public int getMtcheVehID() {
			return mtcheVehID;
		}

		public void setMtcheVehID(int mtcheVehID) {
			this.mtcheVehID = mtcheVehID;
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
				
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int probability = 0; //counting successful instances
		int allIterations = 0; // counting all instances
		int vehAddedInIteration = 20000;
		String vehDist = "U";
		String mainFolder = "YarraRanges";
		
		
		File plog = new File(mainFolder + "\\YarraRanges_probabilities.csv" );
		FileWriter pFileWriter = new FileWriter(plog, true);
		BufferedWriter pBufferedWriter = new BufferedWriter(pFileWriter);
		pBufferedWriter.write("Area_sqKm,Population,Population_distribution,Vehicles_added_each_iteration,Vehicles_distribution,Success_probability%\n");
		
		for (int probIteration = 1 ; probIteration <= 10 ; probIteration++)
		{
			String dir = mainFolder + "\\V" + vehDist + vehAddedInIteration + "\\" + probIteration + "\\" ;
			double reachMeasure = 100; // in meter
			double potentialUtil = 5.0;
			double vehUtilThres = 2.0;
			double passUtilThres = 2.0;
			int iterations = 100;
			int iterationWrite = 50;
			int vehicleCapacity = 1;
			int passInterestThres = 5;
			int interestedPassengers = 0;
			Files.createDirectories(Paths.get(dir));
			File log = new File(dir + "aggregated-YarraRanges" + "-V" + vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv" );
			FileWriter fileWriter = new FileWriter(log, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,"
					+ "% Interested passengers,% Matched passengers,% Matched Vehicles,"
					+ "Interested passengers,Matched passengers,Matched Vehicles,Total Vehicles\n");
			
			
			//========================================================================================
			
			ArrayList<Passenger> passengers = new ArrayList <Passenger>();
			ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
			
			//generating the population and reading their locations from the created file in RandomPopulation
			String [][] population = ZahraUtility.Data(22035, 3 , "G:\\My Drive\\1-PhDProject\\CriticalMass\\GM-MB\\YarraRangesPop10%.csv");
			for (int i = 1 ; i < population.length ; i++)
			{
				Point passengerCoord = new Point();
				passengerCoord.setLocation(Double.parseDouble(population[i][1]), Double.parseDouble(population[i][2]));
//				ArrayList<MatchedVehicle> vehIDsToCheck = new ArrayList <MatchedVehicle>();
				Passenger tempPassenger = new Passenger (i, passengerCoord, 0.0, 0, 0, -1);
				passengers.add(tempPassenger);
			}
			
	
			//iterations
			for (int k = 1 ; k <= iterations ; k++)
			{
				System.out.println("It." + k);
				
				interestedPassengers = 0;
				double passengerUtilSum = 0.0;
				double vehicleUtilSum = 0.0;
				int matchedPassengers = 0 ;
				int unmatchedVehicles = 0;
				
				//======================= required inputs for vehicle generation ===========================
				
				Random rndOD = new Random();
				CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84,"EPSG:28355");
				String sa3Name = "Yarra Ranges";
				String zonesFile = "G:\\My Drive\\1-PhDProject\\CriticalMass\\GM-MB\\GreaterMelbourne_SA3.shp";
				File dataFile = new File(zonesFile);
		        dataFile.setReadOnly();
				FileDataStore store = FileDataStoreFinder.getDataStore(dataFile);;
				SimpleFeatureSource source = store.getFeatureSource();
				Map<String,SimpleFeature> featureMap = new LinkedHashMap<>();
				{
					//Iterator to iterate over the features from the shape file
					try ( SimpleFeatureIterator it = source.getFeatures().features() )
					{
						while (it.hasNext())
						{
							// get feature
							SimpleFeature ft = it.next(); //A feature contains a geometry (in this case a polygon) and an arbitrary number
							featureMap.put((String) ft.getAttribute("SA3_NAME16") , ft ) ;
						}
						it.close();
						store.dispose();
					}
					catch ( Exception ee )
					{
						throw new RuntimeException(ee) ;
					}
				}
				//============================================================================================
				
				//generating the vehicles to add in this iteration with a uniform distribution
				int startingID = -1; // this is necessary to have unique id for vehicles through all iterations
				if (vehicles.size() > 0)
					startingID = vehicles.get(vehicles.size()- 1).id;
	
				for (int i = startingID + 1 ; i <= startingID + vehAddedInIteration ; i++ )
				{
//					Random rnd = new Random();
					Point vehicleCoord = new Point();
					Coord originCoord = RandomPopulation.createRandomCoordinateInCcdZone(rndOD,featureMap ,sa3Name, ct);
					vehicleCoord.setLocation(originCoord.getX(), originCoord.getY());//(rnd.nextDouble() * xLimit) + xStartLimit, (rnd.nextDouble() * yLimit) + yStartLimit);
					Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
					vehicles.add(tempVehicle);
				}
				
				
				//Neighbours: counting the number of available vehicles around each passenger
				for (int i = 0 ; i < passengers.size() ; i++ )
				{
					
					passengers.get(i).setMtcheVehID(-1);
					passengers.get(i).setNeighbour(0);
					
					Point passengerCoord = passengers.get(i).coordinate;
					double finalDist = reachMeasure + 10;
					
					for (int j = 0 ; j < vehicles.size() ; j++ )
					{
						
						Point vehicleCoord = vehicles.get(j).coordinate;
						
						if (Math.abs(passengerCoord.getX() - vehicleCoord.getX()) <= reachMeasure && Math.abs(passengerCoord.getY() - vehicleCoord.getY()) <= reachMeasure )
						{
							double distance = passengerCoord.distance( vehicleCoord );
							
							if (distance < reachMeasure)
							{
								passengers.get(i).neighbour++;
								
								if (distance < finalDist && vehicles.get(j).capacity > 0)
								{
									finalDist = distance;
									passengers.get(i).setMtcheVehID(j);	
								}
							}
						}
					}// end of vehicle loop
					
					if (passengers.get(i).neighbour > passInterestThres || passengers.get(i).utility > passUtilThres )
					{
						passengers.get(i).setInterest(1);
						interestedPassengers++;
					}
					else
						passengers.get(i).setInterest(0);
					
					//here the utility is reset to 0 because the interest based on the previous example is already calculated 
					passengers.get(i).setUtility(0.0);
					
					if (passengers.get(i).getInterest() == 1 && passengers.get(i).mtcheVehID > -1 )
					{					
						double util = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
						passengers.get(i).setUtility(util);
						vehicles.get(passengers.get(i).mtcheVehID).setUtility(util);
						vehicles.get(passengers.get(i).mtcheVehID).capacity-- ;
						matchedPassengers++;
					}
					
					passengerUtilSum += passengers.get(i).utility;
					
				}// end of passenger loop
				
				
				for (int i = 0 ; i < vehicles.size() ; i++)
				{
					vehicleUtilSum += vehicles.get(i).utility;
				}
				
				
				
				
				//==================== calculating the mean utility ====================

				double meanPassengersUtil = passengerUtilSum / passengers.size();

				double meanVehUtil = vehicleUtilSum / (vehicles.size() );
				
				double matchedPassPercent = (double) matchedPassengers/passengers.size() * 100;
				double matchedVehPercent = (double) matchedPassengers /vehicles.size() * 100; //(vehicles.size() - unmatchedVehicles)
				
				if (k % iterationWrite == 0 || k == 1 )
				{
					StringBuilder fileContentP = new StringBuilder();
					StringBuilder fileContentV = new StringBuilder();
					
					fileContentP.append("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
					for (int i = 0 ; i < passengers.size(); ++i)
						fileContentP.append(k + "," + passengers.get(i).toString() + "\n");
					
					fileContentV.append("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
					for (int i = 0 ; i < vehicles.size(); ++i)
						fileContentV.append(k + "," +vehicles.get(i).toString() + "\n");
					
					ZahraUtility.write2File(fileContentP.toString(), dir + k + "-passengers-YarraRanges" 
							+ "-V" + vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
					ZahraUtility.write2File(fileContentV.toString(), dir + k + "-vehicles-YarraRanges" + "-V" 
							+ vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
				}
				
				bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," 
						+ (double)interestedPassengers/passengers.size() * 100 + "%," 
						+  matchedPassPercent + "%," + matchedVehPercent + "%," + interestedPassengers 
						+ "," + matchedPassengers + "," + matchedPassengers + "," 
						+ vehicles.size() + "\n");
				    
				    
				//=============== vehicles with utility lower than a certain threshold leave ===============
			    for (int i = 0 ; i < vehicles.size(); i ++)
			    {
			    	if (vehicles.get(i).utility < vehUtilThres)
			    	{
			    		vehicles.remove(i);
			    		i--;
			    	}
			    	else
			    	{
			    		vehicles.get(i).setUtility(0.0);
						vehicles.get(i).setUtility(vehicleCapacity);
						vehicles.get(i).setId(i);
			    	}
			    	
			    }
			    
			    //resetting veh id
//			    for (int i = 0 ; i < vehicles.size(); i ++)
//			    {
//			    	vehicles.get(i).setId(i);
//			    } 
			    
							
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
			System.out.println(probIteration);
			
			bufferedWriter.close();
		    
		    
		}//end of probability iteration
		double successProb = (double) probability/allIterations * 100; 
		System.out.println(successProb + "%");
		pBufferedWriter.write("2447," + vehAddedInIteration + "," + vehDist + "," + successProb + "\n");
		pBufferedWriter.close();
		
		System.out.println("DONE");
		Toolkit.getDefaultToolkit().beep();
	}
		

}
