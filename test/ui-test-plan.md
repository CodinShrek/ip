# UI Test Plan

This file is maintained by the project-specific `$test-ui` skill.

## Test configuration

- Program build command: `javac -d out src\main\java\proton\task\Task.java src\main\java\proton\task\Todo.java src\main\java\proton\task\Deadline.java src\main\java\proton\task\Event.java src\main\java\proton\exception\ProtonException.java src\main\java\proton\Proton.java`
- Program launch command: `java -cp out proton.Proton`
- Manual working directory: `E:\NUS\Academics\Year 2\CS2113\Individual_Project\ip`
- Java version: 25 (verified using `java -version`)
- Comparison: Exact complete stdout after normalizing CRLF/LF line endings
- Failure policy: Stop immediately after the first failed test case
- Automated Java launch: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp <absolute out path> proton.Proton`
- Automated runner: `python test/run_ui.py` (builds once with Java 25).
- Test working directory: A fresh temporary directory under `_temp/` for each case; compiled classes use an absolute path.
- Preconditions: Start each case with a fresh Proton process, an empty in-memory task list, and no data directory unless a case supplies initial_save. This isolates tests from real saved tasks.
- Save checks: UI-SAVE-01 checks exact UTF-8 file contents after every command response while the process remains running; `null` means the file must not exist. Normalize only line endings.
- Scope: Load tasks on startup and save after each mutation. JSON objects can specify initial_save, per-command snapshots, unchanged_save, and restart_expected. Restart checks launch a second process in the same isolated directory with exactly `list` and `bye`; compare its entire stdout, stderr, and exit code.

## Test cases

### UI-TODO-01: Add and manage a ToDo task

Aim: Verify that `todo` adds a typed task that can be listed, marked, and unmarked.

Input:

```text
todo borrow book
list
mark 1
unmark 1
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-DEADLINE-01: Add and manage a deadline task

Aim: Verify that `deadline` stores and displays its due date/time as text.

Input:

```text
deadline return book /by Sunday
list
mark 1
unmark 1
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-EVENT-01: Add and manage an event task

Aim: Verify that `event` stores and displays its start and end date/times as text.

Input:

```text
event project meeting /from Mon 2pm /to 4pm
list
mark 1
unmark 1
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-TODO-INVALID-01: Reject an empty todo

Aim: Verify that an empty todo reports an error and does not stop later commands.

Input:

```text
todo
todo borrow book
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! A todo needs a description.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-DEADLINE-INVALID-01: Reject a malformed deadline

Aim: Verify that a deadline without `/by` reports its required format and does not stop later commands.

Input:

```text
deadline return book
deadline return book /by Sunday
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: deadline DESCRIPTION /by DATE
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-EVENT-INVALID-01: Reject a malformed event

Aim: Verify that an event without `/to` reports its required format and does not stop later commands.

Input:

```text
event project meeting /from Mon 2pm
event project meeting /from Mon 2pm /to 4pm
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-TASK-NUMBER-INVALID-01: Reject invalid task references

Aim: Verify that invalid task references report specific errors and do not stop later commands.

Input:

```text
mark
unmark proton
mark 1
todo borrow book
mark 2
mark 1
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: mark TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: unmark TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! There are no tasks in Proton's orbit yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-UNKNOWN-01: Reject an unknown command

Aim: Verify that an unknown command reports an error and does not add a task.

Input:

```text
dance
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! That command is outside Proton's orbit.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-BLANK-01: Reject a blank command

Aim: Verify that a blank command reports an error and does not stop later commands.

Input:

```text

bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! No command was detected. Please enter a command.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-CAPACITY-01: Grow beyond the former capacity

Aim: Verify that Proton accepts task 101 and still accepts `bye`.

