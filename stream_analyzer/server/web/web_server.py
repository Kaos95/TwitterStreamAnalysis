#
# Flask web server serving the project web pages
#
from bson.objectid import ObjectId
from flask import Flask
import json as _JSON
import pymongo as _PYMONGO

app=Flask(__name__)

@app.route('/', methods=["GET"])
def heartbeat():
	return _JSON.dumps({ "success":True, "status":200 })

if __name__ == '__main__':
	app.run()
