# Instruction on creating/saving a task

## **-> Properties/Attributes of a Task** we have defined ##


> private String   name;          // define the task action

> private String   place;         // define the place of task

> private String   list;          // belong to which list

> private Priority priority;      // priority of the task

> private Date     startDateTime; // start date and time

> private Date     endDateTime;   // end date and time

> private Date     deadline;      // deadline date and time

> private Long     duration;      // duration of task, stores in second

> private boolean  status;        // completed or not

-> You can **create a Task using the one of the two constructors**

1. public Task( // how to remember: same type (String, Date etc) of
attributes are grouped together

> String name,

> String place,

> String list,

> Priority priority,

> Date startDateTime,

> Date endDateTime,

> Date deadline,

> Long duration,

> boolean status

);

2. public Task(); // For this one, you can use each set..() (eg setName()) to set each attribute of the task

## -> Most Important: **how to specify each properties** ##

**Must Have**: _(default means if the user has not specified this property, you need to initialize it with its default value)_

> - name

> - list     -> default: TaskLists.INBOX

> - priority -> default: Priority.NORMAL

> - status   -> default: Task.INCOMPLETE

**Other Attributes:**

> - Place         -> default: null

> - startDateTime -> default: null (if there is startTime, then there
> should have startDate)

> - endDateTime   -> default: null (if there is no startDateTime, then no endDateTime either)

> - deadline      -> default: null

> - duration      -> default: null (null means there is no startTime/endTime provided)

## **-> Examples** of how to create a Task**(VERY IMPORTANT)** ##

_Assume the date, time of Today in all examples is 2011-10-08 14:00:23, Saturday_

_Note: In our application, we do not care about the seconds of a time, but we **HAVE to set it to 0**_


---

Example 00

---

**User Provided:**have lunch

**Task Attributes:**

> - name           -> "**have lunch**"

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> null

> - endDateTime    -> null

> - deadline       -> null

> - duration       -> null

> - status         -> Task.INCOMPLETE


---



---

Example 01

---

**User Provided:**have lunch 1200-1310 @(PGP canteen)

**Task Attributes:**

> - name           -> "**have lunch**"

> - place          -> **"PGP canteen"**

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> **2011-10-08 12:00:00**

> - endDateTime    -> **2011-10-08 13:10:00**

> - deadline       -> null

> - duration       -> **1\*3600 + 10\*60**

> - status         -> Task.INCOMPLETE


---



---

Example 02

---

**User Provided:**go to bed at 12AM _(Note: 12AM is 24:00, which is also the next day 00:00. 12PM is 12:00)_

**Task Attributes:**

> - name           -> "**go to bed**"

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> **2011-10-09 00:00:00**

> - endDateTime    -> **2011-10-09 00:00:00**

> - deadline       -> null

> - duration       -> **0**

> - status         -> Task.INCOMPLETE


---



---

Example 03

---

**User Provided:**have tea break on Sunday

**Task Attributes:**

> - name           -> **"have tea break**"

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> **2011-10-09 00:00:00**

> - endDateTime    -> **2011-10-09 00:00:00**

> - deadline       -> null

> - duration       -> **null**

> - status         -> Task.INCOMPLETE


---




**Note the difference between Example 02 and 03:**

If the user does **not specify date**, we took it as today. **(for example 02,
which is a special case, 12AM belongs to the next day morning zero clock)**

If the user does **not specify time**, we took it 00:00:00.

If you look closer, In startDateTime, endDateTime, they all stores **2011-10-09
00:00:00.**

How do we differentiate whether the user has specified a time or not?

The answer is looking at **duration**,

> - the duration is **0**, means the user has specified a time.

> - the duration is **null**, means the user has not specified a time (in
> other words, it is an**All Day** task)




---

Example 04

---

**User Provided:**have tea break at 330pm for 30 mins

**Task Attributes:**

> - name           -> **"have tea break**"

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> **2011-10-08 15:30:00**

> - endDateTime    -> **2011-10-08 16:00:00**

> - deadline       -> null

> - duration       -> **30\*60**

> - status         -> Task.INCOMPLETE


---



---

Example 05

---

**User Provided:**have tea break at 330pm - 400pm !1 #(List 1)

**Task Attributes:**

> - name           -> **"have tea break**"

> - place          -> null

> - list           -> **"List 1"**

> - priority       -> **Priority.IMPORTANT**

> - startDateTime  -> **2011-10-08 15:30:00**

> - endDateTime    -> **2011-10-08 16:00:00**

> - deadline       -> null

> - duration       -> **30\*60**

> - status         -> Task.INCOMPLETE


---



---

Example 06

---

**User Provided:**submit Version 0.1 by Monday

**Task Attributes:**

> - name           -> **"submit Version 0.1"**

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> null

> - endDateTime    -> null

> - deadline       -> **2011-10-10 23:59:00**

> - duration       -> null

> - status         -> Task.INCOMPLETE


---



---

Example 07

---

**User Provided:**have tea break by Monday 3pm

**Task Attributes:**

> - name           -> "**have tea break**"

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> null

> - endDateTime    -> null

> - deadline       -> **2011-10-10 15:00:00**

> - duration       -> null

> - status         -> Task.INCOMPLETE


---



---

Example 08

---

**User Provided:**hand in Version 0.1 by 3pm

**Task Attributes:**

> - name           -> **have tea break**

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> null

> - endDateTime    -> null

> - deadline       -> **2011-10-08 15:00:00**

> - duration       -> null

> - status         -> Task.INCOMPLETE


---




**Note for Example 06, 07, 08:**

If the user does **not specify date for deadline**, take **today** as date
(Example 08).

If the user does **not specify time for deadline**, take the **last minute of
that date** as time, 23:59:00. (Example 06)




---




---

Example 09

---

**User Provided:**have tea break for 30mins

**Task Attributes:**

> - name           -> **have tea break**

> - place          -> null

> - list           -> TaskList.INBOX

> - priority       -> Priority.NORMAL

> - startDateTime  -> **2011-10-08 14:00:00**

> - endDateTime    -> **2011-10-08 14:30:00**

> - deadline       -> null

> - duration       -> **30\*60**

> - status         -> Task.INCOMPLETE


---



---



**Note for Example 09:**

If the user does **not specify date/time for startDateTime, but provided
duration**. We take present date/time correct to minute (means you need set seconds to 0) as startDateTime, and added up the duration to form
endDateTime.



---



Hope it clears your doubts on creating a task now.

If you still have problems, **read it again first.**
Storage and GUI parts already follow this instruction strictly.

Zhuochun