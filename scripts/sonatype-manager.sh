#!/bin/bash

# Sonatype Central Portal API Manager
# This script helps manage deployments on Sonatype Central

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

API_BASE_URL="https://central.sonatype.com/api/v1/publisher"

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Function to generate auth token
generate_auth_token() {
    local username="$1"
    local password="$2"
    echo -n "${username}:${password}" | base64
}

# Function to list all deployments
list_deployments() {
    local auth_token="$1"

    print_info "Fetching deployment list..."

    # Note: The API doesn't have a list all endpoint in the docs, but we can check deployments
    # by trying to get status. For now, we'll need the deployment ID.
    print_warning "The API requires a deployment ID to check status."
    print_info "You can find your deployment ID in the Sonatype Central Portal UI:"
    print_info "  https://central.sonatype.com/publishing/deployments"
    echo ""
}

# Function to get deployment status
get_deployment_status() {
    local auth_token="$1"
    local deployment_id="$2"

    print_info "Checking status for deployment: ${deployment_id}"

    response=$(curl --silent --request POST \
        --header "Authorization: Bearer ${auth_token}" \
        "${API_BASE_URL}/status?id=${deployment_id}")

    echo "$response" | jq '.'
}

# Function to drop a deployment
drop_deployment() {
    local auth_token="$1"
    local deployment_id="$2"

    print_info "Dropping deployment: ${deployment_id}"

    # First check the status
    print_info "Checking deployment status first..."
    status_json=$(curl --silent --request POST \
        --header "Authorization: Bearer ${auth_token}" \
        "${API_BASE_URL}/status?id=${deployment_id}")

    state=$(echo "$status_json" | jq -r '.deploymentState')
    name=$(echo "$status_json" | jq -r '.deploymentName')

    echo ""
    print_info "Deployment Name: ${name}"
    print_info "Current State: ${state}"
    echo ""

    if [[ "$state" != "VALIDATED" && "$state" != "FAILED" ]]; then
        print_error "Cannot drop deployment in state: ${state}"
        print_warning "Deployments can only be dropped in VALIDATED or FAILED state"
        print_info "Current state: ${state}"

        if [[ "$state" == "PENDING" || "$state" == "VALIDATING" ]]; then
            print_warning "Wait for validation to complete, then try again"
        fi
        return 1
    fi

    print_warning "Are you sure you want to drop this deployment?"
    read -p "Type 'yes' to confirm: " confirm

    if [[ "$confirm" != "yes" ]]; then
        print_info "Cancelled"
        return 0
    fi

    response=$(curl --silent --request DELETE \
        --write-out "\nHTTP_STATUS:%{http_code}" \
        --header "Authorization: Bearer ${auth_token}" \
        "${API_BASE_URL}/deployment/${deployment_id}")

    http_code=$(echo "$response" | grep "HTTP_STATUS" | cut -d: -f2)

    if [[ "$http_code" == "204" ]]; then
        print_success "Deployment dropped successfully!"
    else
        print_error "Failed to drop deployment. HTTP status: ${http_code}"
        echo "$response" | grep -v "HTTP_STATUS"
        return 1
    fi
}

# Function to publish a deployment
publish_deployment() {
    local auth_token="$1"
    local deployment_id="$2"

    print_info "Publishing deployment: ${deployment_id}"

    # First check the status
    status_json=$(curl --silent --request POST \
        --header "Authorization: Bearer ${auth_token}" \
        "${API_BASE_URL}/status?id=${deployment_id}")

    state=$(echo "$status_json" | jq -r '.deploymentState')

    if [[ "$state" != "VALIDATED" ]]; then
        print_error "Cannot publish deployment in state: ${state}"
        print_warning "Deployment must be in VALIDATED state to publish"
        return 1
    fi

    response=$(curl --silent --request POST \
        --write-out "\nHTTP_STATUS:%{http_code}" \
        --header "Authorization: Bearer ${auth_token}" \
        "${API_BASE_URL}/deployment/${deployment_id}")

    http_code=$(echo "$response" | grep "HTTP_STATUS" | cut -d: -f2)

    if [[ "$http_code" == "204" ]]; then
        print_success "Deployment published successfully!"
        print_info "It will now be uploaded to Maven Central"
    else
        print_error "Failed to publish deployment. HTTP status: ${http_code}"
        echo "$response" | grep -v "HTTP_STATUS"
        return 1
    fi
}

# Main script
show_usage() {
    echo "Usage: $0 <command> <username> <password> [deployment-id]"
    echo ""
    echo "Commands:"
    echo "  list         - List information about deployments (requires manual deployment ID)"
    echo "  status       - Check status of a deployment (requires deployment-id)"
    echo "  drop         - Drop a pending/failed deployment (requires deployment-id)"
    echo "  publish      - Publish a validated deployment (requires deployment-id)"
    echo ""
    echo "Arguments:"
    echo "  username     - Your Sonatype Central username"
    echo "  password     - Your Sonatype Central password (user token)"
    echo "  deployment-id - The deployment ID (UUID)"
    echo ""
    echo "Example:"
    echo "  $0 status myuser mypass 28570f16-da32-4c14-bd2e-c1acc0782365"
    echo "  $0 drop myuser mypass 28570f16-da32-4c14-bd2e-c1acc0782365"
    echo ""
    echo "Get your user token from: https://central.sonatype.com/account"
    echo "Find deployment IDs at: https://central.sonatype.com/publishing/deployments"
}

# Parse arguments
if [[ $# -lt 3 ]]; then
    show_usage
    exit 1
fi

COMMAND="$1"
USERNAME="$2"
PASSWORD="$3"
DEPLOYMENT_ID="${4:-}"

# Generate auth token
AUTH_TOKEN=$(generate_auth_token "$USERNAME" "$PASSWORD")

# Execute command
case "$COMMAND" in
    list)
        list_deployments "$AUTH_TOKEN"
        ;;
    status)
        if [[ -z "$DEPLOYMENT_ID" ]]; then
            print_error "Deployment ID required for status command"
            show_usage
            exit 1
        fi
        get_deployment_status "$AUTH_TOKEN" "$DEPLOYMENT_ID"
        ;;
    drop)
        if [[ -z "$DEPLOYMENT_ID" ]]; then
            print_error "Deployment ID required for drop command"
            show_usage
            exit 1
        fi
        drop_deployment "$AUTH_TOKEN" "$DEPLOYMENT_ID"
        ;;
    publish)
        if [[ -z "$DEPLOYMENT_ID" ]]; then
            print_error "Deployment ID required for publish command"
            show_usage
            exit 1
        fi
        publish_deployment "$AUTH_TOKEN" "$DEPLOYMENT_ID"
        ;;
    *)
        print_error "Unknown command: $COMMAND"
        show_usage
        exit 1
        ;;
esac
