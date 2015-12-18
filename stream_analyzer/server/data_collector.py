#
# Data collection module.
# 
#
import pyspark
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import requests as _req

_sc = SparkContext("local[2]", "Twitter Data Collector")
_ssc = StreamingContext(_sc, 1)

def run():
	'''Run data collection task'''
	url = "https://stream.twitter.com/1.1/statuses/filter.json?"
	res = _req.get(url, auth=config.auth, stream=True)


#String CONSUMER_KEY = "goiaVjc84eaL50XTT5VPHx03H"
#String CONSUMER_SECRET = "zwKNRZaEXIFhClmGapw7DawbaWnT9XXEHFSsmuhn5PhXFw8B3B"
#String ACCESS_TOKEN = "3622126518-n42YOv8uiLzWVVwJMouMRSdD206FHyg2YD1OBYE"
#String ACCESS_TOKEN_SECRET = "ENQ4hgzLMPNxO07biRvtlaGmgkyGC7yijqv7Cx9Ks6KqY"
    
#System.setProperty("twitter4j.oauth.consumerKey", CONSUMER_KEY)
#System.setProperty("twitter4j.oauth.consumerSecret", CONSUMER_SECRET)
#System.setProperty("twitter4j.oauth.accessToken", ACCESS_TOKEN)
#System.setProperty("twitter4j.oauth.accessTokenSecret", ACCESS_TOKEN_SECRET)

if __name__=='__main__':
	run()
