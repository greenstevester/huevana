# Deployment and Management Scripts

This directory contains utility scripts for managing huevana deployments to Maven Central and GPG key distribution.

## Scripts Overview

### 🚀 deploy.sh
**Purpose:** Deploy huevana to Maven Central with comprehensive validation checks.

**Usage:**
```bash
cd /path/to/huevana
./scripts/deploy.sh <version> [gpg-key-id]
```

**Examples:**
```bash
# Deploy version 5.2.0 with auto-detected GPG key
./scripts/deploy.sh 5.2.0

# Deploy with specific GPG key
./scripts/deploy.sh 5.2.0 ABCD1234EFGH5678
```

**Features:**
- ✅ Version format validation (X.Y.Z or X.Y.Z-suffix)
- ✅ Git working directory clean check
- ✅ Local and remote git tag validation
- ✅ Maven Central duplicate version check
- ✅ POM version synchronization
- ✅ Auto-detection of GPG keys
- ✅ GPG signing test
- ✅ Interactive deployment confirmation
- ✅ Automatic git tag creation
- ✅ Optional tag push to remote

**Requirements:**
- Clean git working directory
- Valid GPG key for signing
- Maven Central credentials in `~/.m2/settings.xml`
- Terminal.app (not Claude Code terminal) for GPG passphrase entry

---

### 🔑 publish-gpg-keyservers.sh
**Purpose:** Publish your GPG public key to keyservers required for Maven Central.

**Usage:**
```bash
./scripts/publish-gpg-keyservers.sh [gpg-key-id]
```

**Examples:**
```bash
# Auto-detect and publish GPG key
./scripts/publish-gpg-keyservers.sh

# Publish specific GPG key
./scripts/publish-gpg-keyservers.sh ABCD1234EFGH5678
```

**Features:**
- ✅ Auto-detection of GPG keys
- ✅ Pre-publication check (detects already published keys)
- ✅ Publishes to required keyservers:
  - keyserver.ubuntu.com
  - keys.openpgp.org (requires email verification)
  - pgp.mit.edu
- ✅ Subkey revocation guidance
- ✅ Interactive confirmation

**Important:**
- keys.openpgp.org requires email verification
- Check your email after publishing and click the verification link
- Wait 5-10 minutes for keyserver propagation

---

### ✓ verify-gpg-keyservers.sh
**Purpose:** Verify your GPG public key is available on required keyservers.

**Usage:**
```bash
./scripts/verify-gpg-keyservers.sh [gpg-key-id]
```

**Examples:**
```bash
# Auto-detect and verify GPG key
./scripts/verify-gpg-keyservers.sh

# Verify specific GPG key
./scripts/verify-gpg-keyservers.sh ABCD1234EFGH5678
```

**Features:**
- ✅ Auto-detection of GPG keys
- ✅ Checks all required keyservers
- ✅ Clear status reporting (Found ✓ / Not found ✗)
- ✅ Provides publishing instructions if key not found

**When to use:**
- After publishing keys with `publish-gpg-keyservers.sh`
- Before deploying to Maven Central
- To verify keyserver propagation

---

### 🔧 sonatype-manager.sh
**Purpose:** Manage Sonatype Central deployments (check status, drop, or publish).

**Usage:**
```bash
./scripts/sonatype-manager.sh <command> <username> <token> [deployment-id]
```

**Commands:**
- `status` - Check deployment status
- `drop` - Drop a failed/validated deployment
- `publish` - Manually publish a validated deployment

**Examples:**
```bash
# Check deployment status
./scripts/sonatype-manager.sh status myuser mytoken f5c6fb3c-77f2-43c6-9f8d-00148a39d4c2

# Drop a deployment
./scripts/sonatype-manager.sh drop myuser mytoken f5c6fb3c-77f2-43c6-9f8d-00148a39d4c2

# Manually publish
./scripts/sonatype-manager.sh publish myuser mytoken f5c6fb3c-77f2-43c6-9f8d-00148a39d4c2
```

