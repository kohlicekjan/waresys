import RPi.GPIO as GPIO
import time


class LedRGB(object):
    """ Ovládání RGB LED diody"""

    def __init__(self, pin_red, pin_green, pin_blue):
        self.__pins = (pin_red, pin_green, pin_blue)

        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.__pins, GPIO.OUT)
        GPIO.output(self.__pins, GPIO.LOW)

    def blink(self, color=None, length=0.1, num=2):
        if (color is not None):
            self.set_color(color)

        for i in range(0, num):
            self.switch_on()
            time.sleep(length)
            self.switch_off()
            time.sleep(length)

    def switch_off(self):
        GPIO.output(self.__pins, (GPIO.LOW, GPIO.LOW, GPIO.LOW))

    def switch_on(self, color=None):
        if (color is not None):
            self.set_color(color)

        GPIO.output(self.__pins, self.__color.rgb)

    def toggle(self):
        if self.is_on:
            self.switch_off()
        else:
            self.switch_on()

    def set_color(self, color):
        if not isinstance(color, Color):
            raise ValueError('Invalid color received')

        self.__color = color

    @property
    def pins(self):
        return self.__pins

    @property
    def is_on(self):
        return GPIO.input(self.__pins[0]) or GPIO.input(self.__pins[1]) or GPIO.input(self.__pins[2])


class Color(object):
    def __init__(self, color_red=None, color_green=None, color_blue=None, color_hex=None):
        if (color_hex is not None):
            hex = color_hex.lstrip('#')
            hex_len = len(hex)
            try:
                self.__rgb = tuple(int(hex[i:i + hex_len // 3], 16) for i in range(0, hex_len, hex_len // 3))
            except ValueError:
                raise ValueError('Invalid hex color received')
        else:
            self.__rgb = (color_red, color_green, color_blue)

        if not self.__check():
            raise ValueError('Invalid color provided')

    def __check(self):
        for value in self.__rgb:
            if 0 <= value <= 255:
                continue
            return False
        return True

    @property
    def hex(self):
        return '#%02X%02X%02X' % self.__rgb

    @property
    def rgb(self):
        return self.__rgb
