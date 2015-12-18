import pyspark as _ps
from _ps.sql import SQLContext

sql_context = SQLContext(sc)

df = sql_context.read.json("user/root/tweets/tweet-1450444076000.raw")

df.show()

