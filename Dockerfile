FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-2.0.1

COPY build/libs/sscs-ccd-callback-orchestrator.jar /opt/app/

CMD ["sscs-ccd-callback-orchestrator.jar"]

EXPOSE 8070