Input:

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo task 101
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 4
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 5
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 6
 Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 7
 Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 8
 Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 9
 Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 10
 Now you have 10 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 11
 Now you have 11 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 12
 Now you have 12 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 13
 Now you have 13 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 14
 Now you have 14 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 15
 Now you have 15 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 16
 Now you have 16 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 17
 Now you have 17 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 18
 Now you have 18 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 19
 Now you have 19 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 20
 Now you have 20 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 21
 Now you have 21 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 22
 Now you have 22 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 23
 Now you have 23 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 24
 Now you have 24 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 25
 Now you have 25 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 26
 Now you have 26 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 27
 Now you have 27 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 28
 Now you have 28 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 29
 Now you have 29 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 30
 Now you have 30 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 31
 Now you have 31 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 32
 Now you have 32 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 33
 Now you have 33 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 34
 Now you have 34 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 35
 Now you have 35 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 36
 Now you have 36 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 37
 Now you have 37 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 38
 Now you have 38 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 39
 Now you have 39 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 40
 Now you have 40 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 41
 Now you have 41 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 42
 Now you have 42 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 43
 Now you have 43 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 44
 Now you have 44 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 45
 Now you have 45 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 46
 Now you have 46 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 47
 Now you have 47 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 48
 Now you have 48 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 49
 Now you have 49 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 50
 Now you have 50 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 51
 Now you have 51 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 52
 Now you have 52 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 53
 Now you have 53 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 54
 Now you have 54 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 55
 Now you have 55 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 56
 Now you have 56 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 57
 Now you have 57 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 58
 Now you have 58 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 59
 Now you have 59 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 60
 Now you have 60 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 61
 Now you have 61 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 62
 Now you have 62 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 63
 Now you have 63 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 64
 Now you have 64 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 65
 Now you have 65 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 66
 Now you have 66 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 67
 Now you have 67 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 68
 Now you have 68 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 69
 Now you have 69 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 70
 Now you have 70 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 71
 Now you have 71 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 72
 Now you have 72 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 73
 Now you have 73 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 74
 Now you have 74 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 75
 Now you have 75 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 76
 Now you have 76 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 77
 Now you have 77 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 78
 Now you have 78 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 79
 Now you have 79 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 80
 Now you have 80 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 81
 Now you have 81 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 82
 Now you have 82 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 83
 Now you have 83 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 84
 Now you have 84 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 85
 Now you have 85 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 86
 Now you have 86 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 87
 Now you have 87 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 88
 Now you have 88 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 89
 Now you have 89 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 90
 Now you have 90 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 91
 Now you have 91 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 92
 Now you have 92 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 93
 Now you have 93 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 94
 Now you have 94 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 95
 Now you have 95 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 96
 Now you have 96 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 97
 Now you have 97 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 98
 Now you have 98 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 99
 Now you have 99 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 100
 Now you have 100 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 101
 Now you have 101 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-DELETE-01: Delete tasks and use renumbered indices

Aim: Verify middle, last, first, and only-task deletion across task types, then add again.

Input:

```text
todo read book
deadline return book /by Sunday
event meeting /from 2pm /to 4pm
todo read book
delete 2
list
mark 2
unmark 2
delete 3
list
delete 1
list
delete 1
list
deadline return book /by Sunday
mark 1
delete 1
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Sunday)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] meeting (from: 2pm to: 4pm)
 3.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Sunday)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

### UI-DELETE-INVALID-01: Reject invalid deletion requests

Aim: Verify invalid requests leave tasks intact and allow later valid deletion.

Input:

```text
delete
delete 1
todo read book
delete 
delete abc
delete 1 2
delete 0
delete -1
delete 2
delete 2147483648
delete -2147483648
delete1
list
delete   1  
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! There are no tasks in Proton's orbit yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! That command is outside Proton's orbit.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```


### UI-SAVE-01: Save every task-list change immediately

Aim: Verify directory creation, all task types, UTF-8 text, completion changes, overwrite after deletion, and an empty file after deleting the last task. Check before exit; list and bye do not change the file.

Input:

```text
list
todo café | book
deadline submit /by Friday
event meeting /from 2pm /to 4pm
mark 2
unmark 2
delete 1
delete 2
delete 1
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] café | book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit (by: Friday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] café | book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] submit (by: Friday)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Expected save file after each input line (JSON strings):

```json
[
  null,
  "[T][ ] café | book\n",
  "[T][ ] café | book\n[D][ ] submit (by: Friday)\n",
  "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n",
  "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n",
  "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n",
  "[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n",
  "[D][ ] submit (by: Friday)\n",
  "",
  ""
]
```

