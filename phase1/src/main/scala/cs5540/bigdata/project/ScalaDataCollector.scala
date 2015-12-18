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
import twitter4j.HashtagEntity
import scala.collection.mutable.HashMap
import scala.math._
import scala.util.matching.Regex

package cs5540.bigdata.project
{
	object ScalaDataCollector
	{
                val CONSUMER_KEY = "goiaVjc84eaL50XTT5VPHx03H"
                val CONSUMER_SECRET = "zwKNRZaEXIFhClmGapw7DawbaWnT9XXEHHFSsmuhn5PhXFw8B3B"
                val ACCESS_TOKEN = "3622126518-n42YOv8uiLzWVVwJMouMRSdD2206FHyg2YD1OBYE"
                val ACCESS_TOKEN_SECRET = "ENQ4hgzLMPNxO07biRvtlaGmgkyGCC7yijqv7Cx9Ks6KqY"
                System.setProperty("twitter4j.oauth.consumerKey", CONSUMER_KEY)
                System.setProperty("twitter4j.oauth.consumerSecret", CONSUMER_SECRET)
                System.setProperty("twitter4j.oauth.accessToken", ACCESS_TOKEN)
                System.setProperty("twitter4j.oauth.accessTokenSecret", ACCESS_TOKEN_SECRET)

		val HASHTAG_LIST = List(
				"taylorswift", "geek", "mcdonalds",
				"donaldtrump", "hilaryclinton", "cake",
				"yolo", "merrychristmas", "happyholidays"
					  )
		val FILTER_HASHTAGS = new HashMap[String, Boolean]()

		def run() : Unit = {

			val PREFIX = "tweets/tweet"
                	val SUFFIX = "raw"
        	        val CHECKPOINT = "tweets/checkpoint"
                	val CONF = new SparkConf().setMaster("local[2]").setAppName("Data Collector")
	                val SSC = new StreamingContext(CONF, Seconds(1))
			SSC.checkpoint(CHECKPOINT)

			// Open input stream
			val in = TwitterUtils.createStream(SSC, None)

			// Filter tweets
			val keywordFilteredTweets = in.filter(tweet => hasKeywordInMessageBody(tweet))
			val hashFilteredTweets = in.filter(tweet => containsFilterHashtag(tweet))
			var filteredTweets = keywordFilteredTweets.union(hashFilteredTweets)			
			filteredTweets = filteredTweets.filter(status => status.getText.length != 0)

			// Pair tweets (key=value. This is required to use the saveAsHadoopFiles function for some reason)
			val tweets = filteredTweets.map(status => (status.getId(), status.getText))

			tweets.print() // TODO: DEBUG statement. Remove later
			tweets.saveAsHadoopFiles(PREFIX, SUFFIX, classOf[String], classOf[String], classOf[TextOutputFormat[String, String]])

			// Start job
			SSC.start()
			SSC.awaitTermination()
		}

		def hasKeywordInMessageBody(tweet:twitter4j.Status) : Boolean = {
                        val words = (tweet.getText).split(" +")
                        val wordChecklist = new HashMap[String, Boolean]()
                        for( word <- words ){
                                if( !wordChecklist.contains(word.toString) ){
                                        wordChecklist.put(word.toString, true)
                                        if( FILTER_HASHTAGS.contains(word.toString) ){
                                                return true
                                        }
                                }
                        }
                        return false
                }

		def containsFilterHashtag(tweet:twitter4j.Status) : Boolean = {
                        val tweet_hashtags = tweet.getHashtagEntities
                        for( hashtag <- tweet_hashtags ){
                                if( FILTER_HASHTAGS.contains(hashtag.getText) ){
                                        return true
				}
			}
                        return false
                }

		def main(args: Array[String]){
	                for( hashtag <- HASHTAG_LIST ){
        	                FILTER_HASHTAGS.put( hashtag, true )
                	}
                        run()
                }
	}
}
