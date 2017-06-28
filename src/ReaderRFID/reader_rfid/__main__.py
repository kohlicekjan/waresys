import argparse
import logging
import json
import time
import sys
#from threading import Thread

import paho.mqtt.client as mqtt
import rc522
import led_rgb
import utils
from __init__ import __version__, APP_NAME, HOST, PORT, LED_PINS, MQTT_TOPIC, LOG_FILE, LOG_FORMAT


def message_led(client, userdata, msg):
    """ zpracovává zprávy na téma /led """

    data = json.loads(msg.payload.decode('utf-8'))

    try:
        #kontrola formatu zprávy
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

    #vypne diodu
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

        #odešle se žádost o odběr
        client.subscribe(MQTT_TOPIC['LED'].format(client._client_id), 0)
        #nastaví fukce pro zpracování zprávy na téma /led
        client.message_callback_add(MQTT_TOPIC['LED'].format(client._client_id), message_led)   

        #odešlou se informace o zařízení
        data = {'serial_number': utils.serial_number(), 'version': __version__}
        client.publish(MQTT_TOPIC['INFO'].format(client._client_id), json.dumps(data))


def parse_args():
    parser = argparse.ArgumentParser(prog=APP_NAME, description='Reader RFID is client BPINI')
    parser.add_argument('-v', '--version', action='version', version="%(prog)s (version {0})".format(__version__))

    parser.add_argument('-d', '--debug', dest='debug', action='store_true', default=False, help='Debug mode')
    parser.add_argument('-H', '--host', dest="host", default=HOST, help="MQTT host to connect to [default: %(default)d]")
    parser.add_argument('-p', '--port', dest="port",  type=int, default=PORT, help="Port for remote MQTT host [default: %(default)d]")
       
    return parser.parse_args()
          

#def send_info(client):
#    while not client.is_stop:
#        time.sleep(1.5)
#        data = {'serial_number': utils.serial_number(), 'version': __version__}
#        client.publish(MQTT_TOPIC['INFO'].format(client._client_id), json.dumps(data))


def main(): 
    args = parse_args()

    log_level = args.debug if logging.DEBUG else logging.INFO
    logging.basicConfig(filename=LOG_FILE, level=log_level, format=LOG_FORMAT)

    #vytvoření klient ID
    client_id = '{0}/{1}'.format(APP_NAME, utils.device_id())
    ledrgb = led_rgb.LedRGB(LED_PINS['RED'], LED_PINS['GREEN'], LED_PINS['BLUE'])
    rfid = rc522.RFID()

    print("---- BPINI - READER RFID ----")
    print("Client ID: {0}".format(client_id))
    logging.info("Client ID: {0}".format(client_id))

    #vytvoření MQTT klienta
    client = mqtt.Client(client_id)
    client.on_message = on_message
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_log = on_log    
    client.user_data_set({'ledrgb': ledrgb})

    #bude se pokoušet připojit dokud se to nepovede
    while True:
        try:
            time.sleep(2)
            client.connect(args.host, args.port, 60)
            client.loop_start() 
            break
        except Exception as e:
            print(e)
            logging.warning(e)     
        except KeyboardInterrupt:
            rfid.cleanup()
            sys.exit(0)
       

    #client.is_stop=False
    #t = Thread(target=send_info, args=(client,))
    #t.start()


    while(True):
        try:
            #načtení UID tagu
            uid = rfid.read_uid()
            if(uid is not None):
                #převedení UID na format hex code
                uid_hex = ''.join('{0:02x}'.format(uid[x]) for x in range(4))
                logging.info("Read TAG UID: {0}".format(uid_hex))
                
                #odeslání zpravy s UID
                infot = client.publish(MQTT_TOPIC['TAG'].format(client._client_id), json.dumps({'uid': uid_hex}))
                infot.wait_for_publish()

        except KeyboardInterrupt:
            #client.is_stop=True
            #t.join()            
            client.loop_stop()
            client.disconnect()
            rfid.cleanup()
            break

if __name__ == "__main__":
    main()