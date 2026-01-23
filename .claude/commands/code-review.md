---
description: "코드 리뷰 에이전트를 활용해 현재까지의 작업을 리뷰합니다."
---

# Code Review Command

Invoke the code review agent defined in `.claude/agents/code-review-agent.md` to perform code review.

## Determine Review Target

1. Check staged changes with `git diff --staged`
2. If nothing staged, check current branch commit history with `git log` and review

## Rules

- If agent file (`.claude/agents/code-review-agent.md`) doesn't exist, notify user and stop
- Follow the checklist and output format defined in the agent exactly


## Changes

| Item | Reason |
|------|--------|
| Specify agent path | Claude needs exact file location to find it |
| Specific git commands | Clear instructions on how to check |
| Add "follow agent format" | Prevent ignoring agent file's output format |

## Folder Structure

.claude/
├── commands/
│   └── code-review.md      # This command file
└── agents/
└── code-review-agent.md  # Agent definition (checklist, output format, etc.)
