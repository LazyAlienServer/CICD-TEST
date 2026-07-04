#!/bin/bash
# Setup script for CICD-TEST project

echo "=========================================="
echo "CICD-TEST Project Setup"
echo "=========================================="
echo ""

# Check if Nacos archive exists
NACOS_ARCHIVE="/Users/martin/Desktop/CICD-TEST/nacos/nacos-server-2.4.3.tar.gz"
NACOS_DIR="/Users/martin/Desktop/CICD-TEST/nacos/nacos"

if [ -f "$NACOS_ARCHIVE" ]; then
    echo "✓ Nacos archive found"

    if [ ! -d "$NACOS_DIR" ]; then
        echo "→ Extracting Nacos..."
        cd /Users/martin/Desktop/CICD-TEST/nacos
        tar -xzf nacos-server-2.4.3.tar.gz

        if [ -d "$NACOS_DIR" ]; then
            echo "✓ Nacos extracted successfully"
            chmod +x "$NACOS_DIR/bin/*.sh"
        else
            echo "✗ Failed to extract Nacos"
            exit 1
        fi
    else
        echo "✓ Nacos already extracted"
    fi
else
    echo "✗ Nacos archive not found"
    echo "  Please download it first or wait for the download to complete"
    echo "  Expected location: $NACOS_ARCHIVE"
fi

echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo ""
echo "1. Start Nacos:"
echo "   cd nacos && ./start-nacos.sh"
echo ""
echo "2. Start CICD service:"
echo "   cd cicd && mvn spring-boot:run"
echo ""
echo "3. Start Gateway:"
echo "   cd gateway && mvn spring-boot:run"
echo ""
echo "4. Access Nacos Console:"
echo "   http://localhost:8848/nacos (nacos/nacos)"
echo ""
echo "5. Test endpoints:"
echo "   curl http://localhost:8082/api/test  # Direct"
echo "   curl http://localhost:9090/api/test  # Via Gateway"
echo ""
