#!/bin/bash

MACHINE=$1
CMD=$2

if [ "$MACHINE" = "" -o "$CMD" = "" ]; then
  echo "Usage: `basename $0` <machine> <cmd>"
  exit 1
fi

(
  unset TMUX
  tmux new-session -d -s tmp && \
  echo "Connecting to $MACHINE" && \
  tmux send-keys -t tmp:1 "ssh $MACHINE" C-m && \
  sleep 3 && \
  echo "Sending command: $CMD" && \
  tmux send-keys -t tmp:1 "tmux -f /dev/null new-session -d -s remote-tmux && tmux send-keys -t remote-tmux:0 '$CMD' C-m" C-m && \
  sleep 1 && \
  tmux send-keys -t tmp:1 "exit" C-m && \
  sleep 2 && \
  tmux kill-session -t tmp
)
