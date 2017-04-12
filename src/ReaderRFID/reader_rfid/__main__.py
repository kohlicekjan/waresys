import argparse
import logging
import json
import time
import sys
import os

import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import rc522
import led_rgb
import utils
import __init__ as reader_rfid

HOST = '10.10.90.26'
PORT = 1883
LED_PINS = {'red': 36, 'green': 38 , 'blue': 40}

MQTT_TOPIC_LED = '{0}/led'
MQTT_TOPIC_TAG = '{0}/tag'
MQTT_TOPIC_INFO = '{0}/info'

LOG_FILE = os.getenv('LOGFILE', reader_rfid.APP_NAME + '.log')
LOG_FORMAT = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'


def message_led(client, userdata, msg):
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

def on_disconnect(client, userdata, rc):
    print("Disconnect to {0}:{1}".format(client._host, client._port))
    logging.info("Disconnect to {0}:{1}".format(client._host, client._port))
    userdata['ledrgb'].switch_off()

def on_log(client, userdata, level, string):
    logging.log(level, string)

def on_message(client, userdata, msg):
    logging.info('on_message', {'msg': msg})

def on_connect(client, userdata, flags, rc):
    logging.info('connect',{'flags': flags, 'rc': rc})

    if(rc == 0):
        print("Connected to {0}:{1}".format(client._host, client._port))
        logging.info("Connected to {0}:{1}".format(client._host, client._port))

        client.subscribe(MQTT_TOPIC_LED.format(client._client_id), 0)
        client.message_callback_add(MQTT_TOPIC_LED.format(client._client_id), message_led)   

        data = {'serial_number': utils.serial_number(), 'vesion': reader_rfid.__version__}
        client.publish(MQTT_TOPIC_INFO.format(client._client_id), json.dumps(data))


def parse_args():
    parser = argparse.ArgumentParser(prog=reader_rfid.APP_NAME, description='Reader RFID is client BPINI')
    parser.add_argument('-v', '--version', action='version', version="%(prog)s (version {0})".format(reader_rfid.__version__))

    parser.add_argument('-d', '--debug', dest='debug', action='store_true', default=False, help='Debug mode')
    parser.add_argument('-H', '--host',  default = HOST, help = "Host to connect [default: %(default)s]")
    parser.add_argument('-p', '--port',  type = int, default = PORT, help = "Port to connect [default: %(default)d]")
       
    return parser.parse_args()
            

def main(): 
    args = parse_args()

    log_level = args.debug if logging.DEBUG else logging.INFO
    logging.basicConfig(filename=LOG_FILE, level=log_level, format=LOG_FORMAT)

    client_id = '{0}/{1}'.format('reader_rfid', utils.device_id())
    ledrgb = led_rgb.LedRGB(LED_PINS['red'], LED_PINS['green'], LED_PINS['blue'])
    rfid = rc522.RFID()

    print("---- BPINI - READER RFID ----")
    print("Client ID: {0}".format(client_id))
    logging.info("Client ID: {0}".format(client_id))

    client = mqtt.Client(client_id)
    client.on_message = on_message
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_log = on_log    
    client.user_data_set({'ledrgb': ledrgb})

    while(True):
        try:
            client.connect(args.host, args.port, 60)
            client.loop_start() 
            break
        except Exception as e:
            print(e)
            logging.warning(e)
            time.sleep(2)
        except KeyboardInterrupt:
            sys.exit(0)
       
    while(True):
        try:
            uid = rfid.read_uid()
            if(uid is not None):
                logging.info("Read TAG (UID): {0}".format(uid))

                infot = client.publish(MQTT_TOPIC_TAG.format(client._client_id), json.dumps({'uid': uid}), qos=1, retain=False)
                infot.wait_for_publish()  
            else:
                color = led_rgb.Color(color_hex='#ff0000')
                ledrgb.blink(color)

        except KeyboardInterrupt:
            client.loop_stop()
            client.disconnect()
            break

if __name__ == "__main__":
    main()