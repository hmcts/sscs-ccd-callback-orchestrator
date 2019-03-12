provider "azurerm" {}

locals {
  ase_name       = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  azureVaultName = "sscs-${var.env}"
  s2sCnpUrl     = "http://rpe-service-auth-provider-${var.env}.service.${local.ase_name}.internal"

  shared_app_service_plan     = "${var.product}-${var.env}"
  non_shared_app_service_plan = "${var.product}-${var.component}-${var.env}"
  app_service_plan            = "${(var.env == "saat" || var.env == "sandbox") ?  local.shared_app_service_plan : local.non_shared_app_service_plan}"
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = "${var.location_app}"

  tags = "${merge(var.common_tags,
    map("lastUpdated", "${timestamp()}")
    )}"
}

data "azurerm_key_vault" "sscs_key_vault" {
  name                = "${local.azureVaultName}"
  resource_group_name = "${local.azureVaultName}"
}

data "azurerm_key_vault_secret" "idam_api" {
  name      = "idam-api"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "sscs_s2s_secret" {
  name      = "sscs-s2s-secret"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "idam_sscs_systemupdate_user" {
  name      = "idam-sscs-systemupdate-user"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "idam_sscs_systemupdate_password" {
  name      = "idam-sscs-systemupdate-password"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "idam_oauth2_client_secret" {
  name      = "idam-sscs-oauth2-client-secret"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "sscs_asb_primary_send_and_listen_shared_access_key" {
  name      = "sscs-asb-primary-send-and-listen-shared-access-key"
  vault_uri = "${data.azurerm_key_vault.sscs_key_vault.vault_uri}"
}

module "sscs-ccd-callback-orchestrator" {
  source              = "git@github.com:hmcts/moj-module-webapp?ref=master"
  product             = "${var.product}-${var.component}"
  location            = "${var.location_app}"
  env                 = "${var.env}"
  ilbIp               = "${var.ilbIp}"
  resource_group_name = "${azurerm_resource_group.rg.name}"
  subscription        = "${var.subscription}"
  capacity            = "${var.capacity}"
  common_tags         = "${var.common_tags}"

  app_settings = {
    LOGBACK_REQUIRE_ALERT_LEVEL = "${var.logback_require_alert_level}"
    LOGBACK_REQUIRE_ERROR_CODE  = "${var.logback_require_error_code}"

    IDAM_API_URL = "${data.azurerm_key_vault_secret.idam_api.value}"

    IDAM.S2S-AUTH.TOTP_SECRET  = "${data.azurerm_key_vault_secret.sscs_s2s_secret.value}"
    IDAM.S2S-AUTH              = "${local.s2sCnpUrl}"
    IDAM.S2S-AUTH.MICROSERVICE = "${var.ccd_idam_s2s_auth_microservice}"

    IDAM_SSCS_SYSTEMUPDATE_USER     = "${data.azurerm_key_vault_secret.idam_sscs_systemupdate_user.value}"
    IDAM_SSCS_SYSTEMUPDATE_PASSWORD = "${data.azurerm_key_vault_secret.idam_sscs_systemupdate_password.value}"

    IDAM_OAUTH2_CLIENT_ID     = "${var.idam_oauth2_client_id}"
    IDAM_OAUTH2_CLIENT_SECRET = "${data.azurerm_key_vault_secret.idam_oauth2_client_secret.value}"
    IDAM_OAUTH2_REDIRECT_URL  = "${var.idam_redirect_url}"

    AMQP_HOST         = "sscs-servicebus-${var.env}.servicebus.windows.net"
    // In Azure Service bus, rulename/key is used as username/password
    AMQP_USERNAME     = "SendAndListenSharedAccessKey"
    AMQP_PASSWORD     = "${data.azurerm_key_vault_secret.sscs_asb_primary_send_and_listen_shared_access_key.value}"
    TOPIC_NAME        = "evidenceshare"
    SUBSCRIPTION_NAME = "${data.azurerm_key_vault_secret.idam_oauth2_client_secret.value}"

    TRUST_ALL_CERTS   = "${var.trust_all_certs}"
  }
}
