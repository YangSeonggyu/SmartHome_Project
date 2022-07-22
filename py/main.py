import firebase_admin
from firebase_admin import db
from firebase_admin import credentials
from gpiozero import LED
import RPi.GPIO as GPIO

cred = credentials.Certificate('smarthome-4f0f4-firebase-adminsdk-fjnqq-27d866e2e0.json')
firebase_admin.initialize_app(cred,{
    'databaseURL' : 'https://smarthome-4f0f4-default-rtdb.firebaseio.com/'
})
# bedroom
bedroom_led = LED(3)
bedroom_tv = LED(4)
# livingroom
livingroom_led = LED(17)
livingroom_tv = LED(18)
# kitchen
kitchen_led = LED(2)

# motor
Stop = 0
Start = 1
OUTPUT = 1
INPUT = 0
HIGH = 1
LOW = 0
ENA = 26
IN1 = 19
IN2 = 13
GPIO.setmode(GPIO.BCM)
GPIO.setup(ENA,GPIO.OUT)
GPIO.setup(IN1,GPIO.OUT)
GPIO.setup(IN2,GPIO.OUT)
pwm = GPIO.PWM(ENA,100)
pwm.start(0)

def setMotorContorl(speed,stat):
    pwm.ChangeDutyCycle(speed)
    if stat == Start:
        GPIO.output(IN1,HIGH)
        GPIO.output(IN2,LOW)
    else:
        GPIO.output(IN1,LOW)
        GPIO.output(IN2,LOW)
        
while True:
    # db reference
    bedroom_led_db = db.reference('bedroom_led')
    livingroom_led_db = db.reference('livingroom_led')
    kitchen_led_db = db.reference('kitchen_led')
    bedroom_motor_db = db.reference('bedroom_motor')
    livingroom_motor_db = db.reference('livingroom_motor')
    kitchen_motor_db = db.reference('kitchen_motor')
    bedroom_auto_db = db.reference('bedroom_auto')
    livingroom_auto_db = db.reference('livingroom_auto')
    kitchen_auto_db = db.reference('kitchen_auto')
    bedroom_lcd_db = db.reference('bedroom_lcd')
    livingroom_lcd_db = db.reference('livingroom_lcd')
    kitchen_lcd_db = db.reference('kitchen_lcd')
    # led_set
    if bedroom_led_db.get() == True:
        bedroom_led.on()
    else:
        bedroom_led.off()
    if livingroom_led_db.get() == True:
        livingroom_led.on()
    else:
        livingroom_led.off()
    if kitchen_led_db.get() == True:
       kitchen_led.on()
    else:
       kitchen_led.off()
    # tv_set
    if bedroom_lcd_db.get() == True:
        bedroom_tv.on()
    else:
        bedroom_tv.off()
    if livingroom_lcd_db.get() == True:
        livingroom_tv.on()
    else:
        livingroom_tv.off()
    # motor_set
    if livingroom_motor_db.get() == True:
        setMotorContorl(80,Start)
    else:
        setMotorContorl(80,Stop)
    