# Garden reloader mount component

<img width="30%"
     align="right" padding="5px"
     alt=":)"
     src="https://raw.githubusercontent.com/Otann/garden-gnome/master/resources/gnome.jpeg?raw=true"/>

Helper to define your own component to watch Garden sources from repl.

Inspired by [plexus/garden-watcher](https://github.com/plexus/garden-watcher) 
and adapted to use with mount instead of [lein-garden][lein-garden].

## Installation

Add garden-mount as a dependency in project.clj (Leiningen) or build.boot (Boot).

`[garden-mount "0.1.0"]`

## Usage

Configuration follows [lein-garden][lein-garden]:

Say you have a file with your styles `sample.styles/screen`:

```clojure
(ns sample.styles
  (:require [garden.def :as garden]))

(garden/defstyles screen
  [:body {:background-color "black"}])
```

And you have following configuration in your `project.clj`

```clojure
:garden {:builds [{;; Source path where to watch for changes:
                   :source-paths ["dev/sample"]
                   ;; The var containing your stylesheet:
                   :stylesheet   sample.styles/screen
                   ;; Compiler flags passed to `garden.core/css`:
                   :compiler     {:output-to     "resources/public/css/screen.css"
                                  :pretty-print? true}}]}
```

Then in your `user.clj` just add new mount component:

```clojure
(ns user
  (:require [mount.core :as mount]
            [garden-mount.watcher :as garden-watcher]
            [clojure.tools.namespace.repl :as ns-tools]))

(mount/defstate garden
  :start (garden-watcher/start! (garden-watcher/default-config))
  :stop (garden-watcher/stop! garden))
```

Now in your REPL whenever you start you mount system, a watcher will start which 

## License

Copyright © 2017 Anton Chebotaev

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[lein-garden]: https://github.com/noprompt/lein-garden