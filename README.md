# android_final_project

![alt text](https://github.com/alvarorp19/android_final_project/blob/main/Screenshot_2024-10-31-20-09-37-609_com.microsoft.office.onenote.jpg)

- Desde la main activity (LinesActivity), hacer los elementos del recyclerview clickables (short-click) y pasar como parametro el campo idlinea -> SecondActivity (RouteActivity) descargará y parseará elementos de la url con formato string = "url/" + {idlinea}
- Desde RouteActivity se lanza por trayecto/route una tercera actividad que, como parámetro se le pasa string = "url/" + {idlinea} + "/" {idtrayecto} -> ThirdActivity (StopsActicity) con mapa de paradas (SECUNDARIO), recyclerview de paradas y tiempo de bus a paradas y botones de los autobuses de la ruta para pedir la parada. La parada se realiza presionando el boton virtual correspondiente al bus en el que está el usuario y agitando el telefono 2 veces
- MQTT: Subscribe a noticias con niveles de jerarquía según los menús. Publish solicitud de parada del bus en el que se está viajando. JERARQUÍA DE TÓPICOS STOP: stop/linea/trayecto/bus. JERARQUÍA DE TOPICOS DE NOTICIAS: noticias/lineas y noticias/lineas/trayectos


#Bugs pendientes de corregir

- [X] Comprobar como funciona la app cuando no hay conexion (los botones no funcionan bien)
- [X] hay lineas que no tienen ninguna ruta, arreglar (comprobar que se muestre el mensaje)
- [X] si es de noche no hay datos en el JSON de los trayectos ,corregir
- [X] Mantener el ultimo mensaje de MQTT recibido en la actividad 1 si nos habiamos conectado previamente
- [X] utilizar el buzzer cada vez que se pulsa un boton
- [X] refrescar el reciclerView de la tercera actividad cada 30 segundos
- [ ] mirar porque no refresca bien el mapa
- [X] mirar talkback
- [X] hacer que si se pulsa un elemento del tercer reciclerView no haga nada
