FROM openjdk:8-jre-slim as builder
RUN apt-get update && \
    apt-get install -y --no-install-recommends apt-transport-https apt-utils bc dirmngr gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends sbt
COPY . /cardano-analytics-scala
WORKDIR /cardano-analytics-scala
RUN sbt cardano-db-writer/assembly
RUN mv `find . -name indexes-writer.jar` /indexes-writer.jar
CMD ["/usr/bin/java", "-jar", "/indexes-writer.jar"]

FROM openjdk:8-jre-slim

ENV LOG_CONFIG "/cardano-analytics-scala/conf/logback.xml"
ENV ENV_CONFIG "/cardano-analytics-scala/conf/conf.env"

COPY --from=builder /indexes-writer.jar /indexes-writer.jar
ENTRYPOINT java -Xms1G -Xmx2G -Dlogback.configurationFile=${LOG_CONFIG} -Duser.timezone=Europe/Moscow -jar /indexes-writer.jar ${ENV_CONFIG}