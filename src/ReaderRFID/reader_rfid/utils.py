import uuid

def device_id():
    return hex(uuid.getnode()).rstrip("L").lstrip("0x")

def serial_number():
  
  serial = None
  try:
    f = open('/proc/cpuinfo','r')
    for line in f:
      if line[0:6] == 'Serial':
        serial = line[10:26]
    f.close()
  except:
    serial = None
 
  return serial




def ParseConfigFile(configFilePath):
    parms = configparser.ConfigParser({
		    "id": str(uuid.uuid4()),
		    "domain": "internetofthings.ibmcloud.com",
		    "port": "8883",
		    "type": "standalone",
		    "clean-session": "true"
	    })