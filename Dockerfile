ARG APP_INSIGHTS_AGENT_VERSION=3.4.13
FROM hmctspublic.azurecr.io/base/java:21-distroless-debug
COPY lib/applicationinsights.json /opt/app/
COPY build/libs/sscs-ccd-callback-orchestrator.jar /opt/app/

CMD ["sscs-ccd-callback-orchestrator.jar"]

EXPOSE 8070
