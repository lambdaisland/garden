# Garden

<!-- badges -->
[![CircleCI](https://circleci.com/gh/org.clojars.sandudorogan/garden.svg?style=svg)](https://circleci.com/gh/org.clojars.sandudorogan/garden) [![cljdoc badge](https://cljdoc.org/badge/org.clojars.sandudorogan/garden)](https://cljdoc.org/d/org.clojars.sandudorogan/garden) [![Clojars Project](https://img.shields.io/clojars/v/org.clojars.sandudorogan/garden.svg)](https://clojars.org/org.clojars.sandudorogan/garden)
<!-- /badges -->

This repo (`lambdaisland/garden`) is a fork of the original `noprompt/garden`
repo, since the upstream repo had not had a Clojars release for some years, and
a number of fixes and improvements had accumulated. The version found here is
released to Clojars under the `org.clojars.sandudorogan` organization. See below for
installation instructions. Note that currently we still use the same namespace
names as before (e.g. `garden.compiler`), without a `sandudorogan` prefix. This
means you should take care to only have `org.clojars.sandudorogan/garden` on your
classpath, or you may get unexpected results. We may introduce a namespace
prefix in the future.

Originally we just took the upstream code as-is, since PRs had still been
merged, just not released. The main initial change was moving to the same
unified release tooling all lambdaisland libraries use. Since then we've merged
and released a number of PRs that haven't yet been merged upstream. See
`CHANGELOG.md` for release details.

This fork has also been made Babashka-compatible, with the caveat that a few
things are not supported. See the relevant README section.

Also check out [lambdaisland/ornament](https://github.com/lambdaisland/ornament)
for our styled component companion library.

<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
org.clojars.sandudorogan/garden {:mvn/version "1.10.610"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[org.clojars.sandudorogan/garden "1.10.610"]
```
<!-- /installation -->

# Babashka Compatibility

This fork uses `:bb` reader conditionals to provide alternatives for some of the
expressions that aren't supported by Babashka. In particular:

Babashka does not support extending `IFn`, so instances of `CSSSelector` and
`CSSColor` can not be called as functions. This means things like `((s/selector
:*) s/before)` don't work. Use `"*::before"` instead (i.e. just use a string),
or call `s/css-selector` explicitly. You can still use some of the sugar in
`garden.selector` that doesn't depend on `IFn`, e.g. `(s/attr= :type "button")`.

CSS Compression in Garden is delegated to
`com.yahoo.platform.yui.compressor.CssCompressor`. Since that class is not
compiled into bb, there's no way to leverage it. This means you have to keep
`:pretty-print?` on (the default). Someone could create a pod for YUI compressor
if they really wanted, but we don't recommend using it anyway. YUI compressor is
an unmaintained tool that has not kept up with modern CSS developments, and
we've seen it make a mess and cause breaking changes in your CSS.

# Original Garden README (pre lambdaisland fork)

Garden is a library for rendering CSS in Clojure and ClojureScript.
Conceptually similar to [Hiccup](https://github.com/weavejester/hiccup), it uses
vectors to represent rules and maps to represent declarations. It is designed
for stylesheet authors who are interested in what's possible when you trade a
preprocessor for a programming language.

## Table of Contents

* [Getting Started](#getting-started)
* [Syntax](#syntax)
* [Development](#development)
* [Community](#community)
* [Help!](#help)

## Getting Started

Garden 1.2.5 and below requires Clojure 1.6.0 and is known to work with
ClojureScript 0.0-2342. However, starting with Garden 1.3.0 Garden requires
Clojure 1.7 and ClojureScript 1.7.x to leverage a unified syntax with
[reader conditionals](http://dev.clojure.org/display/design/Reader+Conditionals),
and other major changes in the compiler and repl in Clojurescript.

## Syntax

Garden syntax is very similar to
[Hiccup](https://github.com/weavejester/hiccup). If you're familiar with Hiccup
you should feel right at home. If not, don't sweat it!

From your project's root directory start up a new REPL and try the following:

```clojure
user=> (require '[garden.core :refer [css]])
nil
user=> (css [:body {:font-size "16px"}])
"body{font-size:16px}"
```

First you'll notice the use of the `css` function. This function takes an
optional map of compiler flags, any number of rules, and returns a string of
compiled CSS.

Vectors represent rules in CSS. The first _n_ **non-collection** elements of a
vector depict the rule's selector where _n_ > 0. When _n_ = 0 the rule is not
rendered. To produce a rule which selects the `<h1>` and `<h2>` HTML elements
for example, we simply begin a vector with `[:h1 :h2]`:

```clojure
user=> (css [:h1 :h2 {:font-weight "none"}])
"h1,h2{font-weight:none}"
```

To target **child selectors** nested vectors may be employed:

```clojure
user=> (css [:h1 [:a {:text-decoration "none"}]])
"h1 a{text-decoration:none}"
user=> (css [:h1 :h2 [:a {:text-decoration "none"}]])
"h1 a, h2 a{text-decoration:none}"
```

As in Less/Sass, Garden also supports selectors prefixed with the `&`
character allowing you to reference a **parent selector**:

```clojure
user=> (css [:a
             {:font-weight 'normal
              :text-decoration 'none}
             [:&:hover
              {:font-weight 'bold
               :text-decoration 'underline}]])
"a{text-decoration:none;font-weight:normal}a:hover{text-decoration:underline;font-weight:bold}"
```

A slightly more complex example demonstrating nested vectors with multiple
selectors:

```clojure
user=> (css [:h1 :h2 {:font-weight "normal"}
             [:strong :b {:font-weight "bold"}]])
"h1,h2{font-weight:normal}h1 strong,h1 b,h2 strong,h2 b{font-weight:bold}"
```

`garden.selectors` namespace defines a CSSSelector record. It doubles as both a
function and a literal (when passed to the css-selector). When the function is
called it will return a new instance that possesses the same properties. All
arguments to the function must satisfy ICSSSelector.

`garden.selectors` namespace also defines these macros that create a selector
record: `defselector`, `defclass`, `defid`, `defpseudoclass` and
`defpseudoelement`.

`garden.selectors` namespace also defines many CSSSelector instances such as:

* Type selectors `a`, `abbr`, `address` and [more](src/garden/selectors.cljc)
* Pseudo-classes `active`, `checked`, `disabled` and
  [more](src/garden/selectors.cljc)
* Language and negation pseudo-classes `lang` and `not`
* Structural pseudo-classes `nth-child`, `nth-last-child`, `nth-of-type` and
  `nth-last-of-type`
* Pseudo-elements `after`, `before`, `first-letter` and `first-line`
* Attribute selectors `attr=`, `attr-contains`, `attr-starts-with`,
  `attr-starts-with*`, `attr-ends-with` and `attr-matches`
* Combinators `descendant`, `+`, `-` and `>`
* Special selector `&`

and allows to compose complex selectors such as this:

```clojure
(defselector *)
(defpseudoclass host [x] x)
(defpseudoelement content)
(> (host (attr :flipped)) content (* last-child))
;; => :host([flipped]) > ::content > *:last-child
```

`garden.selectors` namespace also defines a CSS3 selectors's `specificity`
function:

```clojure
(specificity "#s12:not(FOO)")
;; => 101
(specificity (a hover))
;; => 10
```

Clojure maps represent CSS declarations where map keys and values represent CSS
properties and values respectively. Garden's declaration syntax is a bit more
involved than rules and understanding it is important to make the most of the
library.

Declaration map keys _should_ either be a string, keyword, or symbol:

```clojure
user=> (css [:h1 {"font-weight" "normal"}])
"h1{font-weight:normal}"
user=> (css [:h1 {:font-weight "normal"}])
"h1{font-weight:normal}"
user=> (css [:h1 {'font-weight "normal"}])
"h1{font-weight:normal}"
```

Be aware, Garden makes no attempt to validate your declarations and
will not raise an error if other key types are used.

```clojure
user=> (css [:h1 {30000 "nom-nom"}])
"h1{30000:nom-nom}"
```

We've already seen strings used as declaration map values, but Garden also
supports keywords, symbols, numbers, maps, vectors, and lists in addition.

##### Custom functions

Since Garden doesn't have wrappers for all the possible CSS functions,
sometimes you might need to define the function you need yourself.
This is where the `defcssfn` macro comes in handy.
Suppose you want to use the `url` CSS function, even if it's not available
in Garden directly you can just define it yourself by simply:

```
(defcssfn url)
;; => #'user/url
```

Which will render like this:

```
(css (url "http://fonts.googleapis.com/css?family=Lato"))
;; => url(http://fonts.googleapis.com/css?family=Lato)
```

##### Strings, keywords, symbols, and numbers

Strings, keywords, symbols, and numbers are rendered as literal CSS values:

```clojure
user=> (css [:body {:font "16px sans-serif"}])
"body{font:16px sans-serif}"
```

Be warned, you must escape literal string values yourself:

```clojure
user=> (css [:pre {:font-family "\"Liberation Mono\", Consolas, monospace"}])
"pre{font-family:\"Liberation Mono\", Consolas, monospace}"

```

## Development

### Leiningen commands

Building ClojureScript

```
lein build-cljs
```

Starting a Node REPL

```
lein node-repl
```

Run Clojure tests, along with a test runner

```
lein test-clj
```

Run ClojureScript tests (on Node)

```
lein test-cljs
```

Run both Clojure _and_ ClojureScript tests

```
lein test-cljc
```

## Further Reading & Wiki

Detailed documentation and a developer guide for Syntax, Rules, Declarations,
and Plugins is under the community-contributed
[wiki](https://github.com/noprompt/garden/wiki).

Please contribute!

## Help

This project is looking for team members who can help this project succeed!
Specifically of interest are people who can

* help fix bugs,
* answer questions,
* merge pull requests, and
* deploy new versions.

If you are interested in becoming a team member please open an issue and direct
message @noprompt, or direct message @noprompt on
[Clojurians](https://clojurians.slack.com).

The original author, @noprompt, is a busy person with a family, a job, and
other projects. Be aware that it may take some time for pull requests to be
evaluated.

## Community

### Mailing List

* [Google Groups](https://groups.google.com/forum/#!forum/garden-clojure)

### Slack

* #clojurescript or #css channel on [Clojurians](https://clojurians.slack.com)

## License

Copyright © 2013-2019 Joel Holdbrooks.

Distributed under the Eclipse Public License, the same as Clojure.