### UI-LOAD-01: Restore and modify saved tasks across restarts

Aim: Restore all task types, status, order, dates, and Unicode; modify the loaded list and verify another process restores the saved changes.

Input:

```text
list
unmark 1
mark 2
delete 3
event lunch /from noon /to 1pm
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] café | book
 2.[D][ ] submit (by: Friday)
 3.[E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] café | book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][X] meeting (from: 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] lunch (from: noon to: 1pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] café | book
 2.[D][X] submit (by: Friday)
 3.[E][ ] lunch (from: noon to: 1pm)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Storage configuration (exact UTF-8 contents and restart expectations):

```json
{
  "initial_save": "[T][X] café | book\n[D][ ] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n",
  "snapshots": [
    "[T][X] café | book\n[D][ ] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n",
    "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n",
    "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n",
    "[T][ ] café | book\n[D][X] submit (by: Friday)\n",
    "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n",
    "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n",
    "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n"
  ],
  "restart_expected": " ____            _              \n|  _ \\ _ __ ___ | |_ ___  _ __ \n| |_) | '__/ _ \\| __/ _ \\| '_ \\\n|  __/| | | (_) | || (_) | | | |\n|_|   |_|  \\___/ \\__\\___/|_| |_|\n\n____________________________________________________________\nHey there! I'm Proton, your positively charged chatbot!\nI'm fired up and ready to help! What awesome thing shall we tackle today?\n____________________________________________________________\n____________________________________________________________\n Here are the tasks in your list:\n 1.[T][ ] café | book\n 2.[D][X] submit (by: Friday)\n 3.[E][ ] lunch (from: noon to: 1pm)\n____________________________________________________________\n____________________________________________________________\n Powering down for now, I'll see you next time!\n____________________________________________________________\n"
}
```

### UI-LOAD-EMPTY-01: Load an empty save file

Aim: Start with an empty list and preserve the zero-byte file.

Input:

```text
list
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Storage configuration (exact UTF-8 contents and restart expectations):

```json
{
  "initial_save": "",
  "snapshots": [
    "",
    ""
  ],
  "restart_expected": " ____            _              \n|  _ \\ _ __ ___ | |_ ___  _ __ \n| |_) | '__/ _ \\| __/ _ \\| '_ \\\n|  __/| | | (_) | || (_) | | | |\n|_|   |_|  \\___/ \\__\\___/|_| |_|\n\n____________________________________________________________\nHey there! I'm Proton, your positively charged chatbot!\nI'm fired up and ready to help! What awesome thing shall we tackle today?\n____________________________________________________________\n____________________________________________________________\n Here are the tasks in your list:\n____________________________________________________________\n____________________________________________________________\n Powering down for now, I'll see you next time!\n____________________________________________________________\n"
}
```

### UI-LOAD-INVALID-01: Preserve a malformed save file

Aim: Stop before accepting a mutation when a later saved line is malformed, preserving the whole original file.

Input:

```text
todo must not overwrite
bye
```

Expected output:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
 Proton could not load data/proton.txt. Check the file and restart.
```

Storage configuration (exact UTF-8 contents and restart expectations):

```json
{
  "initial_save": "[T][ ] valid task\nnot a saved task\n",
  "unchanged_save": "[T][ ] valid task\nnot a saved task\n"
}
```

## Latest test session

Timestamp: 2026-09-16T03:39:40.522463+08:00

Result: PASS

Java: java version "25.0.4" 2026-07-21 LTS

### UI-TODO-01: Add and manage a ToDo task

PASS; exit code: 0

Input:

```text
todo borrow book
list
mark 1
unmark 1
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-DEADLINE-01: Add and manage a deadline task

PASS; exit code: 0

Input:

```text
deadline return book /by Sunday
list
mark 1
unmark 1
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-EVENT-01: Add and manage an event task

PASS; exit code: 0

Input:

```text
event project meeting /from Mon 2pm /to 4pm
list
mark 1
unmark 1
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-TODO-INVALID-01: Reject an empty todo

PASS; exit code: 0

Input:

