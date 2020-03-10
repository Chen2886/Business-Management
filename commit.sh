message="$@"
echo "Message is Date: `date`, changes: $message"
git add .
git commit -m "Date: `date`, changes: \"$message\""
git push origin dev
