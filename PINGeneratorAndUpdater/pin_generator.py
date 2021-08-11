import math, random

def generate_pin():
    digits = "0123456789"
    pin = ""

    i = 0

    while i < 4:
        pin += digits[math.floor(random.random() * 10)]
        i += 1

    return pin