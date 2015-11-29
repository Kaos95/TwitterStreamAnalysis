# TwitterStreamAnalysis
Spark project utilizing the twitter streaming API, Spark Streaming API, Apache Maven.

Design
	Step 1. Collect tweets. 
		|TweetCollector| <--> Twitter API or equivalently
		|Filter| <--> Twitter API

	Step 2. Filter out undesirable tweets.
