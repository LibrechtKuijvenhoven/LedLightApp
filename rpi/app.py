from flask import Flask
from settings import *
import os

app = Flask(__name__)
neo = Neo()

@app.route('/')
def home():
    return

@app.route('/shutdown')
def shutdown():
    neo.shutdown()
    os.system("sudo shutdown -h now")

@app.route('/<method>/<color>/<color2>')
def blink(method, color, color2):
    neo.setColors(color, color2)
    neo.setMethod(method)
    return "Empty"


app.run(host="0.0.0.0")