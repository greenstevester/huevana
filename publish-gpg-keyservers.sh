#!/bin/bash

# Publish GPG public key to keyservers required for Maven Central
# Usage: ./publish-gpg-keyservers.sh [gpg-key-id]

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
    echo "  gpg-key-id   GPG key ID to publish (optional, auto-detected if not provided)"
    echo ""
    echo "Examples:"
    echo "  $0                           # Auto-detect and publish GPG key"
    echo "  $0 ABCD1234EFGH5678          # Publish specific key"
    echo ""
}

GPG_KEY_ID="${1:-}"

echo "=================================================="
echo "  GPG Keyserver Publication"
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
        print_error "Please specify which key to publish:"
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

# Check if key has already been published
print_info "Checking if key is already published..."
ALREADY_PUBLISHED=false

for entry in "keyserver.ubuntu.com:hkps" "keys.openpgp.org:hkps"; do
    keyserver="${entry%%:*}"
    protocol="${entry##*:}"

    if gpg --keyserver "${protocol}://${keyserver}" --recv-keys "$GPG_KEY_ID" >/dev/null 2>&1; then
        ALREADY_PUBLISHED=true
        print_warning "Key is already published on $keyserver"
        break
    fi
done

if [ "$ALREADY_PUBLISHED" = true ]; then
    echo ""
    print_warning "Your key has already been distributed to keyservers."
    echo ""
    echo "📌 Important: If you need to modify subkeys:"
    echo ""
    echo "  ✓ Use 'revkey' to REVOKE subkeys (recommended)"
    echo "    - Revocation is propagated to keyservers"
    echo "    - Others will know the key is no longer valid"
    echo ""
    echo "  ✗ Do NOT use 'delkey' to DELETE subkeys"
    echo "    - Deletion is NOT propagated"
    echo "    - Old copies remain on keyservers"
    echo ""
    echo "To revoke a subkey:"
    echo "  1. gpg --edit-key $GPG_KEY_ID"
    echo "  2. key N    # Select subkey number N"
    echo "  3. revkey   # Revoke the selected subkey"
    echo "  4. save     # Save and exit"
    echo "  5. Re-run this script to publish revocation"
    echo ""
fi

# Confirm publication
read -p "Publish this key to all keyservers? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    print_info "Publication cancelled."
    exit 0
fi

echo ""
print_info "Publishing key to keyservers..."
echo ""

# Define keyservers (works with bash 3.2+)
KEYSERVERS=(
    "keyserver.ubuntu.com:hkps"
    "keys.openpgp.org:hkps"
    "pgp.mit.edu:hkp"
)

# Track success/failure
all_success=true
openpgp_published=false

# Publish to each keyserver
for entry in "${KEYSERVERS[@]}"; do
    keyserver="${entry%%:*}"
    protocol="${entry##*:}"

    printf "%-30s " "$keyserver:"

    if gpg --keyserver "${protocol}://${keyserver}" --send-keys "$GPG_KEY_ID" >/dev/null 2>&1; then
        print_success "Published"
        if [ "$keyserver" = "keys.openpgp.org" ]; then
            openpgp_published=true
        fi
    else
        print_error "Failed"
        all_success=false
    fi
done

echo ""
echo "=================================================="

if [ "$all_success" = true ]; then
    print_success "Key published to all keyservers!"
    echo ""

    if [ "$openpgp_published" = true ]; then
        print_warning "IMPORTANT: keys.openpgp.org requires email verification"
        echo ""
        echo "1. Check your email: $GPG_KEY_INFO"
        echo "2. Click the verification link in the email"
        echo "3. Your key will be public after verification"
        echo ""
    fi

    echo "Next steps:"
    echo ""
    echo "  1. Wait 5-10 minutes for keyserver propagation"

    if [ "$openpgp_published" = true ]; then
        echo "  2. Verify your email for keys.openpgp.org"
        echo "  3. Verify publication with:"
    else
        echo "  2. Verify publication with:"
    fi

    echo "     ./verify-gpg-keyservers.sh"
    echo ""

    exit 0
else
    print_warning "Some keyservers failed"
    echo ""
    echo "You can try publishing individually:"
    echo ""

    for entry in "${KEYSERVERS[@]}"; do
        keyserver="${entry%%:*}"
        protocol="${entry##*:}"
        echo "  gpg --keyserver ${protocol}://${keyserver} --send-keys $GPG_KEY_ID"
    done

    echo ""
    echo "Common issues:"
    echo "  - Network connectivity problems"
    echo "  - Keyserver temporarily unavailable"
    echo "  - Firewall blocking keyserver ports"
    echo ""
    echo "After fixing, verify with:"
    echo "  ./verify-gpg-keyservers.sh"
    echo ""

    exit 1
fi
