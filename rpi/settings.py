import RPi.GPIO as GPIO
from time import *
import math

class Setting:
    """"""
    def __init__(self):
        #setup gpio
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(0)       
        self.clock_pin = 19
        self.data_pin = 26
        GPIO.setup(self.clock_pin, GPIO.OUT)
        GPIO.setup(self.data_pin, GPIO.OUT)

        #set total amounts of leds
        self.leds = 8

        #set times
        self.delay = 0.2
        self.wait = 0.5

        #initialize bools
        self.runBool = False
        self.blinkBool = False
        self.bounceBool = False

        #initialize  colors
        self.mainColor = ''
        self.secundairColor = '[0,0,0]'
        self.colorOff = [0,0,0]
    
    def bitSender(self, bytes):
        """
        Sends the given bytes to the APA102 ledstring 
        
        @param bytes : String[]

        @return Nothing
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
        """
        Converts given list of color int to binary

        @param colors : int[]

        @return Nothing
        """
        bytesList = ["00000000", "00000000", "00000000", "00000000"]
        for color in colors:

            bytesList.append("11111111")
            for i in color:
                bytesList.append(bin(i).replace("0b","").zfill(8))
        bytesList += ["11111111", "11111111", "11111111", "11111111"]
        self.bitSender(bytesList)

    """
    color settings
    """

    def colours (self, x, n, on, off):
        """
        Checks wich leds chould be on and which should be off

        @param x - current position of outer scope loop : int
        @param n - total leds on ledstrip : int
        @param on - color when led must be on : int[]
        @param off - color when led must be off : int[]

        @return list of colors in order : [int[]]
        """
        result = []
        for i in range(0,n):
            if i == x:
                result.append(on)
            else:
                result.append(off)
        return result

    def run(self):
        """
        Lets led walk across ledstrip

        @retun Nothing
        """
        firstColorList = self.stringToList(self.mainColor)
        secondColorList = self.stringToList(self.secundairColor)
        while self.runBool:
            for led in range(self.leds):
                self.bitBuilder(self.colours(led, self.leds, firstColorList, secondColorList ))
                sleep(self.delay)
            if not self.runBool:
                return

    def bounce(self):
        """
        Lets led bounce across ledstrip

        @retun Nothing
        """
        firstColorList = self.stringToList(self.mainColor)
        secondColorList = self.stringToList(self.secundairColor)
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
        """
        Sets ledstrip one color

        @retun Nothing
        """
        colorList = self.stringToList(self.mainColor)
        leds = []
        for i in range(self.leds):
            leds.append(colorList)
        self.bitBuilder(leds)    
    
    def fade(self):
        """
        Lets color fade out trough out ledstrip

        @retun Nothing
        """
        colorList = self.stringToList(self.mainColor)
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
        """
        Lets color fade to second color trough out ledstrip

        @retun Nothing
        """
        firstColorList = self.stringToList(self.mainColor)
        secondColorList = self.stringToList(self.secundairColor)
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
        """
        Sets ledstrip one color then sets it an other

        @return Nothing

        """
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
    def stringToList(self, color):
        """
        Converts string to list

        @param color : String

        @return colorList : int[]
        """
        map_object = map(int, color.split(","))
        colorList = list(map_object)
        return colorList

    def setsetting(self, setting):
        """
        Run the given setting

        @param setting : String

        @return Nothing
        """
        self.setOff()
        if setting == "Still":
            self.still()
        elif setting == "Fade":
            self.fade()
        elif setting == "Blink":
            self.setBlink()
            self.blink()
        elif setting == "Run":
            self.setRun()
            self.run()
        elif setting == "Bounce":
            self.setBounce()
            self.bounce()
        elif setting == "Gradient":
            self.gradient()
    
    def setColors(self, color, color2):
        """
        Set the colors of class

        @param color : String
        @param color2 : String

        @return Nothing
        """
        self.mainColor = color
        self.secundairColor = color2

    def setBounce(self):
        """
        Set bounceBool True to let setting loop

        @return Nothing
        """
        self.bounceBool = True

    def setBlink(self):
        """
        Set blinkBool True to let setting loop

        @return Nothing
        """
        self.blinkBool = True

    def setRun(self):
        """
        Set runBool True to let setting loop

        @return Nothing
        """
        self.runBool = True

    def setOff(self):
        """
        Set all bools False to let setting stop looping

        @return Nothing
        """
        self.runBool = False
        self.blinkBool = False
        self.bounceBool = False
    

