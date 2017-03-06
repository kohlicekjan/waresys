import argparse
import logging
import json
import time

import sys

import signal
import os

import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import rc522
import led_rgb

import utils
import __init__ as reader_rfid

HOST = '10.10.90.26'
PORT = 1883
USER = 'reader_rfid'
PASS = 'heslo'
PINS_LED_RGB = [36,38,40]


MQTT_TOPIC_LED = '{0}/led'
MQTT_TOPIC_TAG = '{0}/tag'
MQTT_TOPIC_INFO = '{0}/info'

client = None
#INIFILE = os.getenv('INIFILE', reader_rfid.APP_NAME + '.ini')
LOG_FILE = os.getenv('LOGFILE', reader_rfid.APP_NAME + '.log')

LOG_FORMAT = '%(filename)s:%(lineno)s %(levelname)s:%(message)s'


def cleanup(signum, frame):
    GPIO.cleanup()

    logging.info("Disconnecting from broker...")   
    client.loop_stop()
    client.disconnect()

    logging.info("Exiting on signal {0}".format(signum))
    sys.exit(signum) 






def on_disconnect(client, userdata, rc):
    logging.info('disconnect', {'rc': rc})
    print("Disconnect to {0}:{1}".format(client._host, client._port))
    logging.info("Disconnect to {0}:{1}".format(client._host, client._port))

def on_log(client, userdata, level, string):
    logging.log(level, string)


def on_led(client, userdata, msg):
    data = json.loads(msg.payload.decode('utf-8'))

    try:
        if(set(('color', 'blink')).issubset(data)):                     
            color = led_rgb.Color(color_hex=data['color'])
            
            if(int(data['blink']) > 0):
                userdata['ledrgb'].blink(color, num = int(data['blink']))
            else:
                userdata['ledrgb'].switch_on(color)
                                
    except Exception as e:
        logging.warning(e)


def on_message(client, userdata, msg):
    print("Topic: {0}, msg: {1}".format(msg.topic, msg.payload))
    logging.info('on_message', {'msg': msg})



def on_connect(client, userdata, flags, rc):
    logging.info('connect',{'flags': flags, 'rc': rc})

    if(rc == 0):
        print("Connected to {0}:{1}".format(client._host, client._port))
        logging.info("Connected to {0}:{1}".format(client._host, client._port))

        client.subscribe(MQTT_TOPIC_LED.format(client._client_id), 0)

        client.message_callback_add(MQTT_TOPIC_LED.format(client._client_id), on_led)   


def parse_args():
    parser = argparse.ArgumentParser(prog='reader_rfid',description='')
    parser.add_argument('-v', '--version', action='version', version="%(prog)s (version {0})".format(reader_rfid.__version__))

    
    parser.add_argument('-c', '--config', help='ini config file path', dest='config', default='/dev/null')
    parser.add_argument('-d', '--debug', dest='debug', action='store_true', default=False, help='Debug mode')
    parser.add_argument('-s', '--host',  default = HOST, help = "Host to connect [default: %(default)s]")
    parser.add_argument('-o', '--port',  type = int, default = PORT, help = "Port to connect [default: %(default)d]")
    parser.add_argument('-u', '--user',  default = USER, help = "Username on remote host [default: %(default)s]")
    parser.add_argument('-p', '--password',  default = PASS, help = "Password on remote [default: %(default)s]")
       
    return parser.parse_args()
            

def main(): 
    args = parse_args()


    #options = ibmiotf.application.ParseConfigFile(cliArgs.config)
    #options. = args.op or options.


    for sig in [signal.SIGTERM, signal.SIGINT, signal.SIGHUP, signal.SIGQUIT]:
        signal.signal(sig, cleanup)

    log_level = args.debug if logging.DEBUG else logging.INFO
    logging.basicConfig(filename=LOG_FILE, level=log_level, format=LOG_FORMAT)

    logging.info("----BPINI - READER RFID----")

    client_id = '{0}/{1}'.format('reader_rfid', utils.device_id())

    logging.info("Client ID: {0}".format(client_id))

    client = mqtt.Client(client_id)
    client.username_pw_set(args.user, args.password)
    client.on_message = on_message
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_log = on_log


    while(True):
        try:
            client.connect(args.host, args.port, 60)
            break
        except Exception as e:
            print(e)
            logging.warning(e)
            time.sleep(1)
   
    client.loop_start()
    

    data = {'serial_number': utils.serial_number(), 'vesion': reader_rfid.__version__}
    infot = client.publish(MQTT_TOPIC_INFO.format(client._client_id), json.dumps(data), qos = 1, retain = False)
    infot.wait_for_publish()

    ledrgb = led_rgb.LedRGB(36,38,40)
    client.user_data_set({'wait': False,'ledrgb': ledrgb})

    rfid = rc522.RFID()

    while(True):
        uid = rfid.read_uid()
        if(uid is not None):
            logging.info("Read TAG (UID): {0}".format(uid))
            data = {'uid': uid}

            ledrgb.switch_off()

            infot = client.publish(MQTT_TOPIC_TAG.format(client._client_id), json.dumps(data), qos=1, retain=False)
            infot.wait_for_publish()  
        else:
            color = led_rgb.Color(color_hex='#ff0000')
            ledrgb.blink(color)


if __name__ == "__main__":
    try:
        main()
    except (KeyboardInterrupt, SystemExit):
        sys.exit(0)