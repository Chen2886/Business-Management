message="$@"
echo "Message is `date` $message"
git add .
git commit -m "$message"
git push origin dev
