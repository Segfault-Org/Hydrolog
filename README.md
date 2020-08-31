# Hydrolog

Hydrolog stands for *Hydrogen Blog*, which aims to be the lightest blog program, with simplicity and performance in mind.

Current status: *Indev*. The program is under rapid development and breaking changes may be made.

## Purpose

The initial purpose of Hydrolog is to replace my current blogging program WordPress. The major barriers to master WordPress are:

* Written in PHP, which I do not understand.

* Requires MySQL to run, which is resource consuming.

* Does not match some of my personal ideologies, for example, K.I.S.S and others (I do not want to use JS as well).

* Other complains.

Therefore, Hydrolog may not seem to achieve your goals on blogging. However, along with the future development, it will support more and more modern futures.

## Goals / Features

* Simple: Written in pure Java, without Web frameworks.

* Fast: We are doing our best to achieve the fastest performance.

* Multiple Formats: We support multiple post formats. You can also add your own by extending `IRenderer`.

  * HTML: Directly output to the final HTML.
  
  * Markdown: Automatically convert markdown to HTML.
  
  * Others: Add `<p></p>`.

* "Native": Read post information directly using standard Linux FS metadata (see below)

  * Post title: File name
  
  * Creation Time / Modification Time: From file metadata
  
  * Hidden posts: From file name ("dotfiles")
  
  * Post author: File owner (`UNIX is a multiuser operating system` <sup>TM</sup>)

## TODO

- [ ] Post tags & categories

- [ ] Search by post tags / categories / authors

- [x] Frontend redo (I'm sorry, but I have no knowledge on frontent)

- [x] Add uptime footer widget

- [ ] Support custom second level index pages

- [ ] i18n

## Usage

You can configure it by setting environment variables, which is easy to support systemd:

```
# HTTP Server
http.addr=0.0.0.0 # Bind address
http.port=80 # Listen port

# File Post service
post.file.root=/mnt/your/post/root/dir/ # Posts root dir

# Default HTML Template
html.default.lang=en # HTML lang tag
html.default.title=My blog # Homepage title
html.default.override=. # Velocity override folder
```

# License

GPL v2 only.