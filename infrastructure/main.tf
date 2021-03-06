provider "azurerm" {
  features {}
}

locals {
  azureVaultName = "sscs-${var.env}"
}

data "azurerm_key_vault" "sscs_key_vault" {
  name                = local.azureVaultName
  resource_group_name = local.azureVaultName
}
