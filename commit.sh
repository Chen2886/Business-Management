message="$@"
echo "Message is Main.Date: `date`, changes: $message"
git add .
git commit -m "Main.Date: `date`, changes: $message"
git push origin dev
