message="$@"
echo "Message: $message"
git add .
git commit -m "$message"
git push origin dev