**Features:**
- ✅ Uses Sonatype Central Portal Publisher API
- ✅ Handles authentication
- ✅ JSON response formatting
- ✅ State validation (can only drop VALIDATED/FAILED deployments)

**When to use:**
- When a deployment is stuck in PENDING state
- To manually control deployment lifecycle
- To verify deployment status

---

## Complete Deployment Workflow

### First-Time Setup

1. **Generate GPG Key** (if you don't have one):
   ```bash
   gpg --full-generate-key
   ```

2. **Publish GPG Key**:
   ```bash
   ./scripts/publish-gpg-keyservers.sh
   ```

3. **Verify Email** from keys.openpgp.org (check your inbox)

4. **Wait and Verify** (5-10 minutes):
   ```bash
   ./scripts/verify-gpg-keyservers.sh
   ```

### Deploying a Release

1. **Ensure clean repository**:
   ```bash
   git status
   ```

2. **Run deployment script**:
   ```bash
   ./scripts/deploy.sh 5.2.0
   ```

3. **Follow the prompts**:
   - Review deployment summary
   - Enter GPP passphrase when prompted
   - Confirm deployment
   - Optionally push git tag

4. **Monitor Maven Central** (~10-30 minutes):
   ```
   https://central.sonatype.com/artifact/io.github.greenstevester/huevana/5.2.0
   ```

### Troubleshooting

**If deployment fails:**
```bash
# Check deployment status
./scripts/sonatype-manager.sh status <user> <token> <deployment-id>

# Drop failed deployment if needed
./scripts/sonatype-manager.sh drop <user> <token> <deployment-id>
```

**If GPG keys not found:**
```bash
# Re-publish keys
./scripts/publish-gpg-keyservers.sh

# Verify after 10 minutes
./scripts/verify-gpg-keyservers.sh
```

**If version already exists on Maven Central:**
- Maven Central doesn't allow re-deploying the same version
- Increment version number and try again
- Example: 5.2.0 → 5.2.1

---

## Security Notes

### Never Commit:
- ❌ GPG private keys
- ❌ Maven Central credentials
- ❌ API tokens
- ❌ Passphrases

### Credentials Location:
- Maven credentials: `~/.m2/settings.xml`
- GPG keys: `~/.gnupg/`
- GitHub secrets: Repository settings → Secrets and variables → Actions

### Best Practices:
- ✅ Use GPG agent for passphrase caching
- ✅ Run deployment from Terminal.app (not Claude Code)
- ✅ Verify git status before deploying
- ✅ Test with pre-release versions first (e.g., 5.2.0-RC1)
- ✅ Wait for keyserver propagation before deploying
- ✅ Keep GPG keys backed up securely

---

## Script Dependencies

### Required Tools:
- `git` - Version control
- `mvn` - Maven build tool
- `gpg` - GNU Privacy Guard
- `curl` - HTTP client
- `jq` - JSON processor (for sonatype-manager.sh)

### Required Configuration:
- `~/.m2/settings.xml` - Maven Central credentials
- `~/.gnupg/` - GPG keyring with valid key pair
- Clean git working directory
- Valid POM version

---

## GitHub Actions Alternative

Instead of using these scripts locally, you can trigger the GitHub Actions workflow:

```bash
# Trigger workflow manually
gh workflow run release.yml -f version=5.2.0 -f prerelease=false

# Monitor progress
gh run watch
```

The GitHub Actions workflow performs the same validation and deployment steps automatically.

---

## Getting Help

**For script issues:**
- Check script usage: `./scripts/<script-name>.sh --help` or run without arguments
- Review error messages carefully
- Ensure all prerequisites are met

**For Maven Central issues:**
- Check Sonatype Central Portal: https://central.sonatype.com
- Review deployment logs
- Use `sonatype-manager.sh` to check status

**For GPG issues:**
- Verify key exists: `gpg --list-secret-keys`
- Test signing: `echo "test" | gpg --clearsign`
- Check GPG agent: `gpg-agent --daemon`

---

## License

These scripts are part of the huevana project and are licensed under the MIT License.
