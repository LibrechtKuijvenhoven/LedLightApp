import RPi.GPIO as GPIO
from time import *
import math

class Neo:
    """"""
    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(0)
        self.clock_pin = 19
        self.data_pin = 26
        self.leds = 8

        self.delay = 0.2
        self.wait = 0.5

        GPIO.setup(self.clock_pin, GPIO.OUT)
        GPIO.setup(self.data_pin, GPIO.OUT)
        self.runBool = False
        self.blinkBool = False
        self.bounceBool = False

        self.mainColor = ''
        self.secundairColor = '[0,0,0]'




    def bitSender(self, bytes):
        """
        zend de bytes naar de APA102 LED strip die is aangesloten op de clock_pin en data_pin
        """
        for byte in bytes:
            for bit in byte:
                if bit == "1":
                    GPIO.output(self.data_pin, GPIO.HIGH)
                else:
                    GPIO.output(self.data_pin, GPIO.LOW)
                GPIO.output(self.clock_pin, GPIO.HIGH)
                GPIO.output(self.clock_pin, GPIO.LOW)

    def bitBuilder(self, colors):
        bytesList = ["00000000", "00000000", "00000000", "00000000"]
        # zend dan voor iedere pixel:
        print(colors)
        for color in colors:
            # eerste een byte met allemaal enen
            # dan de 3 bytes met de kleurwaarden
            bytesList.append("11111111")
            for i in color:
                bytesList.append(bin(i).replace("0b","").zfill(8))
        # zend nog 4 bytes, maar nu met allemaal enen
        bytesList += ["11111111", "11111111", "11111111", "11111111"]
        self.bitSender(bytesList)

    """
    color methods
    """
    def colours (self, x, n, on, off):
        result = []
        for i in range(0,n):
            if i == x:
                result.append(on)
            else:
                result.append(off)
        return result

    def run(self):
        firstColorList = self.setColor(self.mainColor)
        secondColorList = self.setColor(self.secundairColor)
        while self.runBool:
            for led in range(self.leds):
                self.bitBuilder(self.colours(led, self.leds, firstColorList, secondColorList ))
                sleep(self.delay)
            if not self.runBool:
                return

    def bounce(self):
        firstColorList = self.setColor(self.mainColor)
        secondColorList = self.setColor(self.secundairColor)
        while self.bounceBool:
            for led in range(0, self.leds):
                self.bitBuilder(self.colours(led, self.leds, firstColorList, secondColorList ))
                sleep(self.delay)
            for led in range(self.leds, 0, -1):
                self.bitBuilder(self.colours(led, self.leds, firstColorList, secondColorList ))
                sleep(self.delay)
            if not self.bounceBool:
                return
  
    def still(self):
        colorList = self.setColor(self.mainColor)
        leds = []
        for i in range(self.leds):
            leds.append(colorList)
        self.bitBuilder(leds)    
    
    def fade(self):
        colorList = self.setColor(self.mainColor)
        setNegZero = lambda a: int((abs(a)+a)/2)
        b = math.ceil(colorList[0] / self.leds)
        g = math.ceil(colorList[1] / self.leds)
        r = math.ceil(colorList[2] / self.leds)
        fade = []
        for i in range(1, self.leds + 1):
            fading = [ setNegZero((colorList[0]) - math.floor(b * i)), setNegZero((colorList[1]) - math.floor(g * i)),setNegZero((colorList[2]) - math.floor(r * i)) ]
            fade.append(fading)
        self.bitBuilder(fade)    

    def gradient(self):
        firstColorList = self.setColor(self.mainColor)
        secondColorList = self.setColor(self.secundairColor)
        setNegZero = lambda a: int((abs(a)+a)/2)
        b = math.ceil((firstColorList[0] - secondColorList[0] ) / (self.leds - 1))
        g = math.ceil((firstColorList[1] - secondColorList[1] ) / (self.leds - 1))
        r = math.ceil((firstColorList[2] - secondColorList[2] ) / (self.leds - 1))
        fade = []
        for i in range(self.leds):
            fading = [ setNegZero((firstColorList[0]) - math.floor(b * i)), setNegZero((firstColorList[1]) - math.floor(g * i)), setNegZero((firstColorList[2]) - math.floor(r * i)) ]
            fade.append(fading)
        self.bitBuilder(fade)  

    def blink(self):
        chosenColor = self.mainColor
        while self.blinkBool:
            self.still()
            self.mainColor = self.secundairColor
            sleep(self.wait)
            self.still()
            self.mainColor = chosenColor
            if not self.blinkBool:
                return
            sleep(self.wait)

    """
    Setters
    """
    def setColor(self, color):
        map_object = map(int, color.split(","))
        colorList = list(map_object)
        return colorList

    def setMethod(self, method):
        print(method)
        self.setOff()
        if method == "Still":
            self.still()
        elif method == "Fade":
            self.fade()
        elif method == "Blink":
            self.setBlink()
            self.blink()
        elif method == "Run":
            self.setRun()
            self.run()
        elif method == "Bounce":
            self.setBounce()
            self.bounce()
        elif method == "Gradient":
            self.gradient()
    
    def setColors(self, color, color2):
        self.mainColor = color
        self.secundairColor = color2

    def setBounce(self):
        self.bounceBool = True

    def setBlink(self):
        self.blinkBool = True

    def setRun(self):
        self.runBool = True

    def setOff(self):
        self.runBool = False
        self.blinkBool = False
        self.bounceBool = False


