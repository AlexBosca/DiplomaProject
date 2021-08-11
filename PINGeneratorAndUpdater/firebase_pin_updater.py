import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

from pin_generator import generate_pin

from datetime import date

cred = credentials.Certificate("D:/Python/python_firebase/security-system-b4e42-firebase-adminsdk-el8x8-067dfaf95d.json")
firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://security-system-b4e42-default-rtdb.firebaseio.com',
    'databaseAuthVariableOverride': {
        'uid': 'pin_updater'
    }
})

ref = db.reference('/users')
# print(ref.get() + "\n")

users = ref.get()

for key, value in users.items():
    today = str(date.today())
    database_date = ref.child(key).child("pin/date").get()

    if database_date != today:
        ref.child(key).child("pin").update({"value": generate_pin()})
        ref.child(key).child("pin").update({"date": today})

# print(date.today())