# UI Test Plan

This file is maintained by the project-specific `$test-ui` skill.

## Test configuration

- Program build command: `javac -d out src\main\java\proton\Task.java src\main\java\proton\Todo.java src\main\java\proton\Deadline.java src\main\java\proton\Event.java src\main\java\proton\Proton.java`
- Program launch command: `java -cp out proton.Proton`
- Working directory: `E:\NUS\Academics\Year 2\CS2113\Individual_Project\ip`
- Java version: 25 (verified using `java -version`)
- Comparison: Exact complete stdout after normalizing CRLF/LF line endings
- Failure policy: Stop immediately after the first failed test case
- Preconditions: Start each case with a fresh Proton process and an empty in-memory task list

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

## Latest test session

- Timestamp: `2026-09-04 05:41:43 +08:00`
- Result: PASS (3 of 3 cases passed)
- Build: PASS
- UI-TODO-01: PASS
- UI-DEADLINE-01: PASS
- UI-EVENT-01: PASS
- Process exit codes: `0` for all cases
- Standard error: Empty for all cases

UI-TODO-01 transcript:

```text
INPUT
todo borrow book
list
mark 1
unmark 1
list
bye

OUTPUT
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

UI-DEADLINE-01 transcript:

```text
INPUT
deadline return book /by Sunday
list
mark 1
unmark 1
list
bye

OUTPUT
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

UI-EVENT-01 transcript:

```text
INPUT
event project meeting /from Mon 2pm /to 4pm
list
mark 1
unmark 1
list
bye

OUTPUT
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
