#include <Arduino.h>
#include <PubSubClient.h>
#include <WiFi.h>

//credenciales WiFi
const char* ssid = "cotemax_cbb";
const char* password = "cote0d32";

//Credenciales MQTT
const char *mqtt_server = "192.168.1.35";
const int mqtt_port = 1883;
const char *mqtt_user = "DESKTOP";
const char *mqtt_pass = "";

//Declaro clases
WiFiClient espClient;
PubSubClient client(espClient);

//Buffer
long lastMsg = 0;
char msg[50];
int value = 0;
char* luz1 = "luz1";
char* luz2 = "luz2";
char* luz3 = "luz3";
// callback para recibir mensajes del servidor mqtt e imprimir la salida
void callback (char* topic, byte* payload, unsigned int length){
  Serial.print("Mensaje recibido desde: ");
  Serial.print(topic);
  Serial.print("\n");

  for (int i = 0; i < length; i++){
    Serial.print((char)payload[i]);
  }

  if (strcmp(topic,luz1)==0){
    if ((char)payload[0]=='0'){
    digitalWrite(25,LOW);
    Serial.println("\n Luz 1 apagada");
    }
    else if((char)payload[0]=='1'){
    digitalWrite(25,HIGH);
    Serial.println("\n Luz 1 encendida");
    }
  }
  else if (strcmp(topic,luz2)==0){
    if ((char)payload[0]=='0'){
    digitalWrite(26,LOW);
    Serial.println("\n Luz 2 apagada");
    }
    else if((char)payload[0]=='1'){
    digitalWrite(26,HIGH);
    Serial.println("\n Luz 2 encendida");
    }
  }
  else if (strcmp(topic,luz3)==0){
    
    if ((char)payload[0]=='0'){
    digitalWrite(27,LOW);
    Serial.println("\n Luz 3 apagada");
    }
    else if((char)payload[0]=='1'){
    digitalWrite(27,HIGH);
    Serial.println("\n Luz 1 encendida");
    }
  }
  else{
    Serial.println("else");
  }
  Serial.println();
}

void reconnect(){
  while (!client.connected()){
    Serial.println("Intentando conexion MQTT");

    String clientId="iot_1_";
    clientId = clientId + String(random(0xffff), HEX);

    if(client.connect(clientId.c_str(),mqtt_user,mqtt_pass)){
      Serial.println("Conexion exitosa!");
      client.publish("salida","primer mensaje");
      client.subscribe("entrada");
      client.subscribe("luz1");
      client.subscribe("luz2");      
      client.subscribe("luz3");
    }else{
      Serial.println("Fallo la conexion");
      Serial.println(client.state());
      Serial.print("Reintentando en 5 seg");
      delay(5000);
    }
  }
}

//Funcion para conectarse al wifi
void connect_wifi(){

  Serial.println();
  Serial.println("Conectando a...");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.println(".");
  }

  Serial.println("");
  Serial.println("Conectado a la red WiFi");
  Serial.println("Direccion IP: ");
  Serial.println(WiFi.localIP());
}

void setup() {
pinMode(BUILTIN_LED, OUTPUT);
pinMode(25, OUTPUT);
pinMode(26, OUTPUT);
pinMode(27, OUTPUT);
Serial.begin(115200);
connect_wifi();
client.setServer(mqtt_server,mqtt_port);
client.setCallback(callback);
}

void loop() {

  //intenta connectarse al servidor MQTT si pierde la conexion
  if(!client.connected()){
    reconnect();
  }

  client.loop();

  if(millis() - lastMsg > 2000){
    lastMsg = millis();
    value++;
    String mes = "Valor --> " + String (value);
    mes.toCharArray(msg, 50);
    client.publish("salida","msg");
    Serial.println("mensaje enviado ->" + String (value));
  }
}