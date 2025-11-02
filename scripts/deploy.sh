#!/bin/bash

# Deploy huevana to Maven Central
# This script must be run from a real terminal (not Claude Code)

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
    echo "Usage: $0 <version> [gpg-key-id]"
    echo ""
    echo "Arguments:"
    echo "  version      Release version (required)"
    echo "  gpg-key-id   GPG key ID to use for signing (optional, auto-detected if not provided)"
    echo ""
    echo "Examples:"
    echo "  $0 5.2.0                           # Deploy version 5.2.0 with auto-detected GPG key"
    echo "  $0 5.2.0 ABCD1234                  # Deploy with specific GPG key"
    echo "  $0 5.3.0-RC1                       # Deploy release candidate"
    echo ""
}

# Check if version argument is provided
if [ -z "$1" ]; then
    print_error "Version argument required!"
    echo ""
    show_usage
    exit 1
fi

VERSION="$1"
GPG_KEY_ID="${2:-}"

# Validate version format (X.Y.Z or X.Y.Z-suffix)
if [[ ! $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$ ]]; then
    print_error "Invalid version format: $VERSION"
    echo ""
    echo "Expected format: X.Y.Z or X.Y.Z-suffix"
    echo ""
    echo "Valid examples:"
    echo "  5.2.0"
    echo "  5.3.0-RC1"
    echo "  1.0.0-beta"
    echo ""
    exit 1
fi

TAG_NAME="v$VERSION"

echo "=================================================="
echo "  huevana $VERSION Maven Central Deployment"
echo "=================================================="
echo ""

# Check if we're in the correct directory
if [ ! -f "pom.xml" ]; then
    print_error "Not in project root directory (pom.xml not found)"
    echo ""
    echo "Please run this script from the huevana project root:"
    echo "  cd /<your project path>/hue/huevana"
    echo "  ./deploy.sh $VERSION"
    echo ""
    exit 1
fi

# Check if it's a git repository
if [ ! -d ".git" ]; then
    print_error "Not a git repository"
    exit 1
fi

print_info "Checking git status..."

# Check if working directory is clean
if ! git diff-index --quiet HEAD --; then
    print_error "Working directory has uncommitted changes"
    echo ""
    git status --short
    echo ""
    echo "Please commit or stash your changes before deploying:"
    echo "  git status"
    echo "  git add ."
    echo "  git commit -m \"your message\""
    echo ""
    exit 1
fi

print_success "Working directory is clean"

# Check if tag exists locally
print_info "Checking if tag $TAG_NAME exists locally..."
if git rev-parse "$TAG_NAME" >/dev/null 2>&1; then
    print_error "Tag $TAG_NAME already exists locally"
    echo ""
    echo "Local tag details:"
    git show --no-patch --format="%ci - %s" "$TAG_NAME"
    echo ""
    echo "If you want to re-release:"
    echo "  1. Delete local tag:  git tag -d $TAG_NAME"
    echo "  2. Delete remote tag: git push origin :refs/tags/$TAG_NAME"
    echo "  3. Run this script again"
    echo ""
    exit 1
fi

print_success "Tag $TAG_NAME does not exist locally"

# Check if tag exists on remote
print_info "Checking if tag $TAG_NAME exists on remote..."
if git ls-remote --tags origin | grep -q "refs/tags/$TAG_NAME$"; then
    print_error "Tag $TAG_NAME already exists on remote"
    echo ""
    echo "Remote tag exists at:"
    git ls-remote --tags origin | grep "refs/tags/$TAG_NAME"
    echo ""
    echo "If you want to re-release:"
    echo "  1. Delete remote tag: git push origin :refs/tags/$TAG_NAME"
    echo "  2. Delete local tag:  git tag -d $TAG_NAME (if exists)"
    echo "  3. Run this script again"
    echo ""
    exit 1
fi

print_success "Tag $TAG_NAME does not exist on remote"

# Check if version already exists on Maven Central
print_info "Checking if version $VERSION exists on Maven Central..."

MAVEN_CENTRAL_URL="https://repo1.maven.org/maven2/io/github/greenstevester/huevana/$VERSION/"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$MAVEN_CENTRAL_URL")

