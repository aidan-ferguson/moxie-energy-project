echo "The UNITY_LICENSE enviroment variable was not found, generating a license activation file"

if [[ -z "${UNITY_USERNAME}" ]] || [[ -z "${UNITY_PASSWORD}" ]]; then
    echo "You must set the UNITY_USERNAME and UNITY_PASSWORD enviroment variables in order to generate a license activation file, these will be your unity credentials"
    exit 1
fi

# Generate license
xvfb-run --auto-servernum --server-args='-screen 0 640x480x24' \
  unity-editor \
    -logFile /dev/stdout \
    -batchmode \
    -nographics \
    -username "$UNITY_USERNAME" -password "$UNITY_PASSWORD" > ./output.txt

# Output unity activation file
alf=$(cat ./output.txt | grep 'LICENSE SYSTEM .* Posting *' | sed 's/.*Posting *//')
if [ -z "$alf" ]; then
    echo ""
    echo "Printing logs, failed generation"
    echo ""
    cat ./output.txt
    echo ""
    echo "License generation failed, have printed logs above for more information"
    exit 1
fi
echo "This is your activation file. Paste it into a .alf file, then upload it to https://license.unity3d.com/manual and copy the contents of the resulting file and set it to the UNITY_LICENSE enviroment variable"
echo ""
echo "$alf"
echo ""
exit 0