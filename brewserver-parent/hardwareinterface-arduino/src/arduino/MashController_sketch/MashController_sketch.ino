#include <OneWire.h>
#include <DallasTemperature.h>

#define RelayPin 6
#define ONE_WIRE_BUS 8

OneWire ds(ONE_WIRE_BUS);
DallasTemperature sensors(&ds);

double currentTemp;
double desiredTemp;

int tempSensorCount;

boolean manual;

boolean heatingOn;

double delta;

void setup()
{
         delta = 0.0;
	 Serial.begin(9600);
         Serial.println("Started");
         sensors.begin();
	 sensors.requestTemperatures();
         tempSensorCount = sensors.getDeviceCount();
         Serial.print("Found ");
         Serial.print(tempSensorCount);
         Serial.println(" temperature sensors");
         
         manual=true;
         heaterOff();
}

void loop()
{
  readSerial();
  if(tempSensorCount==0){
    Serial.println("WARNING: No Temp sensors found");
    heaterOff();
    return;
  }
  updateTemp();
  if(manual){
    return; 
  }
  if(currentTemp>=(desiredTemp-delta)){
    heaterOff();
  } else {
    heaterOn(); 
  }
  
  if(heatingOn==false && currentTemp>desiredTemp){
    float currentDelta = currentTemp-desiredTemp;
    if(currentDelta > delta){
       delta=currentDelta; 
    }
  }
  delay(1000);
}

void heaterOn(){
  Serial.println("Heater ON");
  digitalWrite(RelayPin,HIGH);
  heatingOn=true;
}

void heaterOff(){
  Serial.println("Heater OFF");
  digitalWrite(RelayPin,LOW);
  heatingOn=false;
}

void updateTemp(){
  float totalTemp=0;
  for(int i=0;i<tempSensorCount;i++){
    float measuredTemp = sensors.getTempCByIndex(i);
    totalTemp+=measuredTemp;
    Serial.print("Temp");
    Serial.print(i);
    Serial.print(":");
    Serial.println(measuredTemp);
  }
  currentTemp=totalTemp/tempSensorCount;
  Serial.print("Temp Average:");
  Serial.println(currentTemp);
  sensors.requestTemperatures();
}

/**
Send T for Target temp eg T65
*/
void readSerial(){
  char inData[64];
  int index=0;
  while(Serial.available()>0){
    inData[index]=Serial.read();
    index++;
    inData[index]='\0';
  }
  if(inData[0]=='T'){
    char temp[3];
    temp[0]=inData[1];
    temp[1]=inData[2];
    temp[2]='\0';
    desiredTemp=atof(temp);
    manual = false;
   Serial.print("New Target tenperature ");
  } else if(inData[0]=='C'){
    // Start cooking
    manual=true;
    heaterOn(); 
  } else if(inData[0]=='O'){
    manual=true;
    heaterOff(); 
  }
}
