---
description: "kotlin-migration-agent를 사용해 Java 파일을 코틀린으로 마이그레이션하는 명령어입니다."
---

# Instructions

You MUST use the Task tool to invoke the kotlin-migration-agent immediately.

## Input Processing

1. If user provides a file path:
   - Use Read tool to verify the file exists and is a Java file
   - Pass the absolute file path to the agent

2. If user provides a directory path:
   - Use Glob to find all `.java` files in that directory
   - Pass the directory path to the agent

3. If no path provided:
   - Ask user to specify the Java file or directory to migrate

## Agent Invocation

Call the Task tool with:
- subagent_type: "kotlin-migration-agent"
- prompt: "Migrate [FILE_PATH or DIRECTORY_PATH] from Java to Kotlin following the Test-First methodology"
- description: "Migrate Java to Kotlin"

Example:
```
Task tool:
  subagent_type: kotlin-migration-agent
  prompt: Migrate src/main/java/leets/leenk/domain/feed/service/FeedGetService.java from Java to Kotlin following the Test-First methodology
  description: Migrate Java to Kotlin
```

## Important Notes

- NEVER perform migration yourself - ALWAYS delegate to kotlin-migration-agent
- Agent will handle: test writing, migration, refactoring, and ktlint verification
- Agent follows strict order: Test → Migrate → Refactor → Verify
- All agent output will be in Korean as per agent rules