```text
todo
todo borrow book
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! A todo needs a description.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-DEADLINE-INVALID-01: Reject a malformed deadline

PASS; exit code: 0

Input:

```text
deadline return book
deadline return book /by Sunday
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: deadline DESCRIPTION /by DATE
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-EVENT-INVALID-01: Reject a malformed event

PASS; exit code: 0

Input:

```text
event project meeting /from Mon 2pm
event project meeting /from Mon 2pm /to 4pm
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-TASK-NUMBER-INVALID-01: Reject invalid task references

PASS; exit code: 0

Input:

```text
mark
unmark proton
mark 1
todo borrow book
mark 2
mark 1
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: mark TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: unmark TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! There are no tasks in Proton's orbit yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-UNKNOWN-01: Reject an unknown command

PASS; exit code: 0

Input:

```text
dance
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! That command is outside Proton's orbit.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-BLANK-01: Reject a blank command

PASS; exit code: 0

Input:

```text

bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! No command was detected. Please enter a command.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-CAPACITY-01: Grow beyond the former capacity

PASS; exit code: 0

Input:

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo task 101
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 4
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 5
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 6
 Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 7
 Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 8
 Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 9
 Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 10
 Now you have 10 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 11
 Now you have 11 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 12
 Now you have 12 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 13
 Now you have 13 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 14
 Now you have 14 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 15
 Now you have 15 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 16
 Now you have 16 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 17
 Now you have 17 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 18
 Now you have 18 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 19
 Now you have 19 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 20
 Now you have 20 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 21
 Now you have 21 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 22
 Now you have 22 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 23
 Now you have 23 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 24
 Now you have 24 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 25
 Now you have 25 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 26
 Now you have 26 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 27
 Now you have 27 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 28
 Now you have 28 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 29
 Now you have 29 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 30
 Now you have 30 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 31
 Now you have 31 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 32
 Now you have 32 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 33
 Now you have 33 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 34
 Now you have 34 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 35
 Now you have 35 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 36
 Now you have 36 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 37
 Now you have 37 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 38
 Now you have 38 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 39
 Now you have 39 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 40
 Now you have 40 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 41
 Now you have 41 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 42
 Now you have 42 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 43
 Now you have 43 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 44
 Now you have 44 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 45
 Now you have 45 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 46
 Now you have 46 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 47
 Now you have 47 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 48
 Now you have 48 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 49
 Now you have 49 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 50
 Now you have 50 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 51
 Now you have 51 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 52
 Now you have 52 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 53
 Now you have 53 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 54
 Now you have 54 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 55
 Now you have 55 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 56
 Now you have 56 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 57
 Now you have 57 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 58
 Now you have 58 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 59
 Now you have 59 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 60
 Now you have 60 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 61
 Now you have 61 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 62
 Now you have 62 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 63
 Now you have 63 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 64
 Now you have 64 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 65
 Now you have 65 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 66
 Now you have 66 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 67
 Now you have 67 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 68
 Now you have 68 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 69
 Now you have 69 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 70
 Now you have 70 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 71
 Now you have 71 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 72
 Now you have 72 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 73
 Now you have 73 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 74
 Now you have 74 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 75
 Now you have 75 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 76
 Now you have 76 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 77
 Now you have 77 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 78
 Now you have 78 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 79
 Now you have 79 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 80
 Now you have 80 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 81
 Now you have 81 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 82
 Now you have 82 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 83
 Now you have 83 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 84
 Now you have 84 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 85
 Now you have 85 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 86
 Now you have 86 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 87
 Now you have 87 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 88
 Now you have 88 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 89
 Now you have 89 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 90
 Now you have 90 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 91
 Now you have 91 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 92
 Now you have 92 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 93
 Now you have 93 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 94
 Now you have 94 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 95
 Now you have 95 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 96
 Now you have 96 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 97
 Now you have 97 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 98
 Now you have 98 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 99
 Now you have 99 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 100
 Now you have 100 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 101
 Now you have 101 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-DELETE-01: Delete tasks and use renumbered indices

PASS; exit code: 0

Input:

