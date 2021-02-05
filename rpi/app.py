from flask import Flask
from settings import *
import os

app = Flask(__name__)

@app.route('/')
def home():
    return

@app.route('/shutdown')
def shutdown():
    os.system("sudo shutdown -h now")

@app.route('/<method>/<color>/<color2>')
def method(method, color, color2):
    neo = Neo()
    neo.setColors(color, color2)
    neo.setMethod(method)
    return "Empty"


app.run(host="0.0.0.0")