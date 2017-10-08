(defproject garden-mount "0.1.0"
  :description "Provides support for watching Garden styles in reloaded workflow"
  :url "https://github.com/Otann/garden-gnome"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.classpath "0.2.3"]
                 [garden "1.3.2"]
                 [hawk "0.2.11"]]

  :main ^:skip-aot garden-mount.main

  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths ["dev" "src"]
                       :dependencies [[mount "0.1.11"]
                                      [com.stuartsierra/component "0.3.2"]
                                      [org.clojure/tools.nrepl "0.2.13"]
                                      [org.clojure/tools.namespace "0.2.11"]]}}

  :repl-options {:init-ns user}
  :garden {:builds [{:source-path "dev/sample"
                     :stylesheet  sample.styles/screen
                     :compiler    {:output-to     "resources/public/css/screen.css"
                                   :pretty-print? true}}]}

  ;; Artifact deployment info
  :scm {:name "git"
        :url  "https://github.com/otann/morse"}

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]]

  :pom-addition [:developers [:developer
                              [:name "Anton Chebotaev"]
                              [:url "http://otann.com"]
                              [:email "anton.chebotaev@gmail.com"]
                              [:timezone "+1"]]])
