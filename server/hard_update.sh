#!/usr/bin/env bash

# Update the source code on Heroku and run rake db:reset to ensure the correct
# DB schema and seed are loaded

function hardUpdate {
  GITDIR=`git rev-parse --git-dir | xargs dirname`
  echo "Found git root: $GITDIR"

  cd "$GITDIR" && \
  git subtree push --prefix server heroku master && \
  heroku run rake db:reset
}

echo "Hard updating the Heroku server. This will update the source code AND run 'rake db:reset'."
read -p "Are you sure you want to continue? [yN] " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]
then
  hardUpdate
  exit 0
else
  echo "Aborting..."
  exit 0
fi

