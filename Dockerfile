FROM java:8-jdk

WORKDIR /code
ADD . /code

RUN ./gradlew build
RUN mv build/dist/edustor-pdfgen.jar .

CMD java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar edustor-pdfgen.jar