if [ "$HTTP_STATUS" = "200" ]; then
    print_error "Version $VERSION already exists on Maven Central!"
    echo ""
    echo "Maven Central URL: $MAVEN_CENTRAL_URL"
    echo ""
    print_warning "This version has already been published and cannot be re-released."
    echo ""
    echo "If you need to publish a new version:"
    echo "  1. Increment the version number (e.g., 5.2.1)"
    echo "  2. Run: ./deploy.sh 5.2.1"
    echo ""
    echo "If you believe this is an error, check manually:"
    echo "  curl -I $MAVEN_CENTRAL_URL"
    echo ""
    exit 1
fi

print_success "Version $VERSION does not exist on Maven Central"

# Check current version in pom.xml
print_info "Checking version in pom.xml..."
CURRENT_VERSION=$(grep -m1 "<version>" pom.xml | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | xargs)

echo ""
echo "Current pom.xml version: $CURRENT_VERSION"
echo "Target release version:  $VERSION"
echo ""

if [ "$CURRENT_VERSION" != "$VERSION" ]; then
    print_warning "Version mismatch detected!"
    echo ""
    read -p "Update pom.xml from $CURRENT_VERSION to $VERSION? (yes/no): " update_version

    if [ "$update_version" = "yes" ]; then
        print_info "Updating version in pom.xml..."
        mvn versions:set -DnewVersion="$VERSION" -DgenerateBackupPoms=false

        if [ $? -eq 0 ]; then
            print_success "Version updated to $VERSION"

            # Commit the version change
            git add pom.xml
            git commit -m "chore: bump version to $VERSION for release"
            print_success "Version change committed"
        else
            print_error "Failed to update version"
            exit 1
        fi
    else
        print_error "Version mismatch not resolved. Please update manually:"
        echo "  mvn versions:set -DnewVersion=$VERSION"
        exit 1
    fi
fi

# Set GPG_TTY for proper passphrase prompting
export GPG_TTY=$(tty)

# Detect or validate GPG key
print_info "Detecting GPG signing key..."

if [ -z "$GPG_KEY_ID" ]; then
    # Auto-detect GPG key
    GPG_KEYS=$(gpg --list-secret-keys --keyid-format=long 2>/dev/null | grep "^sec" | awk '{print $2}' | cut -d'/' -f2)
    KEY_COUNT=$(echo "$GPG_KEYS" | grep -c "^" || true)

    if [ "$KEY_COUNT" -eq 0 ]; then
        print_error "No GPG keys found!"
        echo ""
        echo "You need a GPG key to sign Maven artifacts."
        echo ""
        echo "Generate a new key:"
        echo "  gpg --full-generate-key"
        echo ""
        echo "Then run this script again."
        exit 1
    elif [ "$KEY_COUNT" -eq 1 ]; then
        GPG_KEY_ID="$GPG_KEYS"
        print_success "Auto-detected GPG key: $GPG_KEY_ID"
    else
        print_warning "Multiple GPG keys found:"
        echo ""
        gpg --list-secret-keys --keyid-format=long | grep -A 1 "^sec"
        echo ""
        print_error "Please specify which key to use:"
        echo "  $0 $VERSION <key-id>"
        echo ""
        echo "Example:"
        echo "  $0 $VERSION ${GPG_KEYS%% *}"
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

# Test GPG signing
print_info "Testing GPG signing with key $GPG_KEY_ID..."
echo ""

if echo "test" | gpg --clearsign --local-user "$GPG_KEY_ID" >/dev/null 2>&1; then
    print_success "GPG signing test successful!"
    echo ""
else
    print_error "GPG signing test failed!"
    echo ""
    print_warning "Troubleshooting steps:"
    echo "  1. Ensure you have your GPG passphrase ready"
    echo "  2. Make sure you're running this from a real terminal (not Claude Code)"
    echo "  3. Check GPG configuration:"
    echo "     gpg --list-secret-keys $GPG_KEY_ID"
    echo ""

    read -p "Try GPG signing interactively? (yes/no): " try_gpg

    if [ "$try_gpg" = "yes" ]; then
        echo ""
        print_info "Testing GPG signing (you'll be prompted for passphrase)..."
        echo "test" | gpg --clearsign --local-user "$GPG_KEY_ID"

        if [ $? -ne 0 ]; then
            print_error "GPG signing failed"
            exit 1
        fi

        print_success "GPG signing works!"
        echo ""
    else
        exit 1
    fi
