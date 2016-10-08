import paho.mqtt.client as mqtt
#pip install paho-mqtt
mqttc = mqtt.Client()

def on_connect(client, userdata, flags, rc):
    print('CONNACK received with code %d.' % (rc))
 
client = paho.Client()
client.on_connect = on_connect
client.connect('broker.mqttdashboard.com', 1883)

paho.Client(client_id='', clean_session=True, userdata=None, protocol=paho.MQTTv31)

client.connect(host='localhost', port=1883, keepalive=60, bind_address='')