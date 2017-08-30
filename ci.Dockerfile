FROM openjdk:8-jdk

WORKDIR /code

ADD build/dist/edustor-pdfgen.jar .

CMD java -jar edustor-pdfgen.jar