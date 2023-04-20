#!/bin/bash

# Gitpod-enhanced has some ENHANCED issues.

source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install java 17.0.6-tem
sdk use java 17.0.6-tem
sdk install gradle 8.1
sdk use gradle 8.1