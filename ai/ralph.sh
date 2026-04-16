#!/bin/bash
# Ralph Wiggum - Long-running AI agent loop
# Usage: ./ralph.sh [--tool amp|claude|codex] [--task task-name] [max_iterations]

set -euo pipefail

# Parse arguments
TOOL="amp"  # Default to amp for backwards compatibility
MAX_ITERATIONS=10
TASK_NAME=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --tool)
      TOOL="$2"
      shift 2
      ;;
    --tool=*)
      TOOL="${1#*=}"
      shift
      ;;
    --task)
      TASK_NAME="$2"
      shift 2
      ;;
    --task=*)
      TASK_NAME="${1#*=}"
      shift
      ;;
    *)
      # Assume it's max_iterations if it's a number
      if [[ "$1" =~ ^[0-9]+$ ]]; then
        MAX_ITERATIONS="$1"
      fi
      shift
      ;;
  esac
done

# Validate tool choice
if [[ "$TOOL" != "amp" && "$TOOL" != "claude" && "$TOOL" != "codex" ]]; then
  echo "Error: Invalid tool '$TOOL'. Must be 'amp', 'claude', or 'codex'."
  exit 1
fi
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ACTIVE_DIR="$SCRIPT_DIR/docs/exec-plans/active"
PROGRESS_DIR="$SCRIPT_DIR/docs/progress"
PROMPT_FILE="$SCRIPT_DIR/prompt.md"

mkdir -p "$ACTIVE_DIR" "$SCRIPT_DIR/docs/exec-plans/completed" "$PROGRESS_DIR"

if [[ -n "$TASK_NAME" ]]; then
  TASK_JSON_FILE="$ACTIVE_DIR/$TASK_NAME.json"
else
  mapfile -t JSON_FILES < <(find "$ACTIVE_DIR" -maxdepth 1 -type f -name '*.json' | sort)
  if [[ ${#JSON_FILES[@]} -eq 0 ]]; then
    echo "Error: No task JSON found in $ACTIVE_DIR"
    exit 1
  elif [[ ${#JSON_FILES[@]} -gt 1 ]]; then
    echo "Error: Multiple task JSON files found in $ACTIVE_DIR. Use --task <name>."
    exit 1
  fi
  TASK_JSON_FILE="${JSON_FILES[0]}"
  TASK_NAME="$(basename "${TASK_JSON_FILE%.json}")"
fi

if [[ ! -f "$TASK_JSON_FILE" ]]; then
  echo "Error: Task JSON not found: $TASK_JSON_FILE"
  exit 1
fi

PROGRESS_FILE="$PROGRESS_DIR/$TASK_NAME.txt"

# Initialize progress file if it doesn't exist
if [ ! -f "$PROGRESS_FILE" ]; then
  echo "# Ralph Progress Log" > "$PROGRESS_FILE"
  echo "Started: $(date)" >> "$PROGRESS_FILE"
  echo "" >> "$PROGRESS_FILE"
  echo "## 代码库模式" >> "$PROGRESS_FILE"
  echo "" >> "$PROGRESS_FILE"
  echo "---" >> "$PROGRESS_FILE"
fi

echo "Starting Ralph - Tool: $TOOL - Max iterations: $MAX_ITERATIONS"

run_tool() {
  if [[ "$TOOL" == "amp" ]]; then
    TASK_JSON_FILE="$TASK_JSON_FILE" PROGRESS_FILE="$PROGRESS_FILE" \
      amp --dangerously-allow-all < "$PROMPT_FILE"
  elif [[ "$TOOL" == "claude" ]]; then
    TASK_JSON_FILE="$TASK_JSON_FILE" PROGRESS_FILE="$PROGRESS_FILE" \
      claude --dangerously-skip-permissions --print < "$PROMPT_FILE"
  else
    TASK_JSON_FILE="$TASK_JSON_FILE" PROGRESS_FILE="$PROGRESS_FILE" \
      codex exec --dangerously-bypass-approvals-and-sandbox -C "$SCRIPT_DIR" - < "$PROMPT_FILE"
  fi
}

for i in $(seq 1 $MAX_ITERATIONS); do
  echo ""
  echo "==============================================================="
  echo "  Ralph Iteration $i of $MAX_ITERATIONS ($TOOL / $TASK_NAME)"
  echo "==============================================================="

  # Run the selected tool with the canonical Ralph prompt.
  set +e
  OUTPUT="$(run_tool 2>&1 | tee /dev/stderr)"
  STATUS=$?
  set -e
  if [[ $STATUS -ne 0 ]]; then
    echo ""
    echo "Ralph failed to run $TOOL on iteration $i (exit $STATUS)."
    exit $STATUS
  fi
  
  # Check for completion signal.
  # `codex exec` echoes the full prompt, which itself contains a standalone
  # `<promise>COMPLETE</promise>` line. Restrict the search to the tail of the
  # tool output so we only match a completion marker emitted at the end of the
  # assistant response, not the prompt preamble.
  if printf '%s\n' "$OUTPUT" | tail -n 80 | grep -qE '^[[:space:]]*<promise>COMPLETE</promise>[[:space:]]*$'; then
    echo ""
    echo "Ralph completed all tasks!"
    echo "Completed at iteration $i of $MAX_ITERATIONS"
    exit 0
  fi
  
  echo "Iteration $i complete. Continuing..."
  sleep 2
done

echo ""
echo "Ralph reached max iterations ($MAX_ITERATIONS) without completing all tasks."
echo "Check $PROGRESS_FILE for status."
exit 1