```text
todo read book
deadline return book /by Sunday
event meeting /from 2pm /to 4pm
todo read book
delete 2
list
mark 2
unmark 2
delete 3
list
delete 1
list
delete 1
list
deadline return book /by Sunday
mark 1
delete 1
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Sunday)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] meeting (from: 2pm to: 4pm)
 3.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Sunday)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-DELETE-INVALID-01: Reject invalid deletion requests

PASS; exit code: 0

Input:

```text
delete
delete 1
todo read book
delete 
delete abc
delete 1 2
delete 0
delete -1
delete 2
delete 2147483648
delete -2147483648
delete1
list
delete   1  
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! There are no tasks in Proton's orbit yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! Use: delete TASK_NUMBER
____________________________________________________________
____________________________________________________________
 Positive charge alert! Choose a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Positive charge alert! That command is outside Proton's orbit.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

### UI-SAVE-01: Save every task-list change immediately

PASS; exit code: 0

Input:

```text
list
todo café | book
deadline submit /by Friday
event meeting /from 2pm /to 4pm
mark 2
unmark 2
delete 1
delete 2
delete 1
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] café | book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit (by: Friday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] café | book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] submit (by: Friday)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

Save checks:

```text
list: PASS; save file = null
todo café | book: PASS; save file = "[T][ ] café | book\n"
deadline submit /by Friday: PASS; save file = "[T][ ] café | book\n[D][ ] submit (by: Friday)\n"
event meeting /from 2pm /to 4pm: PASS; save file = "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n"
mark 2: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n"
unmark 2: PASS; save file = "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n"
delete 1: PASS; save file = "[D][ ] submit (by: Friday)\n[E][ ] meeting (from: 2pm to: 4pm)\n"
delete 2: PASS; save file = "[D][ ] submit (by: Friday)\n"
delete 1: PASS; save file = ""
bye: PASS; save file = ""
```

### UI-LOAD-01: Restore and modify saved tasks across restarts

PASS; exit code: 0

Input:

```text
list
unmark 1
mark 2
delete 3
event lunch /from noon /to 1pm
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] café | book
 2.[D][ ] submit (by: Friday)
 3.[E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] café | book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit (by: Friday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][X] meeting (from: 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] lunch (from: noon to: 1pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] café | book
 2.[D][X] submit (by: Friday)
 3.[E][ ] lunch (from: noon to: 1pm)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

Save checks:

```text
list: PASS; save file = "[T][X] café | book\n[D][ ] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n"
unmark 1: PASS; save file = "[T][ ] café | book\n[D][ ] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n"
mark 2: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][X] meeting (from: 2pm to: 4pm)\n"
delete 3: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n"
event lunch /from noon /to 1pm: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n"
list: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n"
bye: PASS; save file = "[T][ ] café | book\n[D][X] submit (by: Friday)\n[E][ ] lunch (from: noon to: 1pm)\n"
Restart restored the saved list: PASS
```

Restart input:

```text
list
bye
```

Restart stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] café | book
 2.[D][X] submit (by: Friday)
 3.[E][ ] lunch (from: noon to: 1pm)
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Restart stderr:

```text
```

Restart exit code: 0

### UI-LOAD-EMPTY-01: Load an empty save file

PASS; exit code: 0

Input:

```text
list
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Stderr:

```text
```

Save checks:

```text
list: PASS; save file = ""
bye: PASS; save file = ""
Restart restored the saved list: PASS
```

Restart input:

```text
list
bye
```

Restart stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Powering down for now, I'll see you next time!
____________________________________________________________
```

Restart stderr:

```text
```

Restart exit code: 0

### UI-LOAD-INVALID-01: Preserve a malformed save file

PASS; exit code: 0

Input:

```text
todo must not overwrite
bye
```

Actual stdout:

```text
 ____            _              
|  _ \ _ __ ___ | |_ ___  _ __ 
| |_) | '__/ _ \| __/ _ \| '_ \
|  __/| | | (_) | || (_) | | | |
|_|   |_|  \___/ \__\___/|_| |_|

____________________________________________________________
Hey there! I'm Proton, your positively charged chatbot!
I'm fired up and ready to help! What awesome thing shall we tackle today?
____________________________________________________________
 Proton could not load data/proton.txt. Check the file and restart.
```

Stderr:

```text
```

Save checks:

```text

Startup preserved the save file: PASS
```
