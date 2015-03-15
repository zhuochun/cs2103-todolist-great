### This project made by NUS CS2103, group T14J1 ###

---

# Introduction #

taskMeter, a todo list management we are going to implement in Java.

---

# Project Background #
**Problem outline:**
We are bombarded with ‘things to do’ continuously. The pile of todo items accumulate and weighs heavily on our mind at times. Sometimes things go out of control and we forget to do certain things on time.

**Solution outline:**
It is good if every todo item is put into a systematic process that tracks them and helps us to decide what to do and when to do things so that we can keep our mind clear of todo items.

**Target audience:**
There are MANY todo managers around. Calender software too can act as todo managers. When entering such a crowded product space, we should target a specific way of managing todo items by a specific type of users. Therefore, we target user like Jim whose ‘todo management’ workflow is described below.

Most of Jim’s todo items arrive as emails. This is how Jim process his emails.

  * 1. Decides what the follow up action required by that email.
  * 2a. If it can be done immediately, he does it right away (e.g., just reply to the email) and ‘archive’ the email (i.e. move it out of inbox).
  * 2b. If it cannot be done immediately, he schedules the follow up action in his calendar and archives the email. If he cannot decide a good time to do the action, he simply schedules it in a relatively free area in his calendar.
  * 3. When Jim is free to do some work, he looks at his calendar and picks up the most appropriate thing to do. Once the task is done, he deletes the item or mark it as ‘done’. If there is a further follow up action required, he schedules it in the calendar.
  * 4. Jim periodically reviews the calendar to pick items that could not be completed and need to be rescheduled or discarded as ‘cannot do’.
  * 5. Todo items not arising from email are dealt similarly by entering them in the calendar.

As you can see from the above workflow, Jim’s inbox is almost empty most of time. He no longer worries about an inbox full of emails he has to deal with at any time he login to email. He also does not have to keep any todo items in his mind because everything is recorded in the calendar somewhere. However, here are some ‘pain points’ of his workflow.

  1. Jim is currently using _Google calendar_. Since he store/retrieve/edit todo tasks so frequently, he find the online calendar too slow to work with. He’d rather have a **desktop software he can summon quickly**. Even better (but not a must) if he can **activate the software by pressing a keyboard shortcut**. He is quite impressed by how Google Desktop Quick Search feature can be activated by pressing the control key twice quickly.
  1. Entering an event in a calendar is a pain because it takes several clicks. **Jim would rather type than use the mouse because he can type faster than fiddling with the mouse**.  For example, to be able to type in commands such as "add July 10, 5-6, project meeting" (such as allowed by GCal’s ‘quick add’ feature). He is not against GUIs, but feels most tasks can be done faster using text commands if the software is designed well. However, GUI can still play a part as a way to give visual feedback or for doing tasks too complex to do using text commands.
  1. A **calendar is not very good in capturing todo items that need to be done before a specific time, or after a specific date**.
  1. While it is good that all Jim’s todo items are currently online and available to access from any computer, he **almost always uses his own laptop to access them and sometimes from places where there is no ready Internet connectivity.**
  1. Jim finds it is painful to do certain things important to Jim’s workflow but not so important in a regular calendar. e.g. **looking for suitable slot to schedule an item, searching for specific todo items, marking an item as done, deciding what todo item to do next, postponing an item.**

In this project, you will try to solve some of Jim’s problems to make his workflow even more efficient and painless. i.e. to make his life better. You don’t have to solve all problems mentioned above and there may be other problems you can solve but not mentioned above. Good software delight users by solving problems users didn’t even realize they had.