message="$@"
echo "Message is Date: `date`, changes: $message"
if [[ -z "$message" ]]; then
  echo "enter message $message"
  exit 1
fi
git add .
git commit -m "Date: `date`, changes: \"$message\""
git push origin master
