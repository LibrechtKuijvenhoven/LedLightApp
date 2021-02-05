from flask import Flask
from settings import *
import os

#initialize Flask
app = Flask(__name__)

# redundant
@app.route('/')
def home():
    return

#shutdown Raspberry Pi
@app.route('/shutdown')
def shutdown():
    os.system("sudo shutdown -h now")

#parse the settings and colors to the set
@app.route('/<method>/<color>/<color2>')
def method(method, color, color2):
    setting = Setting()
    setting.setColors(color, color2)
    setting.setMethod(method)
    return "Empty"

#run server
app.run(host="0.0.0.0")