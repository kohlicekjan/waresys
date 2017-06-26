import sys
import os

__version__ = '1.0.6'
__author__ = 'Jan Kohlicek'

APP_NAME = 'reader_rfid'
HOST = 'localhost'
PORT = 1883

LED_PINS = {'RED': 36, 'GREEN': 38 , 'BLUE': 40}

MQTT_TOPIC = {}
MQTT_TOPIC['LED'] = '{0}/led'
MQTT_TOPIC['TAG'] = '{0}/tag'
MQTT_TOPIC['INFO'] = '{0}/info'

LOG_FILE = os.getenv('LOGFILE', APP_NAME + '.log')
LOG_FORMAT = '%(asctime)s [%(levelname)s] %(message)s'