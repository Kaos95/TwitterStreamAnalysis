package cs5540.bigdata.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.HashMap;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.twitter.*;
import scala.Tuple2;

import org.apache.spark.streaming.twitter.twitter4j;

//This code was developed with the help of the Spark Streaming documentation
//introduction found at:
//https://spark.apache.org/docs/1.1.0/streaming-programming-guide.html

public class SparkStreamConsumer {
	public static void main(String[] args){
		run();
	}

	public static void run(){

		//Filtration values
		

		//Tokens
		String CONSUMER_KEY = "DsrKVsGSct5khALPJJh3VM7aZ";
		String CONSUMER_SECRET = "SCrcpDiw5Pw7mKqBBn6f4cLw2I4Fhxss1mI8eWLp5kRJy9LWcP";
		String ACCESS_TOKEN = "3633627554-rFls2gI4qbrqJTbR0BZKoCADouY2TkjLFEa8FQe";
		String ACCESS_TOKEN_SECRET = "GBgogkDScpUknkuXrl4nNrQhEPIW65TJlkRkX3rkBHqO2";

		//Set twitter4j oauth properties
		System.setProperty("twitter4j.oauth.consumerKey", CONSUMER_KEY);
		System.setProperty("twitter4j.oauth.consumerSecret", CONSUMER_SECRET);
		System.setProperty("twitter4j.oauth.accessToken", ACCESS_TOKEN);
		System.setProperty("twitter4j.oauth.accessTokenSecret", ACCESS_TOKEN_SECRET);

		//Create a local spark streaming context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Twitter Sentiment Analysis");
		JavaStreamingContext jsc = new JavaStreamingContext(conf, new Duration(1000));
		
		//Hashtags
		String[] hashtags = { "hillary2016", "hillaryclinton", "hillary",
				"berniesanders", "bernie2016", "feelthebern", 
				"trump", "trump2016", "bencarson", "bencarsonfacts",
				"bencarsonwikipedia", "freehillary", "hillaryforprison2016",
				"trumpisright", "trumpisdangerous", "GOP", "republican",
				"democrat", "thankful", "thanksgiving", "blackfriday",
				"cyberweekend", "cybermonday" }

		String[] filters = new String[hashtags.length];

		HashMap<String, Boolean> tagMap = new HashMap<String, Boolean>();

		for(int idx = 0; idx < hashtags.length; idx++){
			filters[idx] = "\"hashtags\" " + hashtags[idx]
		}

		//Create DStream (Discretized Stream) of twitter user statuses
		JavaReceiverInputDStream<Status> rawStatuses = TwitterUtils.createStream(jsc, filters);

		//Create hashmap for optimized processing of statuses
		for( String tag : hashtags ){
			tagMap.put(tag, true);
		}

		//Filter results
		JavaDStream<Status> filteredStatuses = rawStatuses.filter(
			new Function<Status, Boolean>(){
				public Boolean call(Status x){
					HashtagEntity[] hashtags = x.getHashtagEntities();
					Boolean containsHash = false;
					
					//Check if status hashtags are those
					//being analyzed 
					for(HashtagEntity hashtag : hashtags){
						if( tagMap.get(hashtag.getText()) )
							return true
					}
					return false
				}
			}
		);

		//Split each line into words
		JavaDStream<String> words = filteredStatuses.flatMap(
			new FlatMapFunction<Status, String>(){
				@Override
				public Iterable<String> call(Status x){
					return Arrays.asList(x.getText().split(" "));
				}
			}
		);
		
		

//		//Count each word in each batch
//		JavaPairDStream<String, Integer> pairs = words.mapToPair(
//			new PairFunction<String, String, Integer>(){
//				@Override
//				public Tuple2<String, Integer> call(String s) throws Exception {
//					return new Tuple2<String, Integer>(s, 1);
//				}
//			}
//		);
//
//		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
///			new Function2<Integer, Integer, Integer>(){
///				@Override
//				public Integer call(Integer i1, Integer i2) throws Exception {
//					return i1 + i2;
//				}
//			}
//		);

		//Print the results
		wordCounts.print();

		//Save results to distributed file system
		wordCounts.saveAsHadoopFiles("sentiment", "out");

		//Now that computation is set up, invoke
		jsc.start();
		jsc.awaitTermination();
	}
}
