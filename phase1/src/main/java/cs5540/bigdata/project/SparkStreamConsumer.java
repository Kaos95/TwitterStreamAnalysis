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
import java.lang.Runtime;

//This code was developed with the help of the Spark Streaming documentation
//introduction found at:
//https://spark.apache.org/docs/1.1.0/streaming-programming-guide.html

public class SparkStreamConsumer {
	public static void main(String[] args){
		run();
	}

	public static void run(){

		//Tokens
		String CONSUMER_KEY = "goiaVjc84eaL50XTT5VPHx03H";
		String CONSUMER_SECRET = "zwKNRZaEXIFhClmGapw7DawbaWnT9XXEHFSsmuhn5PhXFw8B3B";
		String ACCESS_TOKEN = "3622126518-n42YOv8uiLzWVVwJMouMRSdD206FHyg2YD1OBYE";
		String ACCESS_TOKEN_SECRET = "ENQ4hgzLMPNxO07biRvtlaGmgkyGC7yijqv7Cx9Ks6KqY";

		//Set twitter4j oauth properties
		System.setProperty("twitter4j.oauth.consumerKey", CONSUMER_KEY);
		System.setProperty("twitter4j.oauth.consumerSecret", CONSUMER_SECRET);
		System.setProperty("twitter4j.oauth.accessToken", ACCESS_TOKEN);
		System.setProperty("twitter4j.oauth.accessTokenSecret", ACCESS_TOKEN_SECRET);

		//Create a local spark streaming context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Twitter Sentiment Analysis");
		JavaStreamingContext jsc = new JavaStreamingContext(conf, new Duration(1000));
		
		//Hashtags
//		String[] hashtags = { "YOLO", "yolo", "wtf", "monday"
//				"hillary2016", "hillaryclinton", "hillary",
//				"TreatmentForAll", "IsThatAskingTooMuch", "Syria",
//				"Volkswagen"
//				"berniesanders", "bernie2016", "feelthebern", 
//				"trump", "trump2016", "bencarson", "bencarsonfacts",
//				"bencarsonwikipedia", "freehillary", "hillaryforprison2016",
//				"trumpisright", "trumpisdangerous", "GOP", "republican",
//				"democrat", "thankful", "thanksgiving", "blackfriday",
//				"cyberweekend", "cybermonday" 
//				};

//		String[] filters = new String[hashtags.length];

//		for(int idx = 0; idx < hashtags.length; idx++){
//			filters[idx] = "hashtags " + hashtags[idx];
//		}

		//Create DStream (Discretized Stream) of twitter user statuses
		JavaReceiverInputDStream<twitter4j.Status> rawStatuses = TwitterUtils.createStream(jsc);

//		final HashMap<String, Boolean> tagMap = new HashMap<String, Boolean>();

//		//Create hashmap for optimized processing of statuses
//		for( String tag : hashtags ){
//			tagMap.put(tag, true);
//		}

		//Filter results
		JavaDStream<twitter4j.Status> filteredStatuses = rawStatuses;

//rawStatuses.filter(
//			new Function<twitter4j.Status, Boolean>(){
//				public Boolean call(twitter4j.Status x){
//					twitter4j.HashtagEntity[] hashtags = x.getHashtagEntities();
//					Boolean containsHashtag = false;
					
					//Check if status hashtags are those
					//being analyzed 
//					for(twitter4j.HashtagEntity hashtag : hashtags){
//						Boolean foundTag = tagMap.get(hashtag.getText());
//						if(foundTag == null){
//							continue;
//						}
//						else if(foundTag == true){
//							containsHashtag = true;
//							break;
//						}
//					}
//					return containsHashtag;
//				}
//			}
//		);

		//Map to PairRDD because needed for saveHadoopFiles() fcn
		JavaPairDStream<String, twitter4j.Status> pairedStatuses = filteredStatuses.mapToPair(
			new PairFunction<twitter4j.Status, String, twitter4j.Status>(){
				@Override
				public Tuple2<String, twitter4j.Status> call(twitter4j.Status x){
					return new Tuple2<String, twitter4j.Status>(Long.toString(x.getId()), x); 
				}
			}
		);

//		//Split each line into words
//		JavaDStream<String> words = filteredStatuses.flatMap(
//			new FlatMapFunction<twitter4j.Status, String>(){
///				@Override
//				public Iterable<String> call(twitter4j.Status x){
//					return Arrays.asList(x.getText().split(" "));
//				}
//			}
//		);

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
		pairedStatuses.print();

		//Save results to distributed file system
		//pairedStatuses.saveAsTextFiles("sentiment", "out");

		//Now that computation is set up, invoke
		jsc.start();
		jsc.awaitTermination();
	}
}
