discretetime
============

A simple experiment in time tracking that works by letting you sample
what you are up to at specified time intervals.

It pops up every 15 minutes (configurable), and asks you what you're
doing. You can type notes, and any word that starts with : is a
tag. The notes go to a CSV file that can be used from e.g. Excel to
tally up summaries of how you were spending your time. The file ends
up in your home directory /discretetime/notes.utf8.txt. On Windows 7
that's C:\Users\username

The idea is that no-one really has the discipline to log their work
accurately anyway, and being reminded in a non-irritating and useful
way helps. Merely sample what you're doing is less intrusive and in
aggregate, more accurate. If you were away from your computer, you'll
just log it when you get back.

Suggestions for use:
--------------------

Have a simple set of tags for the actual activity, e.g. coding
(:code), designing (:dsn), debugging (:dbg), documenting (:doc),
planning (:plan), meeting (:mt), talking (:talk), support call
(:spt)...

Have another set for different projects or customers e.g. :tax :ibm
:apple :ms :break :lunch

Each timed note would then normally have two tags and if you want,
something human readable.

If you took a coffee break, then that would be in there too.

When you switch on your computer in the morning, just submit an empty
note. That makes it explicit that you did not do anything you want to
track.

What to do with this data? I don't know exactly yet, but it should at
least be interesting to see how much time I really spend doing what I
think I should be doing. :)

Keyboard shortcuts
==================

Alt-Enter - submit the note. When it pops up and you're still doing
the same thing, just hit Alt-Enter straight away.

Ctrl-Z - put the pervious note's tags in the note. Not your
grand-fathers undo...

Escape - dismiss for now.

Corrections
===========

You can edit HOME/discretetime/notes.utf8.txt and correct it at any
time.

Installation
============

Install Java.

Grab the zip file, unzip somewhere, and double-click or run the
appropriate script for your platform. (E.g. discretetime.bat for
Windows)

discretetime goes and sits in the system tray in Windows and Linux.

Building
========

You need gradle and java 8.

```
    git clone https://github.com/avisagie/discretetime.git
    gradle build
```

Analysis
========

There is at present a very simple and crude method to derive a summary
of your notes.utf8.txt. Unfortunately you need the command line.

The following example is for Windows. Suppose I unzipped the
distribution at C:\dt... Run the following commands

```
    cd C:\dt
    java -cp discretetime-0.5.jar dt.SimpleFilter
```

To see all your :radproject tags, add +:radprojects to the
commandline. To remove all time with :talk, add -:talk.
