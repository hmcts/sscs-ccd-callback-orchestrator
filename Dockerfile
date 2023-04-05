ARG APP_INSIGHTS_AGENT_VERSION=3.4.11
FROM hmctspublic.azurecr.io/base/java:11-distroless
COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/sscs-ccd-callback-orchestrator.jar /opt/app/

CMD ["sscs-ccd-callback-orchestrator.jar"]

EXPOSE 8070
