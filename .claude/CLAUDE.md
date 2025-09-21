# MoleGame - Claude Code Guidelines

## Project Overview

MoleGame is a simple and fun Java-based game where you control a mole digging through soil to reach a goal while avoiding obstacles and collecting power-ups. The game features dynamic levels with increasing difficulty, obstacle avoidance, power-up collection, and a scoring system. This project demonstrates Java game development concepts, graphics programming, and user input handling.

## Development Environment

**Operating System**: Windows 11
**Shell**: Git Bash / PowerShell / Command Prompt
**Important**: Always use Windows-compatible commands:
- Use `dir` instead of `ls` for Command Prompt
- Use PowerShell commands when appropriate
- File paths use backslashes (`\`) in Windows
- Use `python -m http.server` for local development server
- Git Bash provides Unix-like commands but context should be Windows-aware

## Development Guidelines

### Code Quality
- Follow Java naming conventions (camelCase for variables/methods, PascalCase for classes)
- Use meaningful variable and method names
- Implement proper exception handling
- Add comprehensive Javadoc comments for public methods and classes
- Follow SOLID principles and design patterns where appropriate
- Maintain clean, readable code
- Follow language-specific best practices

### Security
- No sensitive information in the codebase
- Use HTTPS for all external resources
- Regular dependency updates
- Follow security best practices for the specific technology stack

### Game Development Specific Guidelines
- Implement proper game loop with consistent frame rate
- Separate game logic from rendering code
- Use efficient collision detection algorithms
- Handle user input responsively and consistently
- Implement proper state management for game levels and progression
- Optimize graphics operations for smooth gameplay
- Use appropriate data structures for game objects and level management

## Learning and Communication
- Always explain coding actions and decisions to help the user learn
- Describe why specific approaches or technologies are chosen
- Explain the purpose and functionality of code changes
- Provide context about best practices and coding patterns used
- Provide detailed explanations in the console when performing tasks, as many concepts may be new to the user