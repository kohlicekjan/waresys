import uuid


def device_id():
    """ Vrací MAC adresu ve formátu hex codu """
    return hex(uuid.getnode()).rstrip("L").lstrip("0x")


def serial_number():
    """ Vrací seriové číslo Raspbbery Pi """
    serial = None
    try:
        f = open("/proc/cpuinfo", "r")
        for line in f:
            if line[0:6] == "Serial":
                serial = line[10:26]
        f.close()
    except:
        serial = None

    return serial