fi

# Show deployment summary
echo "=================================================="
echo "  Deployment Summary"
echo "=================================================="
echo ""
echo "Project:         huevana"
echo "Version:         $VERSION"
echo "Tag:             $TAG_NAME"
echo "POM Version:     $CURRENT_VERSION"
echo "Git Status:      Clean"
echo "Local Tag:       Does not exist ✓"
echo "Remote Tag:      Does not exist ✓"
echo "Maven Central:   Does not exist ✓"
echo "GPG Key:         $GPG_KEY_ID"
echo "GPG Identity:    $GPG_KEY_INFO"
echo "GPG Signing:     Ready ✓"
echo ""

# Final confirmation
read -p "Deploy version $VERSION to Maven Central? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    print_info "Deployment cancelled."
    exit 0
fi

echo ""
print_info "Starting deployment..."
echo ""
print_warning "You will be prompted for your GPG passphrase during signing."
echo ""

# Deploy with release profile (includes GPG signing)
if mvn clean deploy -Prelease -DskipTests=true -Dgpg.keyname="$GPG_KEY_ID"; then
    echo ""
    print_success "Deployment to Maven Central successful!"
    echo ""

    # Create git tag
    print_info "Creating git tag $TAG_NAME..."
    git tag -a "$TAG_NAME" -m "Release $VERSION"

    if [ $? -eq 0 ]; then
        print_success "Git tag created: $TAG_NAME"
        echo ""

        # Ask to push tag
        read -p "Push tag $TAG_NAME to remote? (yes/no): " push_tag

        if [ "$push_tag" = "yes" ]; then
            git push origin "$TAG_NAME"

            if [ $? -eq 0 ]; then
                print_success "Tag pushed to remote"
                echo ""
            else
                print_error "Failed to push tag"
                print_warning "You can push manually later:"
                echo "  git push origin $TAG_NAME"
                echo ""
            fi
        else
            print_info "Tag not pushed. Push it manually when ready:"
            echo "  git push origin $TAG_NAME"
            echo ""
        fi
    else
        print_error "Failed to create git tag"
        echo ""
    fi

    echo "=================================================="
    echo "  Deployment Complete!"
    echo "=================================================="
    echo ""
    print_success "Version $VERSION deployed successfully"
    echo ""
    echo "Next steps:"
    echo ""
    echo "  1. Monitor deployment status:"
    echo "     ./sonatype-manager.sh status <username> <token> <deployment-id>"
    echo ""
    echo "  2. Wait for Maven Central sync (~10-30 minutes):"
    echo "     https://central.sonatype.com/artifact/io.github.greenstevester/huevana/$VERSION"
    echo "     https://search.maven.org/artifact/io.github.greenstevester/huevana/$VERSION"
    echo ""
    echo "  3. Update to next SNAPSHOT version:"
    echo "     mvn versions:set -DnewVersion=X.Y.Z-SNAPSHOT"
    echo "     git add pom.xml"
    echo "     git commit -m \"chore: bump version to X.Y.Z-SNAPSHOT\""
    echo "     git push"
    echo ""

    if [ "$push_tag" != "yes" ]; then
        echo "  4. Push the release tag:"
        echo "     git push origin $TAG_NAME"
        echo ""
    fi

    echo "  5. Create GitHub release:"
    echo "     https://github.com/greenstevester/huevana/releases/new?tag=$TAG_NAME"
    echo ""

else
    echo ""
    print_error "Deployment failed!"
    echo ""
    print_warning "Check the error messages above for details."
    echo ""
    echo "Common issues:"
    echo "  - GPG passphrase incorrect"
    echo "  - Network connectivity problems"
    echo "  - Missing credentials in ~/.m2/settings.xml"
    echo ""
    exit 1
fi
