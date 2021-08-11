#include <ArduinoJson.h>

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#include <Keypad.h>

#include <String.h>

#include <NTPClient.h>
#include <WiFiUdp.h>



#define WIFI_SSID "SSID of your wifi"
#define WIFI_PASS "password of your wifi"


#define FIREBASE_AUTH "Tzb8jdbcVRGNmzVi7ajYrU5n1jy3I8R2rLahKu6r"
#define FIREBASE_HOST "security-system-b4e42-default-rtdb.firebaseio.com"

const int button = D8;
const int relay = D0;
const byte rows = 4;
const byte cols = 3;

int button_state = 0;

char keys[rows][cols] = {
  {'1', '2', '3'},
  {'4', '5', '6'},
  {'7', '8', '9'},
  {'*', '0', '#'}
};

byte rowPins[rows] = {D1, D2, D3, D4};
byte colPins[cols] = {D5, D6, D7};

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, rows, cols);

int passwordsNum = 0;
String passwords[30];
String password;

int i = 0;  
bool flag = true;   

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, 10800);

String current_date;

void setup() {
 Serial.begin(115200);

 pinMode(relay, OUTPUT);
 digitalWrite(relay,HIGH);
 keypad.addEventListener(keypadEvent);
 delay(2000);

 // Connect to wifi
 WiFi.begin(WIFI_SSID, WIFI_PASS);
 Serial.print("connecting");
 while (WiFi.status() != WL_CONNECTED) {
   Serial.print(".");
   delay(500);
 }
 Serial.println();
 Serial.print("connected: ");
 Serial.println(WiFi.localIP());

 Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

 timeClient.begin();
 current_date = "";  
}

void loop() {
  
  if(current_date != getDate()) {
    current_date = getDate();
    Serial.println(current_date);
    
    updatePINs();
  }

  button_verifing();            
  
  keypad.getKey();              

  if(flag) {
    if(unlock_door()) {           
      digitalWrite(relay,LOW);
      delay(4000);
      digitalWrite(relay,HIGH);
    } else {
      digitalWrite(relay, HIGH);
    }
  }
}

void button_verifing() {
  button_state = digitalRead(button);
  
  if(button_state == 1) {
    digitalWrite(relay,LOW);
    delay(4000);
    digitalWrite(relay,HIGH);
  } else {
    digitalWrite(relay, HIGH);
  }
}

bool unlock_door() {
  FirebaseObject obj = Firebase.get("requests");

  if(Firebase.failed()) {
    Serial.print("getting FirebaseObject failed: ");
    Serial.println(Firebase.error());
    
    return false;
  }
  
  bool request_entering = obj.getBool("entering");
  String request_user = obj.getString("userID");

  if(request_entering){
    Serial.println();
    Serial.print("User with ID: ");
    Serial.print(request_user);
    Serial.println(" just opened the door");

    Firebase.setBool("requests/entering", false);
  
    if(Firebase.failed()) {
      Serial.print("setting requests updated value failed: ");
      Serial.println(Firebase.error());
      
      return false;
    }

    return true;
  } else return false;
}

void keypadEvent(KeypadEvent eKey) {
  switch(keypad.getState()) {
    case PRESSED:
      Serial.print("Pressed: ");
      Serial.println(eKey);
      flag = false;
      switch(eKey) {
        case '*': guessPassword(); flag = true; break;
        case '#': password.remove(0); flag = true; break;
        default: password += eKey;
      }
  }
}

void guessPassword() {
  Serial.println("Guessing password...");

  if(evaluatePassword()) {
    digitalWrite(relay,LOW);
    delay(4000);
    digitalWrite(relay,HIGH);
  } else {
    digitalWrite(relay,HIGH);
  }

  password.remove(0);
}


bool evaluatePassword() {
  for(int i = 0; i < passwordsNum; i++) {
    if(password.compareTo(passwords[i]) == 0) {
      return true;
    }
  }
  
  return false;
}

void updatePINs() {
  FirebaseObject obj = Firebase.get("userIDs");

  if(Firebase.failed()) {
    Serial.print("getting FirebaseObject failed: ");
    Serial.println(Firebase.error());
    return;
  }

  JsonObject& jsonObj = obj.getJsonVariant().as<JsonObject>();
  passwordsNum = jsonObj.size();

  for(int i = 0; i < passwordsNum; i++) {
    int index = i + 1;
    String key = "user";
    key.concat(index);
    
    String userID = obj.getString(key);
    
    if(Firebase.failed()) {
      Serial.print("getting FirebaseObject failed: ");
      Serial.println(Firebase.error());
      return;
    }

    passwords[i] = getPIN(userID);
  }
}

String getPIN(String userID) {
  String path = "users/" + userID + "/pin";
  
  FirebaseObject obj = Firebase.get(path);

  if(Firebase.failed()) {
    Serial.print("getting FirebaseObject failed: ");
    Serial.println(Firebase.error());
    return "a";
  }

  return obj.getString("value");
  
}

String getDate() {
  timeClient.update();

  unsigned long epochTime = timeClient.getEpochTime();
  struct tm *ptm = gmtime((time_t *)&epochTime);

  return String(ptm->tm_year + 1900) + "-"
       + (ptm->tm_mon + 1 < 10 ? ("0" + String(ptm->tm_mon + 1)) :  String(ptm->tm_mon + 1)) + "-"
       + (ptm->tm_mday < 10 ? ("0" + String(ptm->tm_mday)) :  String(ptm->tm_mday));
}
