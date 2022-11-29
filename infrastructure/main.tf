provider "azurerm" {
  features {}
}

locals {
  azureVaultName = "sscs-${var.env}"
  tags = (merge(
    var.common_tags,
    tomap({
      "Team Contact" = "#sscs"
      "Team Name"    = "SSCS Team"
    })
  ))
}

data "azurerm_key_vault" "sscs_key_vault" {
  name                = local.azureVaultName
  resource_group_name = local.azureVaultName
}
