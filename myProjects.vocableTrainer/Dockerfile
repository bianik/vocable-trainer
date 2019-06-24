FROM openjdk:8

ARG tcpPort

ENV DOWNLOAD http://www.h2database.com/h2-2019-03-13.zip
ENV DATA_DIR /opt/h2-data
ENV TCP_PORT $tcpPort

RUN mkdir -p ${DATA_DIR} \
    && curl ${DOWNLOAD} -o h2.zip \
    && unzip h2.zip -d /opt/ \
    && rm h2.zip

EXPOSE 81 $tcpPort

WORKDIR /opt/h2-data

CMD java -cp /opt/h2/bin/h2*.jar org.h2.tools.Server \
 	-web -webAllowOthers -webPort 81 \
 	-tcp -tcpAllowOthers -tcpPort ${TCP_PORT} \
 	-baseDir ${DATA_DIR} \
	-ifNotExists
