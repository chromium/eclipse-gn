eclipse-gn
==========

<a href="https://code.google.com/p/chromium/wiki/gn">GN</a> meta-build language support for the <a href="http://www.eclipse.org">Eclipse IDE</a>.

The project is based on the Dynamic Languages Toolkit (<a href="http://www.eclipse.org/dltk/">DLTK</a>) 5.0 for Eclipse.

##Installation

In your Eclipse instance, go to `Help` | `Install New Software...` and add http://gneditor.murzwin.com/snapshot as a software site. It offers
the GN language support for Eclipse as well as the source code bundles for the project.

##Features

* A source code editor with GN syntax and semantic highlighting
* "Go to definition" for source code elements and labels
* Navigation to imported files and labels (`F3`)
* Context-based autocomplete
* References search

##Known Issues

* The language model is currently per-file rather than per-project. Which means the `import()` statements will not affect the importing file's contents. This is on our radar.

* External variable references (`is_linux` et al.) are not highlighted. This issue is related to the previous one.

* The GN language is not a perfect fit for the DLTK model, so the source code model in the Outline view may look a bit strange.

* Semantic code checks, an important language support feature, are missing.

* Even though a lot of work has been made to ensure proper parser recovery when editing GN code. However there are still many cases where the recovery may fail producing no parse tree at all. Please report such failing cases on the project issue tracker.

##Bugs

This project is in its early alpha stage, and many language features are still unsupported. Also, the GN language itself is still somewhat in flux, so eclipse-gn may often misunderstand the code.

Please report all kinds of issues you encounter at https://github.com/chromium/eclipse-gn/issues/new.

##Third-Party Code

This project makes use of third-party software:

* JFlex (http://jflex.de), a scanner generator for Java(TM), written in Java(TM). Copyright © Gerwin Klein, Steve Rowe, Regis Decamp. All rights reserved.

JFlex is used at build time to generate the GN language scanner used by CUP.

* CUP (http://www2.cs.tum.edu/projects/cup/), an LALR parser generator for Java. Copyright 1996-1999 by Scott Hudson, Frank Flannery, C. Scott Ananian.

CUP is used to generate the GN language parser, and the CUP parser runtime library is bundled with the installed artifacts.

##Building and Contributing

The project makes use of the Maven tycho plugin designed to deal with Eclipse projects. Run `mvn compile` to copy the CUP runtime into the core plugin and generate the GN scanner and parser (or update them once you have modified `gn.jflex` or `gn.cup`.) `mvn package` will generate p2 artifacts in the `org.chromium.gnsupport.site`'s `target` directory.

Of course, your <a href="https://help.github.com/articles/using-pull-requests">pull requests</a> are welcome.
