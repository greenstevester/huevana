#!/bin/bash

# Verify GPG public key is published to keyservers required for Maven Central
# Usage: ./verify-gpg-keyservers.sh [gpg-key-id]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Show usage
show_usage() {
    echo "Usage: $0 [gpg-key-id]"
    echo ""
    echo "Arguments:"
    echo "  gpg-key-id   GPG key ID to verify (optional, auto-detected if not provided)"
    echo ""
    echo "Examples:"
    echo "  $0                           # Auto-detect GPG key"
    echo "  $0 ABCD1234EFGH5678          # Verify specific key"
    echo ""
}

GPG_KEY_ID="${1:-}"

echo "=================================================="
echo "  GPG Keyserver Verification"
echo "=================================================="
echo ""

# Detect or validate GPG key
if [ -z "$GPG_KEY_ID" ]; then
    print_info "Auto-detecting GPG key..."

    GPG_KEYS=$(gpg --list-secret-keys --keyid-format=long 2>/dev/null | grep "^sec" | awk '{print $2}' | cut -d'/' -f2)
    KEY_COUNT=$(echo "$GPG_KEYS" | grep -c "^" || true)

    if [ "$KEY_COUNT" -eq 0 ]; then
        print_error "No GPG keys found!"
        echo ""
        echo "Generate a new key:"
        echo "  gpg --full-generate-key"
        echo ""
        exit 1
    elif [ "$KEY_COUNT" -eq 1 ]; then
        GPG_KEY_ID="$GPG_KEYS"
        print_success "Auto-detected GPG key: $GPG_KEY_ID"
    else
        print_warning "Multiple GPG keys found:"
        echo ""
        gpg --list-secret-keys --keyid-format=long | grep -A 1 "^sec"
        echo ""
        print_error "Please specify which key to verify:"
        echo "  $0 <key-id>"
        echo ""
        echo "Example:"
        echo "  $0 ${GPG_KEYS%% *}"
        exit 1
    fi
else
    # Validate provided GPG key
    if ! gpg --list-secret-keys --keyid-format=long 2>/dev/null | grep -q "$GPG_KEY_ID"; then
        print_error "GPG key not found: $GPG_KEY_ID"
        echo ""
        echo "Available keys:"
        gpg --list-secret-keys --keyid-format=long
        exit 1
    fi
    print_success "Using specified GPG key: $GPG_KEY_ID"
fi

echo ""

# Get key details for display
GPG_KEY_INFO=$(gpg --list-secret-keys --keyid-format=long "$GPG_KEY_ID" 2>/dev/null | grep "^uid" | sed 's/uid.*\] //')

echo "Key ID:      $GPG_KEY_ID"
echo "Identity:    $GPG_KEY_INFO"
echo ""

# Define keyservers
KEYSERVERS=(
    "keyserver.ubuntu.com"
    "keys.openpgp.org"
    "pgp.mit.edu"
)

print_info "Checking keyservers..."
echo ""

# Check each keyserver
all_found=true

for keyserver in "${KEYSERVERS[@]}"; do
    printf "%-30s " "$keyserver:"

    # Try to receive the key (this checks if it exists)
    if gpg --keyserver "hkps://$keyserver" --recv-keys "$GPG_KEY_ID" >/dev/null 2>&1; then
        print_success "Found"
    else
        # Try without hkps (some servers might not support it)
        if gpg --keyserver "hkp://$keyserver" --recv-keys "$GPG_KEY_ID" >/dev/null 2>&1; then
            print_success "Found"
        else
            print_error "Not found"
            all_found=false
        fi
    fi
done

echo ""
echo "=================================================="

if [ "$all_found" = true ]; then
    print_success "All keyservers have your public key!"
    echo ""
    echo "Your key is properly distributed for Maven Central deployment."
    echo ""
    exit 0
else
    print_warning "Some keyservers do not have your public key"
    echo ""
    echo "To publish your key to all keyservers:"
    echo ""
    echo "  gpg --keyserver hkps://keyserver.ubuntu.com --send-keys $GPG_KEY_ID"
    echo "  gpg --keyserver hkps://keys.openpgp.org --send-keys $GPG_KEY_ID"
    echo "  gpg --keyserver hkp://pgp.mit.edu --send-keys $GPG_KEY_ID"
    echo ""
    echo "Note: keys.openpgp.org requires email verification:"
    echo "  1. Upload key: gpg --keyserver hkps://keys.openpgp.org --send-keys $GPG_KEY_ID"
    echo "  2. Check your email for verification link"
    echo "  3. Click the link to verify your identity"
    echo ""
    echo "After publishing, wait 5-10 minutes for propagation, then run this script again."
    echo ""
    exit 1
fi
