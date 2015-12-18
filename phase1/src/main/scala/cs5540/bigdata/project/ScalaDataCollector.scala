import org.apache.spark
import org.apache.spark.streaming
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.hadoop.mapred._

package cs5540.bigdata.project
{
	object ScalaDataCollector
	{

		val conf = new SparkConf().setMaster("local[2]").setAppName("Data Collector")
		val ssc = new StreamingContext(conf, Seconds(1))

		def main(args: Array[String]){
			run()
		}

		def run() : Unit = {
			//Tokens
			val CONSUMER_KEY = "goiaVjc84eaL50XTT5VPHx03H"
			val CONSUMER_SECRET = "zwKNRZaEXIFhClmGapw7DawbaWnT9XXEHFSsmuhn5PhXFw8B3B"
			val ACCESS_TOKEN = "3622126518-n42YOv8uiLzWVVwJMouMRSdD206FHyg2YD1OBYE"
			val ACCESS_TOKEN_SECRET = "ENQ4hgzLMPNxO07biRvtlaGmgkyGC7yijqv7Cx9Ks6KqY"
			//Set twitter4j oauth properties
			System.setProperty("twitter4j.oauth.consumerKey", CONSUMER_KEY)
			System.setProperty("twitter4j.oauth.consumerSecret", CONSUMER_SECRET)
			System.setProperty("twitter4j.oauth.accessToken", ACCESS_TOKEN)
			System.setProperty("twitter4j.oauth.accessTokenSecret", ACCESS_TOKEN_SECRET)

			ssc.checkpoint("test/checkpoint")
			val tweets = TwitterUtils.createStream(ssc, None)
			val paired_tweets = tweets.map(status => (status.getId().toString, status.getText()))
				paired_tweets.print()
				paired_tweets.saveAsHadoopFiles("test/tweets", "raw", classOf[String], classOf[String], classOf[TextOutputFormat[String, String]])
			ssc.start()
				ssc.awaitTermination()
		}
	}
}


