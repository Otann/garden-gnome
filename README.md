# Garden code watcher

<img width="30%"
     align="right" padding="5px"
     alt=":)"
     src="https://raw.githubusercontent.com/Otann/garden-gnome/master/resources/gnome.jpeg?raw=true"/>

Components to watch Garden sources from REPL for reloaded workflow.

In combination with [Figwheel][figwheel] gives you instant sync between your
[Garden][garden] sources and page in the browser

## Installation

Add garden-gnome as a dependency in project.clj (Leiningen) or build.boot (Boot).

`[garden-gnome "0.1.0"]`

## Usage

### Configuration

Add dependency to your `project.clj`:

```clojure
:profiles {:dev {:dependencies [[garden-gnome "0.1.0"]]}}
```

Then say you have a namespace with your styles defined:

```clojure
(ns sample.styles
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:background-color "black"}])
```

Add following configuration in your `project.clj` (same format as [lein-garden][lein-garden])

```clojure
:garden {:builds [{;; Source path where to watch for changes:
                   :source-path "dev/sample"]
                   ;; The var containing your stylesheet:
                   :stylesheets [sample.styles/screen]
                   ;; Compiler flags passed to `garden.core/css`:
                   :compiler     {:output-to     "resources/public/css/screen.css"
                                  :pretty-print? true}}]}
```

### One-time compilation

Use following command to compile all your configurations

```sh
$ lein run -m garden-gnome.compile
```

### Watching for changes in repl or reloaded workflow  

In your `user.clj`:

#### For Mount 


```clojure
(ns user
  (:require [mount.core :as mount]
            [garden-gnome.watcher :as garden-watcher]))

(mount/defstate garden
  :start (garden-watcher/start! (garden-watcher/default-config))
  :stop (garden-watcher/stop! garden))
```

### For Component

```clojure
(ns user
  (:require [com.stuartsierra.component :as component]
            [garden-gnome.watcher :as garden-gnome]))

(defrecord Gnome []
  component/Lifecycle
  (start [this]
    (let [config  (garden-gnome/default-config)
          watcher (garden-gnome/start! config)]
      (assoc this :watcher watcher)))
  (stop [this]
    (let [watcher (:watcher this)]
      (garden-gnome/stop! watcher)
      (dissoc this :watcher))))
```

Now in your REPL whenever you start you system, a watcher will start which
will observe changes in directories specified in your garden config and automatically
recompile mentioned namespaces whenever files change.

If you have [Figwheel][figwheel] running, it will pick your changes automatically,
so will have a closed loop from editing garden code to seeing changes in your browser instantly.

## Credits

Inspired by [plexus/garden-watcher](https://github.com/plexus/garden-watcher) 
and adapted to use with mount instead of [lein-garden][lein-garden].

## License

Copyright © 2017 Anton Chebotaev

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[lein-garden]: https://github.com/noprompt/lein-garden
[figwheel]: https://github.com/bhauman/lein-figwheel
[garden]: https://github.com/noprompt/garden